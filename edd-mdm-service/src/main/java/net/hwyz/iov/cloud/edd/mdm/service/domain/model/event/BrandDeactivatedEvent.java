package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 品牌失效事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandDeactivatedEvent {

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件发生时间
     */
    private Date occurredAt;

    /**
     * 实体ID（code）
     */
    private String entityId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 事件载荷
     */
    private Object payload;
}
