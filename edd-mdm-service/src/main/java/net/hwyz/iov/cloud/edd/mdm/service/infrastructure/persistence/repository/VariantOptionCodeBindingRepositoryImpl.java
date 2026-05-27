package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.VariantOptionCodeBindingMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantOptionCodeBindingPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 版本选项码绑定仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class VariantOptionCodeBindingRepositoryImpl implements VariantOptionCodeBindingRepository {

    private final VariantOptionCodeBindingMapper bindingMapper;

    @Override
    public void bind(String variantCode, String optionCodeCode, String optionFamilyCode, String createBy) {
        VariantOptionCodeBindingPo po = VariantOptionCodeBindingPo.builder()
                .variantCode(variantCode)
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
    public void unbind(String variantCode, String optionCodeCode, String modifyBy) {
        LambdaUpdateWrapper<VariantOptionCodeBindingPo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(VariantOptionCodeBindingPo::getVariantCode, variantCode);
        wrapper.eq(VariantOptionCodeBindingPo::getOptionCodeCode, optionCodeCode);
        wrapper.eq(VariantOptionCodeBindingPo::getRowValid, true);
        wrapper.set(VariantOptionCodeBindingPo::getRowValid, false);
        wrapper.set(VariantOptionCodeBindingPo::getModifyBy, modifyBy);
        wrapper.set(VariantOptionCodeBindingPo::getModifyTime, new Date());
        bindingMapper.update(null, wrapper);
    }

    @Override
    public boolean existsByVariantCodeAndOptionFamilyCode(String variantCode, String optionFamilyCode) {
        LambdaQueryWrapper<VariantOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantOptionCodeBindingPo::getVariantCode, variantCode);
        wrapper.eq(VariantOptionCodeBindingPo::getOptionFamilyCode, optionFamilyCode);
        wrapper.eq(VariantOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByOptionCodeCode(String optionCodeCode) {
        LambdaQueryWrapper<VariantOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantOptionCodeBindingPo::getOptionCodeCode, optionCodeCode);
        wrapper.eq(VariantOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<VariantOptionCodeBindingPo> findByVariantCode(String variantCode) {
        LambdaQueryWrapper<VariantOptionCodeBindingPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantOptionCodeBindingPo::getVariantCode, variantCode);
        wrapper.eq(VariantOptionCodeBindingPo::getRowValid, true);
        return bindingMapper.selectList(wrapper);
    }
}
