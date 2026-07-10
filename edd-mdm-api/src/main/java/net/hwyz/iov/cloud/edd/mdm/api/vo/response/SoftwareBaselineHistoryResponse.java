package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 软件基线历史版本响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineHistoryResponse {

    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;
    private String code;
    private String name;
    private String anchorType;
    private String anchorCode;
    private String baselineVersion;
    private String baselineStatus;
    private Date releasedAt;
    private String releasedBy;
    private String supersededByCode;
    private String description;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private Boolean forceDelete;
    private List<SoftwareBaselineItemResponse> itemsSnapshot;
}
