package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;

import java.util.List;

/**
 * 配置选项码绑定仓储接口
 *
 * @author hwyz_leo
 */
public interface ConfigurationOptionCodeBindingRepository {

    void bind(String configurationCode, String optionCodeCode, String optionFamilyCode, String createBy);

    void unbind(String configurationCode, String optionCodeCode, String modifyBy);

    boolean existsByConfigurationCodeAndOptionFamilyCode(String configurationCode, String optionFamilyCode);

    boolean existsByOptionCodeCode(String optionCodeCode);

    List<ConfigurationOptionCodeBindingPo> findByConfigurationCode(String configurationCode);

    List<String> findConfigurationCodesByOptionCodes(List<String> optionCodes, int size);

    /**
     * 根据版本和选项码组合查询配置code（包含匹配，仅返回ACTIVE状态）
     *
     * @param variantCode 版本code
     * @param optionCodes 选项码列表
     * @param size        选项码数量
     * @return 匹配的配置code列表
     */
    List<String> findConfigurationCodeByVariantAndOptionCodes(String variantCode, List<String> optionCodes, int size);
}
