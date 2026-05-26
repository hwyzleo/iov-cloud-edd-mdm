package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformType;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformPo;
import org.springframework.stereotype.Component;

/**
 * 平台转换器
 *
 * @author hwyz_leo
 */
@Component
public class PlatformConverter {

    /**
     * 持久化对象转换为领域模型
     *
     * @param po 持久化对象
     * @return 领域模型
     */
    public Platform toDomain(PlatformPo po) {
        if (po == null) {
            return null;
        }
        return Platform.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .platformType(po.getPlatformType() != null ? PlatformType.valueOf(po.getPlatformType()) : null)
                .architecture(po.getArchitecture())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(PlatformStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    /**
     * 领域模型转换为持久化对象
     *
     * @param domain 领域模型
     * @return 持久化对象
     */
    public PlatformPo toPo(Platform domain) {
        if (domain == null) {
            return null;
        }
        return PlatformPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .platformType(domain.getPlatformType() != null ? domain.getPlatformType().name() : null)
                .architecture(domain.getArchitecture())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }
}
