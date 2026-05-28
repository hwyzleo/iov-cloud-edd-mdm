package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车载节点更新事件（EEAD 子域）
 * <p>
 * 推送至 Kafka topic：mdm.eead.vehicleNode.event，eventType=VehicleNodeUpdated
 * 失效（deactivate）操作也使用本事件，通过 payload.status=INACTIVE 体现失效语义。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeUpdatedEvent {

    private String eventId;
    private String eventType;
    private Date occurredAt;
    private String entityId;
    private Integer version;
    private Object payload;
}
