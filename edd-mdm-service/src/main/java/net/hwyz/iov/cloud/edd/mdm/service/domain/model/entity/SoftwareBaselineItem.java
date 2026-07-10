package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 软件基线项（聚合内子实体，随父 SoftwareBaseline 版本化）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineItem {

    private Long id;
    private String baselineCode;
    private String partCode;
    private String vehicleNodeCode;
    private String remark;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    public static SoftwareBaselineItem create(String baselineCode, String partCode,
                                               String vehicleNodeCode, String remark,
                                               String createBy) {
        Date now = new Date();
        return SoftwareBaselineItem.builder()
                .baselineCode(baselineCode)
                .partCode(partCode)
                .vehicleNodeCode(vehicleNodeCode)
                .remark(remark)
                .createBy(createBy)
                .createTime(now)
                .modifyBy(createBy)
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
    }
}
