package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 选项码Assembler
 *
 * @author hwyz_leo
 */
@Component
public class OptionCodeAssembler {

    public OptionCodeResponse toResponse(OptionCodeDto dto) {
        if (dto == null) {
            return null;
        }
        return OptionCodeResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .optionFamilyCode(dto.getOptionFamilyCode())
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

    public OptionCodeHistoryResponse toHistoryResponse(OptionCodeHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return OptionCodeHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .optionFamilyCode(dto.getOptionFamilyCode())
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
