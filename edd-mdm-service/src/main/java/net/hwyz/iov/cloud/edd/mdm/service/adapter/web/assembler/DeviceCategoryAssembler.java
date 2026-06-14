package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 设备类别 Assembler（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Component
public class DeviceCategoryAssembler {

    public DeviceCategoryResponse toResponse(DeviceCategoryDto dto) {
        if (dto == null) {
            return null;
        }
        return DeviceCategoryResponse.builder()
                .id(dto.getId()).code(dto.getCode()).name(dto.getName())
                .nameLocal(dto.getNameLocal()).description(dto.getDescription())
                .sortOrder(dto.getSortOrder())
                .source(dto.getSource()).externalRefId(dto.getExternalRefId())
                .externalVersion(dto.getExternalVersion()).lastSyncTime(dto.getLastSyncTime())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom()).effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy()).createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy()).modifyTime(dto.getModifyTime())
                .build();
    }

    public DeviceCategoryResponse historyToResponse(DeviceCategoryHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return DeviceCategoryResponse.builder()
                .code(dto.getCode()).name(dto.getName()).nameLocal(dto.getNameLocal())
                .description(dto.getDescription()).sortOrder(dto.getSortOrder())
                .source(dto.getSource()).externalRefId(dto.getExternalRefId())
                .externalVersion(dto.getExternalVersion()).lastSyncTime(dto.getLastSyncTime())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom()).effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy()).createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy()).modifyTime(dto.getModifyTime())
                .snapshotTime(dto.getSnapshotTime())
                .operationType(dto.getOperationType())
                .operator(dto.getOperator())
                .build();
    }
}
