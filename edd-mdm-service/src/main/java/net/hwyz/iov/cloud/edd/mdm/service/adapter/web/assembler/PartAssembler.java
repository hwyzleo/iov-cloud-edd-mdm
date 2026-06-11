package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 零件Assembler
 *
 * @author hwyz_leo
 */
@Component
public class PartAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 零件DTO
     * @return 零件响应对象
     */
    public PartResponse toResponse(PartDto dto) {
        if (dto == null) {
            return null;
        }
        return PartResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .baseNo(dto.getBaseNo())
                .numberingSource(dto.getNumberingSource())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .categoryCode(dto.getCategoryCode())
                .partType(dto.getPartType())
                .vehicleNodeCode(dto.getVehicleNodeCode())
                .supplierCode(dto.getSupplierCode())
                .isSoftware(dto.getIsSoftware())
                .isAssembly(dto.getIsAssembly())
                .fotaUpgradeable(dto.getFotaUpgradeable())
                .isSafetyCritical(dto.getIsSafetyCritical())
                .isKeyPart(dto.getIsKeyPart())
                .isRegulatoryPart(dto.getIsRegulatoryPart())
                .isFramePart(dto.getIsFramePart())
                .isAccuratelyTraced(dto.getIsAccuratelyTraced())
                .ffaCode(dto.getFfaCode())
                .ffaDesc(dto.getFfaDesc())
                .isDigitate(dto.getIsDigitate())
                .initialModel(dto.getInitialModel())
                .productionCode(dto.getProductionCode())
                .firstProductionDate(dto.getFirstProductionDate())
                .designer(dto.getDesigner())
                .designerDept(dto.getDesignerDept())
                .uom(dto.getUom())
                .drawingNo(dto.getDrawingNo())
                .drawingVersion(dto.getDrawingVersion())
                .weight(dto.getWeight())
                .weightUom(dto.getWeightUom())
                .lifecycleStage(dto.getLifecycleStage())
                .substitutePartCode(dto.getSubstitutePartCode())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }

    /**
     * DTO转换为简要响应对象
     *
     * @param dto 零件DTO
     * @return 零件简要响应对象
     */
    public PartBriefResponse toBriefResponse(PartDto dto) {
        if (dto == null) {
            return null;
        }
        return PartBriefResponse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .partType(dto.getPartType())
                .categoryCode(dto.getCategoryCode())
                .lifecycleStage(dto.getLifecycleStage())
                .status(dto.getStatus())
                .build();
    }

    /**
     * 历史版本DTO转换为响应对象
     *
     * @param dto 零件历史版本DTO
     * @return 零件历史版本响应对象
     */
    public PartHistoryResponse toHistoryResponse(PartHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return PartHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .categoryCode(dto.getCategoryCode())
                .partType(dto.getPartType())
                .vehicleNodeCode(dto.getVehicleNodeCode())
                .supplierCode(dto.getSupplierCode())
                .isSoftware(dto.getIsSoftware())
                .fotaUpgradeable(dto.getFotaUpgradeable())
                .isSafetyCritical(dto.getIsSafetyCritical())
                .isKeyPart(dto.getIsKeyPart())
                .isRegulatoryPart(dto.getIsRegulatoryPart())
                .isFramePart(dto.getIsFramePart())
                .isAccuratelyTraced(dto.getIsAccuratelyTraced())
                .ffaCode(dto.getFfaCode())
                .ffaDesc(dto.getFfaDesc())
                .isDigitate(dto.getIsDigitate())
                .initialModel(dto.getInitialModel())
                .productionCode(dto.getProductionCode())
                .firstProductionDate(dto.getFirstProductionDate())
                .designer(dto.getDesigner())
                .designerDept(dto.getDesignerDept())
                .uom(dto.getUom())
                .drawingNo(dto.getDrawingNo())
                .drawingVersion(dto.getDrawingVersion())
                .weight(dto.getWeight())
                .weightUom(dto.getWeightUom())
                .lifecycleStage(dto.getLifecycleStage())
                .substitutePartCode(dto.getSubstitutePartCode())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .operationType(dto.getOperationType())
                .snapshotTime(dto.getSnapshotTime())
                .operator(dto.getOperator())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }
}
