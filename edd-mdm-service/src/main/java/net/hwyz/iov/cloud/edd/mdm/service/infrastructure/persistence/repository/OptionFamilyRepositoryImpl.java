package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionFamilyHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.OptionFamilyConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.OptionFamilyHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionFamilyHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionFamilyMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionFamilyHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionFamilyPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 选项族仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class OptionFamilyRepositoryImpl implements OptionFamilyRepository {

    private final OptionFamilyMapper optionFamilyMapper;
    private final OptionFamilyConverter optionFamilyConverter;
    private final OptionFamilyHistoryMapper optionFamilyHistoryMapper;
    private final OptionFamilyHistoryConverter optionFamilyHistoryConverter;

    @Override
    public OptionFamily save(OptionFamily optionFamily, String operationType) {
        OptionFamilyPo po = optionFamilyConverter.toPo(optionFamily);
        if (po.getId() == null) {
            optionFamilyMapper.insert(po);
        } else {
            optionFamilyMapper.updateById(po);
        }
        if (operationType != null) {
            OptionFamilyHistoryPo historyPo = OptionFamilyHistoryPo.builder()
                    .entityId(po.getId())
                    .code(po.getCode())
                    .name(po.getName())
                    .nameLocal(po.getNameLocal())
                    .description(po.getDescription())
                    .category(po.getCategory())
                    .sourceSystem(po.getSourceSystem())
                    .sourceId(po.getSourceId())
                    .sourceVersion(po.getSourceVersion())
                    .ingestionChannel(po.getIngestionChannel())
                    .ingestionTime(po.getIngestionTime())
                    .sourcePayloadHash(po.getSourcePayloadHash())
                    .version(po.getVersion())
                    .effectiveFrom(po.getEffectiveFrom())
                    .effectiveTo(po.getEffectiveTo())
                    .status(po.getStatus())
                    .operationType(operationType)
                    .snapshotTime(new Date())
                    .operator(po.getModifyBy())
                    .createBy(po.getModifyBy())
                    .createTime(new Date())
                    .modifyBy(po.getModifyBy())
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            optionFamilyHistoryMapper.insert(historyPo);
        }
        return optionFamilyConverter.toDomain(po);
    }

    @Override
    public Optional<OptionFamily> findById(Long id) {
        OptionFamilyPo po = optionFamilyMapper.selectById(id);
        return Optional.ofNullable(optionFamilyConverter.toDomain(po));
    }

    @Override
    public Optional<OptionFamily> findByCode(String code) {
        LambdaQueryWrapper<OptionFamilyPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionFamilyPo::getCode, code);
        wrapper.eq(OptionFamilyPo::getRowValid, true);
        OptionFamilyPo po = optionFamilyMapper.selectOne(wrapper);
        return Optional.ofNullable(optionFamilyConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<OptionFamilyPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionFamilyPo::getCode, code);
        wrapper.eq(OptionFamilyPo::getRowValid, true);
        return optionFamilyMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<OptionFamily> findAll(int page, int size, boolean includeInactive, String category) {
        Page<OptionFamilyPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<OptionFamilyPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionFamilyPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(OptionFamilyPo::getStatus, "ACTIVE");
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(OptionFamilyPo::getCategory, category);
        }
        wrapper.orderByDesc(OptionFamilyPo::getCreateTime);
        Page<OptionFamilyPo> result = optionFamilyMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(optionFamilyConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive, String category) {
        LambdaQueryWrapper<OptionFamilyPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionFamilyPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(OptionFamilyPo::getStatus, "ACTIVE");
        }
        if (category != null && !category.isBlank()) {
            wrapper.eq(OptionFamilyPo::getCategory, category);
        }
        return optionFamilyMapper.selectCount(wrapper);
    }

    @Override
    public void delete(OptionFamily optionFamily) {
        OptionFamilyPo po = optionFamilyConverter.toPo(optionFamily);
        optionFamilyMapper.updateById(po);
    }

    @Override
    public List<OptionFamilyHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<OptionFamilyHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OptionFamilyHistoryPo::getCode, code);
        wrapper.eq(OptionFamilyHistoryPo::getRowValid, true);
        wrapper.orderByDesc(OptionFamilyHistoryPo::getVersion);
        List<OptionFamilyHistoryPo> poList = optionFamilyHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(optionFamilyHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
