package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import org.springframework.stereotype.Service;

/**
 * SWIN定义补发领域服务（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-131/US-132）
 * <p>
 * 以 SwinDefinitionUpdated + 当前全量快照（含 managedSystems）重发。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwinDefinitionRepublishDomainService {

    /**
     * 解析事件类型
     * <p>
     * SWIN定义无 released/frozen 生命周期，统一以 SwinDefinitionUpdated 为 eventType。
     *
     * @return 事件类型
     */
    public String resolveEventType() {
        return "SwinDefinitionUpdated";
    }

    /**
     * 构建补发事件的全量快照payload
     * <p>
     * 直接返回SWIN定义聚合根（含managedSystems），与CR-026 §5事件payload结构一致。
     *
     * @param swinDefinition SWIN定义聚合根
     * @return 全量快照payload
     */
    public SwinDefinition buildPayload(SwinDefinition swinDefinition) {
        return swinDefinition;
    }
}
