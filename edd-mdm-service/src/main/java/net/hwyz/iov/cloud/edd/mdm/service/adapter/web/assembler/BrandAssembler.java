package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandDto;
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
}
