package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ConfigurationOptionCodeBindingMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 配置选项码绑定仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class ConfigurationOptionCodeBindingRepositoryImpl implements ConfigurationOptionCodeBindingRepository {

    private final ConfigurationOptionCodeBindingMapper bindingMapper;

    @Override
    public void bind(String configurationCode, String optionCodeCode, String optionFamilyCode, String createBy) {
        ConfigurationOptionCodeBindingPo po = ConfigurationOptionCodeBindingPo.builder()
                .configurationCode(configurationCode)
                .optionCodeCode(optionCodeCode)
                .optionFamilyCode(optionFamilyCode)
                .createBy(createBy)
                .createTime(new Date())
                .modifyBy(createBy)
                .modifyTime(new Date())
                .rowVersion(0)
                .rowValid(true)
                .build();
        bindingMapper.insert(po);
    }

    @Override
    public void unbind(String configurationCode, String optionCodeCode, String modifyBy) {
        LambdaUpdateWrapper<ConfigurationOptionCodeBindingPo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ConfigurationOptionCodeBindingPo::getConfigurationCode, configurationCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getOptionCodeCode, optionCodeCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getRowValid, true);
        wrapper.set(ConfigurationOptionCodeBindingPo::getRowValid, false);
        wrapper.set(ConfigurationOptionCodeBindingPo::getModifyBy, modifyBy);
        wrapper.set(ConfigurationOptionCodeBindingPo::getModifyTime, new Date());
        bindingMapper.update(null, wrapper);
    }

    @Override
    public boolean existsByConfigurationCodeAndOptionFamilyCode(String configurationCode, String optionFamilyCode) {
        LambdaQueryWrapper<ConfigurationOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationOptionCodeBindingPo::getConfigurationCode, configurationCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getOptionFamilyCode, optionFamilyCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByOptionCodeCode(String optionCodeCode) {
        LambdaQueryWrapper<ConfigurationOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationOptionCodeBindingPo::getOptionCodeCode, optionCodeCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<ConfigurationOptionCodeBindingPo> findByConfigurationCode(String configurationCode) {
        LambdaQueryWrapper<ConfigurationOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationOptionCodeBindingPo::getConfigurationCode, configurationCode);
        wrapper.eq(ConfigurationOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectList(wrapper);
    }

    @Override
    public List<String> findConfigurationCodesByOptionCodes(List<String> optionCodes, int size) {
        return bindingMapper.findConfigurationCodesByOptionCodes(optionCodes, size);
    }
}
