package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ModelHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ModelRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.ModelConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.ModelHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ModelHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ModelMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ModelHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ModelPo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车型仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class ModelRepositoryImpl implements ModelRepository {

    private final ModelMapper modelMapper;
    private final ModelConverter modelConverter;
    private final ModelHistoryMapper modelHistoryMapper;
    private final ModelHistoryConverter modelHistoryConverter;

    @Override
    public Model save(Model model, String operationType) {
        ModelPo po = modelConverter.toPo(model);
        if (po.getId() == null) {
            modelMapper.insert(po);
        } else {
            modelMapper.updateById(po);
        }
        if (operationType != null) {
            ModelHistoryPo historyPo = ModelHistoryPo.builder()
                    .entityId(po.getId())
                    .code(po.getCode())
                    .name(po.getName())
                    .nameLocal(po.getNameLocal())
                    .carLineCode(po.getCarLineCode())
                    .platformCode(po.getPlatformCode())
                    .modelYear(po.getModelYear())
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
            modelHistoryMapper.insert(historyPo);
        }
        return modelConverter.toDomain(po);
    }

    @Override
    public Optional<Model> findById(Long id) {
        ModelPo po = modelMapper.selectById(id);
        return Optional.ofNullable(modelConverter.toDomain(po));
    }

    @Override
    public Optional<Model> findByCode(String code) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getCode, code);
        wrapper.eq(ModelPo::getRowValid, true);
        ModelPo po = modelMapper.selectOne(wrapper);
        return Optional.ofNullable(modelConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getCode, code);
        wrapper.eq(ModelPo::getRowValid, true);
        return modelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCarLineCode(String carLineCode) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getCarLineCode, carLineCode);
        wrapper.eq(ModelPo::getRowValid, true);
        return modelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByCarLineCodeAndStatusActive(String carLineCode) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getCarLineCode, carLineCode);
        wrapper.eq(ModelPo::getStatus, "ACTIVE");
        wrapper.eq(ModelPo::getRowValid, true);
        return modelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByPlatformCodeAndStatusActive(String platformCode) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getPlatformCode, platformCode);
        wrapper.eq(ModelPo::getStatus, "ACTIVE");
        wrapper.eq(ModelPo::getRowValid, true);
        return modelMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Model> findAll(int page, int size, String carLineCode, String platformCode, boolean includeInactive) {
        Page<ModelPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getRowValid, true);
        if (StringUtils.hasText(carLineCode)) {
            wrapper.eq(ModelPo::getCarLineCode, carLineCode);
        }
        if (StringUtils.hasText(platformCode)) {
            wrapper.eq(ModelPo::getPlatformCode, platformCode);
        }
        if (!includeInactive) {
            wrapper.eq(ModelPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(ModelPo::getCreateTime);
        Page<ModelPo> result = modelMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(modelConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String carLineCode, String platformCode, boolean includeInactive) {
        LambdaQueryWrapper<ModelPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelPo::getRowValid, true);
        if (StringUtils.hasText(carLineCode)) {
            wrapper.eq(ModelPo::getCarLineCode, carLineCode);
        }
        if (StringUtils.hasText(platformCode)) {
            wrapper.eq(ModelPo::getPlatformCode, platformCode);
        }
        if (!includeInactive) {
            wrapper.eq(ModelPo::getStatus, "ACTIVE");
        }
        return modelMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Model model) {
        ModelPo po = modelConverter.toPo(model);
        modelMapper.updateById(po);
    }

    @Override
    public List<ModelHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<ModelHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelHistoryPo::getCode, code);
        wrapper.eq(ModelHistoryPo::getRowValid, true);
        wrapper.orderByDesc(ModelHistoryPo::getVersion);
        List<ModelHistoryPo> poList = modelHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(modelHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
