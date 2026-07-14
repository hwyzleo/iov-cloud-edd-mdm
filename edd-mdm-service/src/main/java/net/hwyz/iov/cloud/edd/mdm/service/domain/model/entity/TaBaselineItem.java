package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 型式批准基线项（聚合内子实体，随父 TypeApprovalBaseline 版本化）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineItem {

    private Long id;
    private Long taBaselineId;
    private String vehicleNodeCode;
    private String partCode;
    private String approvedVersion;
    private String sourceBaselineCode;
    private String createBy;
    private Date createTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 创建新的型式批准基线项
     *
     * @param taBaselineId       所属基线 ID
     * @param vehicleNodeCode    车载节点代码
     * @param partCode           零件代码
     * @param approvedVersion    型批基准版本
     * @param sourceBaselineCode 来源 SoftwareBaseline code
     * @param createBy           创建人
     * @return 新创建的 TaBaselineItem 实例
     */
    public static TaBaselineItem create(Long taBaselineId, String vehicleNodeCode, String partCode,
                                         String approvedVersion, String sourceBaselineCode, String createBy) {
        Date now = new Date();
        return TaBaselineItem.builder()
                .taBaselineId(taBaselineId)
                .vehicleNodeCode(vehicleNodeCode)
                .partCode(partCode)
                .approvedVersion(approvedVersion)
                .sourceBaselineCode(sourceBaselineCode)
                .createBy(createBy)
                .createTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
    }
}
