package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformDto;
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
