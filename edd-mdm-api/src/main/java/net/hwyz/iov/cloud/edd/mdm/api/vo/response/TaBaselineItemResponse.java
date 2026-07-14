package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * TA基线项响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineItemResponse {

    private Long id;
    private Long taBaselineId;
    private String vehicleNodeCode;
    private String partCode;
    private String approvedVersion;
    private String sourceBaselineCode;
    private String createBy;
    private Date createTime;
}
