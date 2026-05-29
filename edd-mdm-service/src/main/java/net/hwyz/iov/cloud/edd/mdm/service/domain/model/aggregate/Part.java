package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {
    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String categoryCode;
    private PartType partType;
    private String vehicleNodeCode;
    private String supplierCode;
    private Boolean isSoftware;
    private Boolean fotaUpgradeable;
    private Boolean isSafetyCritical;
    private KeyPartLevel isKeyPart;
    private Boolean isRegulatoryPart;
    private Boolean isFramePart;
    private Boolean isAccuratelyTraced;
    private String ffaCode;
    private String ffaDesc;
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
    private LifecycleStage lifecycleStage;
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
    private PartStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    public static Part create(String code, String name, String nameLocal, String description,
                               String categoryCode, PartType partType, String vehicleNodeCode, String supplierCode,
                               Boolean isSoftware, Boolean fotaUpgradeable, Boolean isSafetyCritical,
                               KeyPartLevel isKeyPart, Boolean isRegulatoryPart, Boolean isFramePart,
                               Boolean isAccuratelyTraced, String ffaCode, String ffaDesc,
                               Boolean isDigitate, String initialModel, String productionCode,
                               Date firstProductionDate, String designer, String designerDept,
                               String uom, String drawingNo, String drawingVersion,
                               BigDecimal weight, String weightUom,
                               LifecycleStage lifecycleStage, String substitutePartCode,
                               Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        Date now = new Date();
        return Part.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description)
                .categoryCode(categoryCode).partType(partType).vehicleNodeCode(vehicleNodeCode).supplierCode(supplierCode)
                .isSoftware(isSoftware).fotaUpgradeable(fotaUpgradeable).isSafetyCritical(isSafetyCritical)
                .isKeyPart(isKeyPart).isRegulatoryPart(isRegulatoryPart).isFramePart(isFramePart)
                .isAccuratelyTraced(isAccuratelyTraced).ffaCode(ffaCode).ffaDesc(ffaDesc)
                .isDigitate(isDigitate).initialModel(initialModel).productionCode(productionCode)
                .firstProductionDate(firstProductionDate).designer(designer).designerDept(designerDept)
                .uom(uom).drawingNo(drawingNo).drawingVersion(drawingVersion)
                .weight(weight).weightUom(weightUom)
                .lifecycleStage(lifecycleStage).substitutePartCode(substitutePartCode)
                .sourceSystem("LOCAL").sourceId(code).ingestionChannel("LOCAL").ingestionTime(now)
                .version(1).effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .status(PartStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }

    public void update(String name, String nameLocal, String description,
                       String categoryCode, PartType partType, String vehicleNodeCode, String supplierCode,
                       Boolean isSoftware, Boolean fotaUpgradeable, Boolean isSafetyCritical,
                       KeyPartLevel isKeyPart, Boolean isRegulatoryPart, Boolean isFramePart,
                       Boolean isAccuratelyTraced, String ffaCode, String ffaDesc,
                       Boolean isDigitate, String initialModel, String productionCode,
                       Date firstProductionDate, String designer, String designerDept,
                       String uom, String drawingNo, String drawingVersion,
                       BigDecimal weight, String weightUom,
                       LifecycleStage lifecycleStage, String substitutePartCode,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.categoryCode = categoryCode;
        this.partType = partType;
        this.vehicleNodeCode = vehicleNodeCode;
        this.supplierCode = supplierCode;
        this.isSoftware = isSoftware;
        this.fotaUpgradeable = fotaUpgradeable;
        this.isSafetyCritical = isSafetyCritical;
        this.isKeyPart = isKeyPart;
        this.isRegulatoryPart = isRegulatoryPart;
        this.isFramePart = isFramePart;
        this.isAccuratelyTraced = isAccuratelyTraced;
        this.ffaCode = ffaCode;
        this.ffaDesc = ffaDesc;
        this.isDigitate = isDigitate;
        this.initialModel = initialModel;
        this.productionCode = productionCode;
        this.firstProductionDate = firstProductionDate;
        this.designer = designer;
        this.designerDept = designerDept;
        this.uom = uom;
        this.drawingNo = drawingNo;
        this.drawingVersion = drawingVersion;
        this.weight = weight;
        this.weightUom = weightUom;
        this.lifecycleStage = lifecycleStage;
        this.substitutePartCode = substitutePartCode;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void deactivate(String modifyBy) {
        if (this.status != PartStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的零件才能失效");
        }
        this.status = PartStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void advanceLifecycleStage(LifecycleStage newStage, String modifyBy) {
        if (this.lifecycleStage == LifecycleStage.OBSOLETE) {
            throw new IllegalStateException("OBSOLETE为终态，不可变更");
        }
        if (newStage.ordinal() <= this.lifecycleStage.ordinal()) {
            throw new IllegalStateException("生命周期阶段只能正向流转");
        }
        this.lifecycleStage = newStage;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
