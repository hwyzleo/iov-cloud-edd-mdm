package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 品牌Assembler
 *
 * @author hwyz_leo
 */
@Component
public class BrandAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 品牌DTO
     * @return 品牌响应对象
     */
    public BrandResponse toResponse(BrandDto dto) {
        if (dto == null) {
            return null;
        }
        return BrandResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .logo(dto.getLogo())
                .country(dto.getCountry())
                .foundedYear(dto.getFoundedYear())
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
     * @param dto 品牌历史版本DTO
     * @return 品牌历史版本响应对象
     */
    public BrandHistoryResponse toHistoryResponse(BrandHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return BrandHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .logo(dto.getLogo())
                .country(dto.getCountry())
                .foundedYear(dto.getFoundedYear())
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
