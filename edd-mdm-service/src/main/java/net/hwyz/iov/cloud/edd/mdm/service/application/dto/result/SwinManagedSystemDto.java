package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN管理软件系统DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinManagedSystemDto {
    private Long id;
    private String swinCode;
    private String vehicleNodeCode;
    private Boolean isTypeApprovalRelevant;
    private String approvedSoftwareBaseline;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
}
