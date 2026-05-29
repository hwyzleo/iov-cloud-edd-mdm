package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;

/**
 * 物料分类领域组装器
 *
 * @author hwyz_leo
 */
public class MaterialCategoryDomainAssembler {

    private MaterialCategoryDomainAssembler() {
    }

    /**
     * 创建命令转领域对象
     */
    public static MaterialCategory toDomain(MaterialCategoryCreateCmd cmd, String createBy) {
        return MaterialCategory.create(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getParentCode(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );
    }

    /**
     * 领域对象转DTO
     */
    public static MaterialCategoryDto toDto(MaterialCategory category) {
        return MaterialCategoryDto.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .nameLocal(category.getNameLocal())
                .description(category.getDescription())
                .parentCode(category.getParentCode())
                .sourceSystem(category.getSourceSystem())
                .sourceId(category.getSourceId())
                .sourceVersion(category.getSourceVersion())
                .ingestionChannel(category.getIngestionChannel())
                .ingestionTime(category.getIngestionTime())
                .sourcePayloadHash(category.getSourcePayloadHash())
                .version(category.getVersion())
                .effectiveFrom(category.getEffectiveFrom())
                .effectiveTo(category.getEffectiveTo())
                .status(category.getStatus() != null ? category.getStatus().name() : null)
                .createBy(category.getCreateBy())
                .createTime(category.getCreateTime())
                .modifyBy(category.getModifyBy())
                .modifyTime(category.getModifyTime())
                .build();
    }
}
