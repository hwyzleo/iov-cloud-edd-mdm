package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;

/**
 * 零件领域组装器
 *
 * @author hwyz_leo
 */
public class PartDomainAssembler {

    private PartDomainAssembler() {
    }

    /**
     * 创建命令转领域对象
     */
    public static Part toDomain(PartCreateCmd cmd, String createBy) {
        return Part.create(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getCategoryCode(),
                cmd.getPartType() != null ? PartType.valueOf(cmd.getPartType()) : null,
                cmd.getVehicleNodeCode(), cmd.getSupplierCode(),
                cmd.getIsSoftware(), cmd.getFotaUpgradeable(), cmd.getIsSafetyCritical(),
                cmd.getIsKeyPart() != null ? KeyPartLevel.valueOf(cmd.getIsKeyPart()) : null,
                cmd.getIsRegulatoryPart(), cmd.getIsFramePart(),
                cmd.getIsAccuratelyTraced(), cmd.getFfaCode(), cmd.getFfaDesc(),
                cmd.getIsDigitate(), cmd.getInitialModel(), cmd.getProductionCode(),
                cmd.getFirstProductionDate(), cmd.getDesigner(), cmd.getDesignerDept(),
                cmd.getUom(), cmd.getDrawingNo(), cmd.getDrawingVersion(),
                cmd.getWeight(), cmd.getWeightUom(),
                cmd.getLifecycleStage() != null ? LifecycleStage.valueOf(cmd.getLifecycleStage()) : null,
                cmd.getSubstitutePartCode(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );
    }

    /**
     * 领域对象转DTO
     */
    public static PartDto toDto(Part part) {
        return PartDto.builder()
                .id(part.getId())
                .code(part.getCode())
                .name(part.getName())
                .nameLocal(part.getNameLocal())
                .description(part.getDescription())
                .categoryCode(part.getCategoryCode())
                .partType(part.getPartType() != null ? part.getPartType().name() : null)
                .vehicleNodeCode(part.getVehicleNodeCode())
                .supplierCode(part.getSupplierCode())
                .isSoftware(part.getIsSoftware())
                .fotaUpgradeable(part.getFotaUpgradeable())
                .isSafetyCritical(part.getIsSafetyCritical())
                .isKeyPart(part.getIsKeyPart() != null ? part.getIsKeyPart().name() : null)
                .isRegulatoryPart(part.getIsRegulatoryPart())
                .isFramePart(part.getIsFramePart())
                .isAccuratelyTraced(part.getIsAccuratelyTraced())
                .ffaCode(part.getFfaCode())
                .ffaDesc(part.getFfaDesc())
                .isDigitate(part.getIsDigitate())
                .initialModel(part.getInitialModel())
                .productionCode(part.getProductionCode())
                .firstProductionDate(part.getFirstProductionDate())
                .designer(part.getDesigner())
                .designerDept(part.getDesignerDept())
                .uom(part.getUom())
                .drawingNo(part.getDrawingNo())
                .drawingVersion(part.getDrawingVersion())
                .weight(part.getWeight())
                .weightUom(part.getWeightUom())
                .lifecycleStage(part.getLifecycleStage() != null ? part.getLifecycleStage().name() : null)
                .substitutePartCode(part.getSubstitutePartCode())
                .sourceSystem(part.getSourceSystem())
                .sourceId(part.getSourceId())
                .sourceVersion(part.getSourceVersion())
                .ingestionChannel(part.getIngestionChannel())
                .ingestionTime(part.getIngestionTime())
                .sourcePayloadHash(part.getSourcePayloadHash())
                .version(part.getVersion())
                .effectiveFrom(part.getEffectiveFrom())
                .effectiveTo(part.getEffectiveTo())
                .status(part.getStatus() != null ? part.getStatus().name() : null)
                .createBy(part.getCreateBy())
                .createTime(part.getCreateTime())
                .modifyBy(part.getModifyBy())
                .modifyTime(part.getModifyTime())
                .build();
    }
}
