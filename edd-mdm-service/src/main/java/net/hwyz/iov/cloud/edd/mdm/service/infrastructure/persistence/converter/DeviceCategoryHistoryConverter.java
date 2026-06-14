package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.DeviceCategoryHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.DeviceCategoryHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 设备类别历史 DO ⇄ Domain 转换器（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Component
public class DeviceCategoryHistoryConverter {

    public DeviceCategoryHistory toDomain(DeviceCategoryHistoryPo po) {
        if (po == null) {
            return null;
        }
        return DeviceCategoryHistory.builder()
                .snapshotId(po.getSnapshotId()).entityId(po.getEntityId())
                .operationType(po.getOperationType()).snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .code(po.getCode()).name(po.getName()).nameLocal(po.getNameLocal())
                .description(po.getDescription()).sortOrder(po.getSortOrder())
                .source(po.getSource()).externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion()).lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom()).effectiveTo(po.getEffectiveTo())
                .status(DeviceCategoryStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy()).createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy()).modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion()).rowValid(po.getRowValid())
                .build();
    }
}
