package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinSchemeDto;
import org.springframework.stereotype.Component;

/**
 * SWIN编码方案 Assembler（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Component
public class SwinSchemeAssembler {

    public SwinSchemeResponse toResponse(SwinSchemeDto dto) {
        if (dto == null) {
            return null;
        }
        return SwinSchemeResponse.builder()
                .id(dto.getId()).code(dto.getCode()).name(dto.getName())
                .nameLocal(dto.getNameLocal()).description(dto.getDescription())
                .route(dto.getRoute()).sortOrder(dto.getSortOrder())
                .source(dto.getSource()).externalRefId(dto.getExternalRefId())
                .externalVersion(dto.getExternalVersion()).lastSyncTime(dto.getLastSyncTime())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom()).effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy()).createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy()).modifyTime(dto.getModifyTime())
                .build();
    }
}
