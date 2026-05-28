package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VmdPlantRefService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefSampleResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * VMD Plant 引用反查 Feign 降级工厂
 * <p>
 * 降级时返回 referenceCount=-1 标识不可用，由 PlantDeletionDomainService 触发 fail-safe。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class VmdPlantRefServiceFallbackFactory implements FallbackFactory<VmdPlantRefService> {

    @Override
    public VmdPlantRefService create(Throwable cause) {
        log.error("VMD Plant Ref Feign 调用失败，触发 fail-safe 降级", cause);
        return new VmdPlantRefService() {
            @Override
            public PlantRefCountResponse countByPlantCode(String plantCode) {
                return PlantRefCountResponse.builder()
                        .referenceCount(-1L)
                        .build();
            }

            @Override
            public PlantRefSampleResponse listByPlantCode(String plantCode, Integer limit) {
                return null;
            }
        };
    }
}
