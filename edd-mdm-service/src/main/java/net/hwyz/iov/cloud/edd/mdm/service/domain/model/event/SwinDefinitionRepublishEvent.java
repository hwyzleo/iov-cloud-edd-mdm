package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN定义补发事件（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-131/US-132）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionRepublishEvent {

    private String eventId;
    private String eventType;
    private Date occurredAt;
    private String entityId;
    private Integer version;
    private Object payload;
    private Boolean republish;
    private String republishBatchId;
}
