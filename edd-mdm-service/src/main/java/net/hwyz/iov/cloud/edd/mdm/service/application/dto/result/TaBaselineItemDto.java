package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * TA基线项DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineItemDto {

    private Long id;
    private Long taBaselineId;
    private String vehicleNodeCode;
    private String partCode;
    private String approvedVersion;
    private String sourceBaselineCode;
    private String createBy;
    private Date createTime;
}
