package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinRegistryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.TypeApprovalBaselineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TA基线删除领域服务
 * <p>
 * 负责删除反查保护：
 * 1. RxswinRegistryRepository.countBySoftwareBaselineCode(taBaselineCode) 或等价同库检查
 * 2. SwinManagedSystemRepository 反查
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeApprovalBaselineDeletionDomainService {

    private final TypeApprovalBaselineRepository typeApprovalBaselineRepository;
    private final RxswinRegistryRepository rxswinRegistryRepository;
    private final SwinManagedSystemRepository swinManagedSystemRepository;

    /**
     * 检查并删除TA基线
     *
     * @param baseline   TA基线
     * @param operator   操作人
     * @param forceDelete 是否强制删除
     */
    public void checkAndDelete(TypeApprovalBaseline baseline, String operator, boolean forceDelete) {
        log.info("检查TA基线删除: code={}, forceDelete={}", baseline.getTaBaselineCode(), forceDelete);

        // 1. 检查 RxswinRegistry 引用
        long rxswinRefCount = rxswinRegistryRepository.countBySoftwareBaselineCode(baseline.getTaBaselineCode());
        if (rxswinRefCount > 0) {
            if (!forceDelete) {
                throw new TaBaselineHasReferenceException(baseline.getTaBaselineCode(), "RxswinRegistry");
            }
            log.warn("TA基线被RxswinRegistry引用，强制删除: code={}, refCount={}", baseline.getTaBaselineCode(), rxswinRefCount);
        }

        // 2. 检查 SwinManagedSystem 引用（通过 approved_software_baseline 字段）
        // 注意：当前 SwinManagedSystem 的 approved_software_baseline 字段已降级为来源标注，
        // 但仍需检查是否有引用
        List<String> swinCodes = swinManagedSystemRepository.findSwinCodesByVehicleNodeCode(baseline.getAnchorCode());
        if (!swinCodes.isEmpty() && swinCodes.contains(baseline.getSwinCode())) {
            if (!forceDelete) {
                throw new TaBaselineHasReferenceException(baseline.getTaBaselineCode(), "SwinManagedSystem");
            }
            log.warn("TA基线被SwinManagedSystem引用，强制删除: code={}", baseline.getTaBaselineCode());
        }

        // 3. 执行删除
        typeApprovalBaselineRepository.delete(baseline, operator);
        log.info("TA基线删除成功: code={}", baseline.getTaBaselineCode());
    }
}
