package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.DeviceCategoryPo;
import org.springframework.stereotype.Component;

/**
 * 设备类别 DO ⇄ Domain 转换器（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Component
public class DeviceCategoryConverter {

    public DeviceCategory toDomain(DeviceCategoryPo po) {
        if (po == null) {
            return null;
        }
        return DeviceCategory.builder()
                .id(po.getId()).code(po.getCode()).name(po.getName())
                .nameLocal(po.getNameLocal()).description(po.getDescription())
                .sortOrder(po.getSortOrder())
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

    public DeviceCategoryPo toPo(DeviceCategory domain) {
        if (domain == null) {
            return null;
        }
        return DeviceCategoryPo.builder()
                .id(domain.getId()).code(domain.getCode()).name(domain.getName())
                .nameLocal(domain.getNameLocal()).description(domain.getDescription())
                .sortOrder(domain.getSortOrder())
                .source(domain.getSource()).externalRefId(domain.getExternalRefId())
                .externalVersion(domain.getExternalVersion()).lastSyncTime(domain.getLastSyncTime())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom()).effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy()).createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy()).modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion()).rowValid(domain.getRowValid())
                .build();
    }
}
