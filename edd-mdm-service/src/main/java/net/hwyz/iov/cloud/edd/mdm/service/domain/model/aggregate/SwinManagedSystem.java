package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN管理软件系统实体（EEAD 子域）
 * <p>
 * 描述一个SWIN定义下管理的车载节点清单。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinManagedSystem {

    private Long id;
    private String swinCode;
    private String vehicleNodeCode;
    private Boolean isTypeApprovalRelevant;
    /**
     * 来源标注（语义降级）
     * <p>
     * CR-030 变更：该字段从"型批基准真值来源"降级为"来源标注"。
     * 型批基准真值改由 TA 基线（TypeApprovalBaseline）承载。
     * 字段保留、向后兼容、不硬迁移、不删列。
     */
    private String approvedSoftwareBaseline;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 创建新的管理软件系统
     *
     * @param swinCode               SWIN定义代码
     * @param vehicleNodeCode        车载节点代码
     * @param isTypeApprovalRelevant 是否型式批准相关
     * @param createBy               创建人
     * @return 新创建的SwinManagedSystem实例
     */
    public static SwinManagedSystem create(String swinCode, String vehicleNodeCode, Boolean isTypeApprovalRelevant, String createBy) {
        if (swinCode == null || swinCode.isBlank()) {
            throw new IllegalArgumentException("SWIN代码不能为空");
        }
        if (vehicleNodeCode == null || vehicleNodeCode.isBlank()) {
            throw new IllegalArgumentException("车载节点代码不能为空");
        }
        Date now = new Date();
        return SwinManagedSystem.builder()
                .swinCode(swinCode).vehicleNodeCode(vehicleNodeCode)
                .isTypeApprovalRelevant(isTypeApprovalRelevant != null && isTypeApprovalRelevant)
                .approvedSoftwareBaseline(null)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }
}
