package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 车型Assembler
 *
 * @author hwyz_leo
 */
@Component
public class ModelAssembler {

    public ModelResponse toResponse(ModelDto dto) {
        if (dto == null) {
            return null;
        }
        return ModelResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .carLineCode(dto.getCarLineCode())
                .platformCode(dto.getPlatformCode())
                .modelYear(dto.getModelYear())
                .description(dto.getDescription())
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

    public ModelHistoryResponse toHistoryResponse(ModelHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return ModelHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .carLineCode(dto.getCarLineCode())
                .platformCode(dto.getPlatformCode())
                .modelYear(dto.getModelYear())
                .description(dto.getDescription())
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
