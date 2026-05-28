package net.hwyz.iov.cloud.edd.mdm.service.domain.gateway;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehiclePartReferenceCheckResult;

/**
 * VMD VehiclePart 反向查询网关（EEAD 子域）
 * <p>
 * 领域层抽象"反查 VMD 是否有引用 nodeCode 的 source=MDM 副本"的能力，由 infrastructure 层基于 Feign 实现。
 * 设计参考：design §1 EEAD 包结构 / §4 F11 流程 / §5.6 跨服务依赖。
 *
 * @author hwyz_leo
 */
public interface VehiclePartReverseLookupGateway {

    /**
     * 反查指定 nodeCode 的 VMD VehiclePart 引用情况。
     * <p>
     * 实现层 SHALL 提供 fail-safe 行为：
     * 当 Feign 调用失败 / 超时 / 降级时，SHALL 返回 available=false 的结果而非抛异常，由调用方决定如何处理。
     *
     * @param nodeCode      待删除的车载节点 nodeCode
     * @param sampleLimit   引用样本上限（建议 10）
     * @return 反查结果（含 referenceCount / samples / available 标识）
     */
    VehiclePartReferenceCheckResult checkReferences(String nodeCode, int sampleLimit);
}
