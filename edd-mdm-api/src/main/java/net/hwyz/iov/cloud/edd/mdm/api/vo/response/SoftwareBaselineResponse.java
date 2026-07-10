package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineResponse {
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
    private List<SoftwareBaselineItemResponse> items;
}
