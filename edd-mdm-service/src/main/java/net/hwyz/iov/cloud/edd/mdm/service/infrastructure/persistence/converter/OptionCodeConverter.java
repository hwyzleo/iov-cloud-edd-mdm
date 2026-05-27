package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodePo;
import org.springframework.stereotype.Component;

/**
 * 选项码转换器
 *
 * @author hwyz_leo
 */
@Component
public class OptionCodeConverter {

    public OptionCode toDomain(OptionCodePo po) {
        if (po == null) {
            return null;
        }
        return OptionCode.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .optionFamilyCode(po.getOptionFamilyCode())
                .description(po.getDescription())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(OptionCodeStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public OptionCodePo toPo(OptionCode domain) {
        if (domain == null) {
            return null;
        }
        return OptionCodePo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .optionFamilyCode(domain.getOptionFamilyCode())
                .description(domain.getDescription())
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
