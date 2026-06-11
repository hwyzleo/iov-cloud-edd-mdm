package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;

/**
 * 零件领域组装器
 *
 * @author hwyz_leo
 */
public class PartDomainAssembler {

    private PartDomainAssembler() {
    }

    /**
     * 领域对象转DTO
     */
    public static PartDto toDto(Part part) {
        return PartDto.builder()
                .id(part.getId())
                .code(part.getCode())
                .baseNo(part.getBaseNo())
                .numberingSource(part.getNumberingSource() != null ? part.getNumberingSource().name() : null)
                .name(part.getName())
                .nameLocal(part.getNameLocal())
                .description(part.getDescription())
                .categoryCode(part.getCategoryCode())
                .partType(part.getPartType() != null ? part.getPartType().name() : null)
                .vehicleNodeCode(part.getVehicleNodeCode())
                .supplierCode(part.getSupplierCode())
                .isSoftware(part.getIsSoftware())
                .isAssembly(part.getIsAssembly())
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
