package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BrandStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.BrandPo;
import org.springframework.stereotype.Component;

/**
 * 品牌转换器
 *
 * @author hwyz_leo
 */
@Component
public class BrandConverter {

    /**
     * 持久化对象转换为领域模型
     *
     * @param po 持久化对象
     * @return 领域模型
     */
    public Brand toDomain(BrandPo po) {
        if (po == null) {
            return null;
        }
        return Brand.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .logo(po.getLogo())
                .country(po.getCountry())
                .foundedYear(po.getFoundedYear())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(BrandStatus.valueOf(po.getStatus()))
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
    public BrandPo toPo(Brand domain) {
        if (domain == null) {
            return null;
        }
        return BrandPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .description(domain.getDescription())
                .logo(domain.getLogo())
                .country(domain.getCountry())
                .foundedYear(domain.getFoundedYear())
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
