package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PartHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartHistoryPo;
import org.springframework.stereotype.Component;

@Component
public class PartHistoryConverter {

    public PartHistory toDomain(PartHistoryPo po) {
        if (po == null) {
            return null;
        }
        return PartHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .categoryCode(po.getCategoryCode())
                .partType(po.getPartType() != null ? PartType.valueOf(po.getPartType()) : null)
                .vehicleNodeCode(po.getVehicleNodeCode())
                .supplierCode(po.getSupplierCode())
                .isSoftware(po.getIsSoftware())
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
}
