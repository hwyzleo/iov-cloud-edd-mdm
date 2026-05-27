package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VariantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantPo;
import org.springframework.stereotype.Component;

/**
 * 版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class VariantConverter {

    public Variant toDomain(VariantPo po) {
        if (po == null) {
            return null;
        }
        return Variant.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .modelCode(po.getModelCode())
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
                .status(VariantStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public VariantPo toPo(Variant domain) {
        if (domain == null) {
            return null;
        }
        return VariantPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .modelCode(domain.getModelCode())
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
