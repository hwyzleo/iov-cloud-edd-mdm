package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.OtaBaselineLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaBaselineReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import org.springframework.stereotype.Service;

/**
 * 软件基线删除领域服务（Material 子域）
 * <p>
 * 沿用 PlantDeletionDomainService 的反查 + 硬拒绝模式（design §3 F23 / requirements US-109）
 * DRAFT 直删（含基线项子项）；RELEASED/SUPERSEDED 须反查下游 OTA 投影引用，存在则硬拒绝（812938）。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoftwareBaselineDeletionDomainService {

    private static final int SAMPLE_LIMIT = 10;

    private final SoftwareBaselineRepository softwareBaselineRepository;
    private final OtaBaselineLookupGateway otaBaselineLookupGateway;

    public void checkAndDelete(SoftwareBaseline baseline, String operator, boolean forceDelete) {
        if (baseline.getBaselineStatus() == BaselineStatus.DRAFT) {
            log.info("SoftwareBaseline 删除：DRAFT 直接物理删除 code={} operator={}", baseline.getCode(), operator);
            doDelete(baseline, operator, false);
            return;
        }

        if (forceDelete) {
            log.warn("SoftwareBaseline 删除：MDM-Admin force 旁路 code={} status={} operator={}",
                    baseline.getCode(), baseline.getBaselineStatus(), operator);
            doDelete(baseline, operator, true);
            return;
        }

        OtaBaselineReferenceCheckResult result =
                otaBaselineLookupGateway.checkReferences(baseline.getCode(), SAMPLE_LIMIT);

        if (!result.isAvailable()) {
            log.warn("SoftwareBaseline 删除：OTA 反查不可用，fail-safe 拒绝 code={} operator={}",
                    baseline.getCode(), operator);
            throw new VmdServiceUnavailableException(
                    String.format("OTA 服务不可用，无法确认 code=%s 的下游引用，删除被拒绝；"
                            + "请稍后重试或联系 MDM-Admin 通过 force 旁路删除", baseline.getCode()));
        }

        if (result.hasReference()) {
            log.warn("SoftwareBaseline 删除：存在下游引用，拒绝 code={} referenceCount={} operator={}",
                    baseline.getCode(), result.getReferenceCount(), operator);
            throw new SoftwareBaselineHasDownstreamRefException(baseline.getCode(), result.getReferenceCount());
        }

        log.info("SoftwareBaseline 删除：反查无引用，物理删除 code={} status={} operator={}",
                baseline.getCode(), baseline.getBaselineStatus(), operator);
        doDelete(baseline, operator, false);
    }

    private void doDelete(SoftwareBaseline baseline, String operator, boolean forceDelete) {
        baseline.markAsDeleting(operator);
        softwareBaselineRepository.delete(baseline, operator, forceDelete);
    }
}
