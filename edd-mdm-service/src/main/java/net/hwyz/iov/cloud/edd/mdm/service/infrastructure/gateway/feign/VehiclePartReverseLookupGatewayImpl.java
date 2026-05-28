package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VmdVehiclePartService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartListResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartSampleVO;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.VehiclePartReverseLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehiclePartReferenceCheckResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * VMD VehiclePart 反向查询网关实现（EEAD 子域）
 * <p>
 * 组合 {@link VmdVehiclePartService} 完成领域层抽象 {@link VehiclePartReverseLookupGateway} 契约。
 * fail-safe 行为：当 Feign 返回 referenceCount=-1（FallbackFactory 标识）时返回 unavailable 结果。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VehiclePartReverseLookupGatewayImpl implements VehiclePartReverseLookupGateway {

    /**
     * 引用方服务名（首期范围：edd-vmd VehiclePart）
     */
    private static final String REFERENCING_SERVICE = "edd-vmd:VehiclePart";

    private final VmdVehiclePartService vmdVehiclePartService;

    @Override
    public VehiclePartReferenceCheckResult checkReferences(String nodeCode, int sampleLimit) {
        // 第 1 步：轻量计数
        VehiclePartCountResponse countResp;
        try {
            countResp = vmdVehiclePartService.countByNodeCode(nodeCode);
        } catch (Exception e) {
            log.error("VMD VehiclePart countByNodeCode 调用异常 nodeCode={}（fail-safe）", nodeCode, e);
            return VehiclePartReferenceCheckResult.unavailable(REFERENCING_SERVICE);
        }

        if (countResp == null || countResp.getReferenceCount() < 0) {
            // FallbackFactory 已触发（referenceCount=-1）
            return VehiclePartReferenceCheckResult.unavailable(REFERENCING_SERVICE);
        }

        long referenceCount = countResp.getReferenceCount();

        // 第 2 步：count > 0 才拉样本（性能优化，无引用直接构造空样本）
        List<String> samples = Collections.emptyList();
        if (referenceCount > 0) {
            try {
                VehiclePartListResponse listResp = vmdVehiclePartService.listByNodeCode(nodeCode, sampleLimit);
                if (listResp != null && listResp.getTotalCount() >= 0 && listResp.getSamples() != null) {
                    samples = listResp.getSamples().stream()
                            .map(VehiclePartReverseLookupGatewayImpl::formatSample)
                            .collect(Collectors.toList());
                } else {
                    log.warn("VMD VehiclePart listByNodeCode 返回降级标识 nodeCode={}，使用空样本继续", nodeCode);
                }
            } catch (Exception e) {
                // 计数已成功，仅样本失败时不视为反查不可用，使用空样本继续
                log.warn("VMD VehiclePart listByNodeCode 调用异常 nodeCode={}，使用空样本继续", nodeCode, e);
            }
        }

        return VehiclePartReferenceCheckResult.builder()
                .referencingService(REFERENCING_SERVICE)
                .referenceCount(referenceCount)
                .samples(samples)
                .available(true)
                .build();
    }

    private static String formatSample(VehiclePartSampleVO sample) {
        return String.format("partCode=%s,vin=%s",
                sample.getPartCode(),
                sample.getVehicleVin() != null ? sample.getVehicleVin() : "-");
    }
}
