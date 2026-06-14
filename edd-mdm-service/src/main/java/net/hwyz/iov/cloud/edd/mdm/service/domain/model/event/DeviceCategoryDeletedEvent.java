package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 设备类别删除事件（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryDeletedEvent {
    private String eventId;
    private String eventType;
    private Date occurredAt;
    private String entityId;
    private Integer version;
    private Object payload;
    private boolean forceDelete;
}
