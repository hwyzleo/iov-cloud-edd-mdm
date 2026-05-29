package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 物料分类Assembler
 *
 * @author hwyz_leo
 */
@Component
public class MaterialCategoryAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 物料分类DTO
     * @return 物料分类响应对象
     */
    public MaterialCategoryResponse toResponse(MaterialCategoryDto dto) {
        if (dto == null) {
            return null;
        }
        return MaterialCategoryResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .parentCode(dto.getParentCode())
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
     * @param dto 物料分类历史版本DTO
     * @return 物料分类历史版本响应对象
     */
    public MaterialCategoryHistoryResponse toHistoryResponse(MaterialCategoryHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return MaterialCategoryHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .parentCode(dto.getParentCode())
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
