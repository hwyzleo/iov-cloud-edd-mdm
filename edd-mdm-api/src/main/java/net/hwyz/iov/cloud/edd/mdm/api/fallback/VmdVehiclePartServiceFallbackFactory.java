package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VmdVehiclePartService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartListResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * VMD VehiclePart 反向查询 Feign 降级工厂
 * <p>
 * fail-safe 策略：返回 referenceCount=-1 标识"反查不可用"，
 * 由 VehiclePartReverseLookupGatewayImpl 转换为 unavailable 结果，
 * 进而由 VehicleNodeDeletionDomainService 拒绝删除。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class VmdVehiclePartServiceFallbackFactory implements FallbackFactory<VmdVehiclePartService> {

    @Override
    public VmdVehiclePartService create(Throwable throwable) {
        return new VmdVehiclePartService() {
            @Override
            public VehiclePartCountResponse countByNodeCode(String nodeCode) {
                log.error("VMD VehiclePart 反查计数失败 nodeCode={}（fail-safe 降级）", nodeCode, throwable);
                return VehiclePartCountResponse.builder()
                        .nodeCode(nodeCode)
                        .referenceCount(-1L)
                        .sourceFilter("MDM")
                        .build();
            }

            @Override
            public VehiclePartListResponse listByNodeCode(String nodeCode, Integer limit) {
                log.error("VMD VehiclePart 反查样本失败 nodeCode={} limit={}（fail-safe 降级）",
                        nodeCode, limit, throwable);
                return VehiclePartListResponse.builder()
                        .nodeCode(nodeCode)
                        .totalCount(-1L)
                        .samples(Collections.emptyList())
                        .build();
            }
        };
    }
}
