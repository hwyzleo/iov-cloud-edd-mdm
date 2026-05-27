package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VariantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.VariantConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.VariantHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ModelMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.VariantHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.VariantMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ModelPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantPo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 版本仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class VariantRepositoryImpl implements VariantRepository {

    private final VariantMapper variantMapper;
    private final VariantConverter variantConverter;
    private final VariantHistoryMapper variantHistoryMapper;
    private final VariantHistoryConverter variantHistoryConverter;
    private final ModelMapper modelMapper;

    @Override
    public Variant save(Variant variant) {
        VariantPo po = variantConverter.toPo(variant);
        if (po.getId() == null) {
            variantMapper.insert(po);
        } else {
            variantMapper.updateById(po);
        }
        return variantConverter.toDomain(po);
    }

    @Override
    public Optional<Variant> findById(Long id) {
        VariantPo po = variantMapper.selectById(id);
        return Optional.ofNullable(variantConverter.toDomain(po));
    }

    @Override
    public Optional<Variant> findByCode(String code) {
        LambdaQueryWrapper<VariantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantPo::getCode, code);
        wrapper.eq(VariantPo::getRowValid, true);
        VariantPo po = variantMapper.selectOne(wrapper);
        return Optional.ofNullable(variantConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<VariantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantPo::getCode, code);
        wrapper.eq(VariantPo::getRowValid, true);
        return variantMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByModelCode(String modelCode) {
        LambdaQueryWrapper<VariantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantPo::getModelCode, modelCode);
        wrapper.eq(VariantPo::getRowValid, true);
        return variantMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Variant> findAll(int page, int size, String modelCode, String carLineCode, String platformCode, boolean includeInactive) {
        // 如果按carLineCode或platformCode过滤，先查出对应的modelCode列表
        List<String> modelCodes = resolveModelCodes(modelCode, carLineCode, platformCode);
        if (modelCodes != null && modelCodes.isEmpty()) {
            return Collections.emptyList();
        }

        Page<VariantPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VariantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantPo::getRowValid, true);
        if (modelCodes != null) {
            wrapper.in(VariantPo::getModelCode, modelCodes);
        }
        if (!includeInactive) {
            wrapper.eq(VariantPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(VariantPo::getCreateTime);
        Page<VariantPo> result = variantMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(variantConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String modelCode, String carLineCode, String platformCode, boolean includeInactive) {
        List<String> modelCodes = resolveModelCodes(modelCode, carLineCode, platformCode);
        if (modelCodes != null && modelCodes.isEmpty()) {
            return 0;
        }

        LambdaQueryWrapper<VariantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantPo::getRowValid, true);
        if (modelCodes != null) {
            wrapper.in(VariantPo::getModelCode, modelCodes);
        }
        if (!includeInactive) {
            wrapper.eq(VariantPo::getStatus, "ACTIVE");
        }
        return variantMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Variant variant) {
        VariantPo po = variantConverter.toPo(variant);
        variantMapper.updateById(po);
    }

    @Override
    public List<VariantHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<VariantHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VariantHistoryPo::getCode, code);
        wrapper.eq(VariantHistoryPo::getRowValid, true);
        wrapper.orderByDesc(VariantHistoryPo::getVersion);
        List<VariantHistoryPo> poList = variantHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(variantHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    private List<String> resolveModelCodes(String modelCode, String carLineCode, String platformCode) {
        if (StringUtils.hasText(modelCode)) {
            return List.of(modelCode);
        }
        if (StringUtils.hasText(carLineCode) || StringUtils.hasText(platformCode)) {
            LambdaQueryWrapper<ModelPo> modelWrapper = new LambdaQueryWrapper<>();
            modelWrapper.eq(ModelPo::getRowValid, true);
            if (StringUtils.hasText(carLineCode)) {
                modelWrapper.eq(ModelPo::getCarLineCode, carLineCode);
            }
            if (StringUtils.hasText(platformCode)) {
                modelWrapper.eq(ModelPo::getPlatformCode, platformCode);
            }
            modelWrapper.select(ModelPo::getCode);
            List<ModelPo> models = modelMapper.selectList(modelWrapper);
            return models.stream().map(ModelPo::getCode).collect(Collectors.toList());
        }
        return null;
    }
}
