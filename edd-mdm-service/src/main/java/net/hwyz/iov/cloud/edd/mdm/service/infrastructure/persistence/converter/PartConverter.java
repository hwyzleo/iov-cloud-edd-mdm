package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NumberingSource;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartPo;
import org.springframework.stereotype.Component;

@Component
public class PartConverter {

    public Part toDomain(PartPo po) {
        if (po == null) {
            return null;
        }
        return Part.builder()
                .id(po.getId())
                .code(po.getCode())
                .baseNo(po.getBaseNo())
                .numberingSource(po.getNumberingSource() != null ? NumberingSource.valueOf(po.getNumberingSource()) : null)
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .categoryCode(po.getCategoryCode())
                .partType(po.getPartType() != null ? PartType.valueOf(po.getPartType()) : null)
                .vehicleNodeCode(po.getVehicleNodeCode())
                .supplierCode(po.getSupplierCode())
                .isSoftware(po.getIsSoftware())
                .isAssembly(po.getIsAssembly())
                .fotaUpgradeable(po.getFotaUpgradeable())
                .isSafetyCritical(po.getIsSafetyCritical())
                .isKeyPart(po.getIsKeyPart() != null ? KeyPartLevel.valueOf(po.getIsKeyPart()) : null)
                .isRegulatoryPart(po.getIsRegulatoryPart())
                .isFramePart(po.getIsFramePart())
                .isAccuratelyTraced(po.getIsAccuratelyTraced())
                .ffaCode(po.getFfaCode())
                .ffaDesc(po.getFfaDesc())
                .isDigitate(po.getIsDigitate())
                .initialModel(po.getInitialModel())
                .productionCode(po.getProductionCode())
                .firstProductionDate(po.getFirstProductionDate())
                .designer(po.getDesigner())
                .designerDept(po.getDesignerDept())
                .uom(po.getUom())
                .drawingNo(po.getDrawingNo())
                .drawingVersion(po.getDrawingVersion())
                .weight(po.getWeight())
                .weightUom(po.getWeightUom())
                .lifecycleStage(po.getLifecycleStage() != null ? LifecycleStage.valueOf(po.getLifecycleStage()) : null)
                .substitutePartCode(po.getSubstitutePartCode())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(PartStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public PartPo toPo(Part domain) {
        if (domain == null) {
            return null;
        }
        return PartPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .baseNo(domain.getBaseNo())
                .numberingSource(domain.getNumberingSource() != null ? domain.getNumberingSource().name() : null)
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .description(domain.getDescription())
                .categoryCode(domain.getCategoryCode())
                .partType(domain.getPartType() != null ? domain.getPartType().name() : null)
                .vehicleNodeCode(domain.getVehicleNodeCode())
                .supplierCode(domain.getSupplierCode())
                .isSoftware(domain.getIsSoftware())
                .isAssembly(domain.getIsAssembly())
                .fotaUpgradeable(domain.getFotaUpgradeable())
                .isSafetyCritical(domain.getIsSafetyCritical())
                .isKeyPart(domain.getIsKeyPart() != null ? domain.getIsKeyPart().name() : null)
                .isRegulatoryPart(domain.getIsRegulatoryPart())
                .isFramePart(domain.getIsFramePart())
                .isAccuratelyTraced(domain.getIsAccuratelyTraced())
                .ffaCode(domain.getFfaCode())
                .ffaDesc(domain.getFfaDesc())
                .isDigitate(domain.getIsDigitate())
                .initialModel(domain.getInitialModel())
                .productionCode(domain.getProductionCode())
                .firstProductionDate(domain.getFirstProductionDate())
                .designer(domain.getDesigner())
                .designerDept(domain.getDesignerDept())
                .uom(domain.getUom())
                .drawingNo(domain.getDrawingNo())
                .drawingVersion(domain.getDrawingVersion())
                .weight(domain.getWeight())
                .weightUom(domain.getWeightUom())
                .lifecycleStage(domain.getLifecycleStage() != null ? domain.getLifecycleStage().name() : null)
                .substitutePartCode(domain.getSubstitutePartCode())
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }
}
