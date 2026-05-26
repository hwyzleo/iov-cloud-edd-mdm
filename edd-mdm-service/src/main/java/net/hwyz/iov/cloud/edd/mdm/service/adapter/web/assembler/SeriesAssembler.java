package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SeriesDto;
import org.springframework.stereotype.Component;

/**
 * 车系Assembler
 *
 * @author hwyz_leo
 */
@Component
public class SeriesAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 车系DTO
     * @return 车系响应对象
     */
    public SeriesResponse toResponse(SeriesDto dto) {
        if (dto == null) {
            return null;
        }
        return SeriesResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .brandCode(dto.getBrandCode())
                .seriesType(dto.getSeriesType())
                .lifecycleStatus(dto.getLifecycleStatus())
                .targetMarket(dto.getTargetMarket())
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
