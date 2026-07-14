package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TaBaselineStatus;
import org.springframework.stereotype.Service;

/**
 * TA基线补发领域服务（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-129/US-130）
 * <p>
 * 按当前 status 复用既有事件类型，构建当前全量快照 payload。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaBaselineRepublishDomainService {

    /**
     * 根据TA基线状态解析事件类型
     *
     * @param status TA基线状态
     * @return 事件类型
     */
    public String resolveEventType(TaBaselineStatus status) {
        if (status == null) {
            return "TypeApprovalBaselineCreated";
        }
        return switch (status) {
            case DRAFT -> "TypeApprovalBaselineCreated";
            case RELEASED -> "TypeApprovalBaselineReleased";
            case FROZEN -> "TypeApprovalBaselineFrozen";
        };
    }

    /**
     * 构建补发事件的全量快照payload
     * <p>
     * 直接返回TA基线聚合根（含items），与CR-030 §6事件payload结构一致。
     *
     * @param baseline TA基线聚合根
     * @return 全量快照payload
     */
    public TypeApprovalBaseline buildPayload(TypeApprovalBaseline baseline) {
        return baseline;
    }
}
