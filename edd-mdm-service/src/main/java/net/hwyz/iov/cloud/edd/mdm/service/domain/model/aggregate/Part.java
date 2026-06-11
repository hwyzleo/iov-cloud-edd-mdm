package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NumberingSource;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartCode;
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
    private String baseNo;
    private NumberingSource numberingSource;
    private String name;
    private String nameLocal;
    private String description;
    private String categoryCode;
    private PartType partType;
    private String vehicleNodeCode;
    private String supplierCode;
    private Boolean isSoftware;
    private Boolean isAssembly;
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

    public static Part create(PartCode partCode, String name, String nameLocal, String description,
                               String categoryCode, PartType partType, String vehicleNodeCode, String supplierCode,
                               Boolean isSoftware, Boolean isAssembly, Boolean fotaUpgradeable, Boolean isSafetyCritical,
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
                .code(partCode.code()).baseNo(partCode.baseNo()).numberingSource(NumberingSource.MDM_GEN)
                .name(name).nameLocal(nameLocal).description(description)
                .categoryCode(categoryCode).partType(partType).vehicleNodeCode(vehicleNodeCode).supplierCode(supplierCode)
                .isSoftware(isSoftware).isAssembly(isAssembly).fotaUpgradeable(fotaUpgradeable).isSafetyCritical(isSafetyCritical)
                .isKeyPart(isKeyPart).isRegulatoryPart(isRegulatoryPart).isFramePart(isFramePart)
                .isAccuratelyTraced(isAccuratelyTraced).ffaCode(ffaCode).ffaDesc(ffaDesc)
                .isDigitate(isDigitate).initialModel(initialModel).productionCode(productionCode)
                .firstProductionDate(firstProductionDate).designer(designer).designerDept(designerDept)
                .uom(uom).drawingNo(drawingNo).drawingVersion(drawingVersion)
                .weight(weight).weightUom(weightUom)
                .lifecycleStage(lifecycleStage).substitutePartCode(substitutePartCode)
                .sourceSystem("LOCAL").sourceId(partCode.code()).ingestionChannel("LOCAL").ingestionTime(now)
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

    /**
     * 代次升级（互换性变更）- 创建新记录
     * @param newPartCode 新零件号值对象
     * @param operator 操作人
     * @return 新的Part实例
     */
    public Part upgradeGeneration(PartCode newPartCode, String operator) {
        Date now = new Date();
        Part newPart = new Part();
        newPart.setId(null); // 新id
        newPart.setCode(newPartCode.code());
        newPart.setBaseNo(newPartCode.baseNo());
        newPart.setNumberingSource(this.numberingSource);
        // 复制其他业务字段
        newPart.setName(this.name);
        newPart.setNameLocal(this.nameLocal);
        newPart.setDescription(this.description);
        newPart.setCategoryCode(this.categoryCode);
        newPart.setPartType(this.partType);
        newPart.setVehicleNodeCode(this.vehicleNodeCode);
        newPart.setSupplierCode(this.supplierCode);
        newPart.setIsSoftware(this.isSoftware);
        newPart.setIsAssembly(this.isAssembly);
        newPart.setFotaUpgradeable(this.fotaUpgradeable);
        newPart.setIsSafetyCritical(this.isSafetyCritical);
        newPart.setIsKeyPart(this.isKeyPart);
        newPart.setIsRegulatoryPart(this.isRegulatoryPart);
        newPart.setIsFramePart(this.isFramePart);
        newPart.setIsAccuratelyTraced(this.isAccuratelyTraced);
        newPart.setFfaCode(this.ffaCode);
        newPart.setFfaDesc(this.ffaDesc);
        newPart.setIsDigitate(this.isDigitate);
        newPart.setInitialModel(this.initialModel);
        newPart.setProductionCode(this.productionCode);
        newPart.setFirstProductionDate(this.firstProductionDate);
        newPart.setDesigner(this.designer);
        newPart.setDesignerDept(this.designerDept);
        newPart.setUom(this.uom);
        newPart.setDrawingNo(this.drawingNo);
        newPart.setDrawingVersion(this.drawingVersion);
        newPart.setWeight(this.weight);
        newPart.setWeightUom(this.weightUom);
        newPart.setLifecycleStage(this.lifecycleStage);
        newPart.setSubstitutePartCode(this.substitutePartCode);
        // 设置来源和审计字段
        newPart.setSourceSystem("LOCAL");
        newPart.setSourceId(newPartCode.code());
        newPart.setIngestionChannel("LOCAL");
        newPart.setIngestionTime(now);
        newPart.setVersion(1);
        newPart.setEffectiveFrom(this.effectiveFrom);
        newPart.setEffectiveTo(this.effectiveTo);
        newPart.setStatus(PartStatus.ACTIVE);
        newPart.setCreateBy(operator);
        newPart.setCreateTime(now);
        newPart.setModifyBy(operator);
        newPart.setModifyTime(now);
        newPart.setRowVersion(0);
        newPart.setRowValid(true);
        return newPart;
    }

    /**
     * 小修订 - 仅升drawing_version
     * @param drawingVersion 新图纸版本
     * @param modifyBy 修改人
     */
    public void minorRevision(String drawingVersion, String modifyBy) {
        this.setDrawingVersion(drawingVersion);
        this.setVersion(this.version + 1);
        this.setModifyBy(modifyBy);
        this.setModifyTime(new Date());
    }

    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
