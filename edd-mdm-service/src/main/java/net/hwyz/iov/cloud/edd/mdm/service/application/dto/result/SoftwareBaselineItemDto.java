package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineItemDto {
    private Long id;
    private String baselineCode;
    private String partCode;
    private String vehicleNodeCode;
    private String remark;
    private String createBy;
    private java.util.Date createTime;
    private String modifyBy;
    private java.util.Date modifyTime;
}
