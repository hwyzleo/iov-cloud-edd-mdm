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
}
