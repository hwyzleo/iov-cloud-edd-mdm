package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionFamilyPo;
import org.springframework.stereotype.Component;

/**
 * 选项族转换器
 *
 * @author hwyz_leo
 */
@Component
public class OptionFamilyConverter {

    public OptionFamily toDomain(OptionFamilyPo po) {
        if (po == null) {
            return null;
        }
        return OptionFamily.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .category(po.getCategory() != null ? OptionFamilyCategory.valueOf(po.getCategory()) : null)
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(OptionFamilyStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public OptionFamilyPo toPo(OptionFamily domain) {
        if (domain == null) {
            return null;
        }
        return OptionFamilyPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .description(domain.getDescription())
                .category(domain.getCategory() != null ? domain.getCategory().name() : null)
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
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
