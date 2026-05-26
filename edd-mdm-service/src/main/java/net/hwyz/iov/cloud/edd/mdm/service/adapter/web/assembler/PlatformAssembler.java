package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 平台Assembler
 *
 * @author hwyz_leo
 */
@Component
public class PlatformAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 平台DTO
     * @return 平台响应对象
     */
    public PlatformResponse toResponse(PlatformDto dto) {
        if (dto == null) {
            return null;
        }
        return PlatformResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .platformType(dto.getPlatformType())
                .architecture(dto.getArchitecture())
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
     * 历史版本DTO转换为响应对象
     *
     * @param dto 平台历史版本DTO
     * @return 平台历史版本响应对象
     */
    public PlatformHistoryResponse toHistoryResponse(PlatformHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return PlatformHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .platformType(dto.getPlatformType())
                .architecture(dto.getArchitecture())
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
