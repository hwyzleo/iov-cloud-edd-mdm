package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 版本Assembler
 *
 * @author hwyz_leo
 */
@Component
public class VariantAssembler {

    public VariantResponse toResponse(VariantDto dto) {
        if (dto == null) {
            return null;
        }
        return VariantResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .modelCode(dto.getModelCode())
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

    public VariantHistoryResponse toHistoryResponse(VariantHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return VariantHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .modelCode(dto.getModelCode())
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
