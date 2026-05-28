package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VmdPlantRefService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefSampleResponse;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.PlantDownstreamLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantReferenceCheckResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Plant 下游引用反查网关实现（Org 子域）
 * <p>
 * 组合 {@link VmdPlantRefService} 完成领域层抽象 {@link PlantDownstreamLookupGateway} 契约。
 * VMD-CR-014 落地前，VMD 侧无此接口，FallbackFactory 返回 referenceCount=-1 触发 fail-safe。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlantDownstreamLookupGatewayImpl implements PlantDownstreamLookupGateway {

    private static final String REFERENCING_SERVICE = "edd-vmd";

    private final VmdPlantRefService vmdPlantRefService;

    @Override
    public PlantReferenceCheckResult checkReferences(String plantCode, int sampleLimit) {
        PlantRefCountResponse countResp;
        try {
            countResp = vmdPlantRefService.countByPlantCode(plantCode);
        } catch (Exception e) {
            log.error("VMD Plant Ref countByPlantCode 调用异常 plantCode={}（fail-safe）", plantCode, e);
            return PlantReferenceCheckResult.unavailable(REFERENCING_SERVICE);
        }

        if (countResp == null || countResp.getReferenceCount() < 0) {
            return PlantReferenceCheckResult.unavailable(REFERENCING_SERVICE);
        }

        long referenceCount = countResp.getReferenceCount();

        List<String> samples = Collections.emptyList();
        if (referenceCount > 0) {
            try {
                PlantRefSampleResponse listResp = vmdPlantRefService.listByPlantCode(plantCode, sampleLimit);
                if (listResp != null && listResp.getTotalCount() >= 0 && listResp.getSamples() != null) {
                    samples = listResp.getSamples();
                }
            } catch (Exception e) {
                log.warn("VMD Plant Ref listByPlantCode 调用异常 plantCode={}，使用空样本继续", plantCode, e);
            }
        }

        return PlantReferenceCheckResult.builder()
                .referencingService(REFERENCING_SERVICE)
                .referenceCount(referenceCount)
                .samples(samples)
                .available(true)
                .build();
    }
}
