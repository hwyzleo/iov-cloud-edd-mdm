package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VehicleNodeHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.VehiclePartReverseLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehicleNodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehiclePartReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import org.springframework.stereotype.Service;

/**
 * 车载节点删除领域服务（EEAD 子域）
 * <p>
 * 编排删除前置依赖检查的 5 个分支（design §4 F11 / requirements US-045）：
 * <ol>
 *   <li>DRAFT 状态：直接物理删除（下游尚未感知）</li>
 *   <li>ACTIVE/INACTIVE 状态 + forceDelete=false + 反查无引用：物理删除</li>
 *   <li>ACTIVE/INACTIVE 状态 + forceDelete=false + 反查有引用：抛 {@link VehicleNodeHasDownstreamRefException}（812003）</li>
 *   <li>ACTIVE/INACTIVE 状态 + forceDelete=false + 反查不可用（Feign 失败）：fail-safe 抛 {@link VmdServiceUnavailableException}</li>
 *   <li>ACTIVE/INACTIVE 状态 + forceDelete=true：跳过反查直接物理删除（旁路路径，需 mdm:eead:vehicleNode:remove:force 权限）</li>
 * </ol>
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleNodeDeletionDomainService {

    /**
     * 反查样本上限：拒绝删除时附在错误响应中
     */
    private static final int SAMPLE_LIMIT = 10;

    private final VehicleNodeRepository vehicleNodeRepository;
    private final VehiclePartReverseLookupGateway vehiclePartReverseLookupGateway;

    /**
     * 检查并执行物理删除。
     *
     * @param node        待删除车载节点（聚合根）
     * @param operator    操作人
     * @param forceDelete 是否走 MDM-Admin force 旁路路径
     * @throws VehicleNodeHasDownstreamRefException 存在下游引用时抛出（812003）
     * @throws VmdServiceUnavailableException       VMD 反查不可用时抛出（fail-safe 路径）
     */
    public void checkAndDelete(VehicleNode node, String operator, boolean forceDelete) {
        if (node.getStatus() == VehicleNodeStatus.DRAFT) {
            log.info("VehicleNode 删除：DRAFT 直接物理删除 nodeCode={} operator={}", node.getNodeCode(), operator);
            doDelete(node, operator, false);
            return;
        }

        if (forceDelete) {
            log.warn("VehicleNode 删除：MDM-Admin force 旁路 nodeCode={} status={} operator={}",
                    node.getNodeCode(), node.getStatus(), operator);
            doDelete(node, operator, true);
            return;
        }

        VehiclePartReferenceCheckResult result =
                vehiclePartReverseLookupGateway.checkReferences(node.getNodeCode(), SAMPLE_LIMIT);

        if (!result.isAvailable()) {
            log.warn("VehicleNode 删除：VMD 反查不可用，fail-safe 拒绝 nodeCode={} operator={}",
                    node.getNodeCode(), operator);
            throw new VmdServiceUnavailableException(
                    String.format("VMD 服务不可用，无法确认 nodeCode=%s 的下游引用，删除被拒绝；"
                            + "请稍后重试或联系 MDM-Admin 通过 force 旁路删除", node.getNodeCode()));
        }

        if (result.hasReference()) {
            log.warn("VehicleNode 删除：存在下游引用，拒绝 nodeCode={} referenceCount={} operator={}",
                    node.getNodeCode(), result.getReferenceCount(), operator);
            throw new VehicleNodeHasDownstreamRefException(
                    node.getNodeCode(),
                    result.getReferencingService(),
                    result.getReferenceCount(),
                    result.getSamples()
            );
        }

        log.info("VehicleNode 删除：反查无引用，物理删除 nodeCode={} status={} operator={}",
                node.getNodeCode(), node.getStatus(), operator);
        doDelete(node, operator, false);
    }

    private void doDelete(VehicleNode node, String operator, boolean forceDelete) {
        node.markAsDeleting(operator);
        vehicleNodeRepository.delete(node, operator, forceDelete);
    }
}
