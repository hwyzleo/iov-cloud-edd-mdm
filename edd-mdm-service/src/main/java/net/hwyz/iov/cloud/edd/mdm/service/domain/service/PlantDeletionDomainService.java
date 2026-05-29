package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PlantHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.PlantDownstreamLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlantRepository;
import org.springframework.stereotype.Service;

/**
 * 工厂删除领域服务（Org 子域）
 * <p>
 * 沿用 VehicleNodeDeletionDomainService 的反查 + 硬拒绝模式（design §4 F13 / requirements US-054）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlantDeletionDomainService {

    private static final int SAMPLE_LIMIT = 10;

    private final PlantRepository plantRepository;
    private final PlantDownstreamLookupGateway plantDownstreamLookupGateway;

    public void checkAndDelete(Plant plant, String operator, boolean forceDelete) {
        if (plant.getStatus() == PlantStatus.DRAFT) {
            log.info("Plant 删除：DRAFT 直接物理删除 code={} operator={}", plant.getCode(), operator);
            doDelete(plant, operator, false);
            return;
        }

        if (forceDelete) {
            log.warn("Plant 删除：MDM-Admin force 旁路 code={} status={} operator={}",
                    plant.getCode(), plant.getStatus(), operator);
            doDelete(plant, operator, true);
            return;
        }

        PlantReferenceCheckResult result =
                plantDownstreamLookupGateway.checkReferences(plant.getCode(), SAMPLE_LIMIT);

        if (!result.isAvailable()) {
            log.warn("Plant 删除：VMD 反查不可用，fail-safe 拒绝 code={} operator={}",
                    plant.getCode(), operator);
            throw new VmdServiceUnavailableException(
                    String.format("VMD 服务不可用，无法确认 code=%s 的下游引用，删除被拒绝；"
                            + "请稍后重试或联系 MDM-Admin 通过 force 旁路删除", plant.getCode()));
        }

        if (result.hasReference()) {
            log.warn("Plant 删除：存在下游引用，拒绝 code={} referenceCount={} operator={}",
                    plant.getCode(), result.getReferenceCount(), operator);
            throw new PlantHasDownstreamRefException(
                    plant.getCode(),
                    result.getReferencingService(),
                    result.getReferenceCount(),
                    result.getSamples()
            );
        }

        log.info("Plant 删除：反查无引用，物理删除 code={} status={} operator={}",
                plant.getCode(), plant.getStatus(), operator);
        doDelete(plant, operator, false);
    }

    private void doDelete(Plant plant, String operator, boolean forceDelete) {
        plant.markAsDeleting(operator);
        plantRepository.delete(plant, operator, forceDelete);
    }
}
