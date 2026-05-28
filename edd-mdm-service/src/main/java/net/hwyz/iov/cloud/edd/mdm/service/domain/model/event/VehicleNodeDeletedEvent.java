package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车载节点删除事件（EEAD 子域）
 * <p>
 * 推送至 Kafka topic：mdm.eead.vehicleNode.event，eventType=VehicleNodeDeleted。
 * payload 携带删除前最后一份完整字段 + forceDelete 标识（design §8 OQ-9）。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeDeletedEvent {

    private String eventId;
    private String eventType;
    private Date occurredAt;
    private String entityId;
    private Integer version;
    private Object payload;

    /**
     * force 旁路删除标识：true 表示由 MDM-Admin 通过 mdm:eead:vehicleNode:remove:force 旁路点强删
     * （绕过反查 VMD VehiclePart 的 fail-safe 校验，下游可基于此标记额外告警）
     */
    private Boolean forceDelete;
}
