package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零件历史快照持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_material_part_history")
public class PartHistoryPo {

    @TableId(type = IdType.AUTO)
    private Long snapshotId;

    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    private String code;
    private String baseNo;
    private String numberingSource;
    private String name;
    private String nameLocal;
    private String description;
    private String categoryCode;
    private String partType;
    private String vehicleNodeCode;
    private String supplierCode;
    @TableField("is_software")
    private Boolean isSoftware;
    @TableField("is_assembly")
    private Boolean isAssembly;
    @TableField("fota_upgradeable")
    private Boolean fotaUpgradeable;
    @TableField("is_safety_critical")
    private Boolean isSafetyCritical;
    private String isKeyPart;
    @TableField("is_regulatory_part")
    private Boolean isRegulatoryPart;
    @TableField("is_frame_part")
    private Boolean isFramePart;
    @TableField("is_accurately_traced")
    private Boolean isAccuratelyTraced;
    private String ffaCode;
    private String ffaDesc;
    @TableField("is_digitate")
    private Boolean isDigitate;
    private String initialModel;
    private String productionCode;
    private Date firstProductionDate;
    private String designer;
    private String designerDept;
    private String uom;
    private String drawingNo;
    private String drawingVersion;
    private BigDecimal weight;
    private String weightUom;
    private String lifecycleStage;
    private String substitutePartCode;

    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;

    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
