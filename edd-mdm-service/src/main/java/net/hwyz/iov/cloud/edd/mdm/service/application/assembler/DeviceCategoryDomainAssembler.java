package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.DeviceCategoryHistory;

public class DeviceCategoryDomainAssembler {

    private DeviceCategoryDomainAssembler() {
    }

    public static DeviceCategoryDto toDto(DeviceCategory category) {
        return DeviceCategoryDto.builder()
                .id(category.getId()).code(category.getCode()).name(category.getName())
                .nameLocal(category.getNameLocal()).description(category.getDescription())
                .sortOrder(category.getSortOrder())
                .source(category.getSource()).externalRefId(category.getExternalRefId())
                .externalVersion(category.getExternalVersion()).lastSyncTime(category.getLastSyncTime())
                .version(category.getVersion())
                .effectiveFrom(category.getEffectiveFrom()).effectiveTo(category.getEffectiveTo())
                .status(category.getStatus() != null ? category.getStatus().name() : null)
                .createBy(category.getCreateBy()).createTime(category.getCreateTime())
                .modifyBy(category.getModifyBy()).modifyTime(category.getModifyTime())
                .build();
    }

    public static DeviceCategoryHistoryDto toHistoryDto(DeviceCategoryHistory history) {
        return DeviceCategoryHistoryDto.builder()
                .snapshotId(history.getSnapshotId()).entityId(history.getEntityId())
                .operationType(history.getOperationType()).snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .code(history.getCode()).name(history.getName()).nameLocal(history.getNameLocal())
                .description(history.getDescription()).sortOrder(history.getSortOrder())
                .source(history.getSource()).externalRefId(history.getExternalRefId())
                .externalVersion(history.getExternalVersion()).lastSyncTime(history.getLastSyncTime())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom()).effectiveTo(history.getEffectiveTo())
                .status(history.getStatus() != null ? history.getStatus().name() : null)
                .createBy(history.getCreateBy()).createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy()).modifyTime(history.getModifyTime())
                .build();
    }
}
