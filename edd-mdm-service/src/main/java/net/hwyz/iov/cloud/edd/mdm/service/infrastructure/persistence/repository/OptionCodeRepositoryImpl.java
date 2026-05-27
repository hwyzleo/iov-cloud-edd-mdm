package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionCodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.OptionCodeConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.OptionCodeHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionCodeHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionCodeMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodeHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodePo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 选项码仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class OptionCodeRepositoryImpl implements OptionCodeRepository {

    private final OptionCodeMapper optionCodeMapper;
    private final OptionCodeConverter optionCodeConverter;
    private final OptionCodeHistoryMapper optionCodeHistoryMapper;
    private final OptionCodeHistoryConverter optionCodeHistoryConverter;

    @Override
    public OptionCode save(OptionCode optionCode) {
        OptionCodePo po = optionCodeConverter.toPo(optionCode);
        if (po.getId() == null) {
            optionCodeMapper.insert(po);
        } else {
            optionCodeMapper.updateById(po);
        }
        return optionCodeConverter.toDomain(po);
    }

    @Override
    public Optional<OptionCode> findById(Long id) {
        OptionCodePo po = optionCodeMapper.selectById(id);
        return Optional.ofNullable(optionCodeConverter.toDomain(po));
    }

    @Override
    public Optional<OptionCode> findByCode(String code) {
        LambdaQueryWrapper<OptionCodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodePo::getCode, code);
        wrapper.eq(OptionCodePo::getRowValid, true);
        OptionCodePo po = optionCodeMapper.selectOne(wrapper);
        return Optional.ofNullable(optionCodeConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<OptionCodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodePo::getCode, code);
        wrapper.eq(OptionCodePo::getRowValid, true);
        return optionCodeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByOptionFamilyCode(String optionFamilyCode) {
        LambdaQueryWrapper<OptionCodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodePo::getOptionFamilyCode, optionFamilyCode);
        wrapper.eq(OptionCodePo::getRowValid, true);
        return optionCodeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<OptionCode> findAll(int page, int size, String optionFamilyCode, boolean includeInactive) {
        Page<OptionCodePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OptionCodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodePo::getRowValid, true);
        if (StringUtils.hasText(optionFamilyCode)) {
            wrapper.eq(OptionCodePo::getOptionFamilyCode, optionFamilyCode);
        }
        if (!includeInactive) {
            wrapper.eq(OptionCodePo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(OptionCodePo::getCreateTime);
        Page<OptionCodePo> result = optionCodeMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(optionCodeConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String optionFamilyCode, boolean includeInactive) {
        LambdaQueryWrapper<OptionCodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodePo::getRowValid, true);
        if (StringUtils.hasText(optionFamilyCode)) {
            wrapper.eq(OptionCodePo::getOptionFamilyCode, optionFamilyCode);
        }
        if (!includeInactive) {
            wrapper.eq(OptionCodePo::getStatus, "ACTIVE");
        }
        return optionCodeMapper.selectCount(wrapper);
    }

    @Override
    public void delete(OptionCode optionCode) {
        OptionCodePo po = optionCodeConverter.toPo(optionCode);
        optionCodeMapper.updateById(po);
    }

    @Override
    public List<OptionCodeHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<OptionCodeHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionCodeHistoryPo::getCode, code);
        wrapper.eq(OptionCodeHistoryPo::getRowValid, true);
        wrapper.orderByDesc(OptionCodeHistoryPo::getVersion);
        List<OptionCodeHistoryPo> poList = optionCodeHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(optionCodeHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
