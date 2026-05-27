package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantOptionCodeBindingPo;

import java.util.List;

/**
 * 版本选项码绑定仓储接口
 *
 * @author hwyz_leo
 */
public interface VariantOptionCodeBindingRepository {

    void bind(String variantCode, String optionCodeCode, String optionFamilyCode, String createBy);

    void unbind(String variantCode, String optionCodeCode, String modifyBy);

    boolean existsByVariantCodeAndOptionFamilyCode(String variantCode, String optionFamilyCode);

    boolean existsByOptionCodeCode(String optionCodeCode);

    List<VariantOptionCodeBindingPo> findByVariantCode(String variantCode);
}
