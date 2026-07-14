package net.hwyz.iov.cloud.edd.mdm.service.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 型式批准基线创建事件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeApprovalBaselineCreatedEvent {

    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private Date timestamp;
    private String operator;

    private String taBaselineCode;
    private String swinCode;
    private String anchorType;
    private String anchorCode;
    private String status;
    private String projectionDigest;
    private String sourceBaselineScope;
    private Integer version;

    private List<TaBaselineItemEvent> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaBaselineItemEvent {
        private String vehicleNodeCode;
        private String partCode;
        private String approvedVersion;
        private String sourceBaselineCode;
    }
}
