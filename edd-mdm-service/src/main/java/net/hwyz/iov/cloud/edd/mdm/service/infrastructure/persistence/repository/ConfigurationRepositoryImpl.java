package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ConfigurationHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.ConfigurationConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.ConfigurationHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ConfigurationHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.ConfigurationMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationPo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 配置仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class ConfigurationRepositoryImpl implements ConfigurationRepository {

    private final ConfigurationMapper configurationMapper;
    private final ConfigurationConverter configurationConverter;
    private final ConfigurationHistoryMapper configurationHistoryMapper;
    private final ConfigurationHistoryConverter configurationHistoryConverter;

    @Override
    public Configuration save(Configuration configuration, String operationType) {
        ConfigurationPo po = configurationConverter.toPo(configuration);
        if (po.getId() == null) {
            configurationMapper.insert(po);
        } else {
            configurationMapper.updateById(po);
        }
        if (operationType != null) {
            ConfigurationHistoryPo historyPo = ConfigurationHistoryPo.builder()
                    .entityId(po.getId())
                    .code(po.getCode())
                    .name(po.getName())
                    .nameLocal(po.getNameLocal())
                    .variantCode(po.getVariantCode())
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
            configurationHistoryMapper.insert(historyPo);
        }
        return configurationConverter.toDomain(po);
    }

    @Override
    public Optional<Configuration> findById(Long id) {
        ConfigurationPo po = configurationMapper.selectById(id);
        return Optional.ofNullable(configurationConverter.toDomain(po));
    }

    @Override
    public Optional<Configuration> findByCode(String code) {
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getCode, code);
        wrapper.eq(ConfigurationPo::getRowValid, true);
        ConfigurationPo po = configurationMapper.selectOne(wrapper);
        return Optional.ofNullable(configurationConverter.toDomain(po));
    }

    @Override
    public Optional<Configuration> findBySourceSystemAndSourceId(String sourceSystem, String sourceId) {
        if (sourceSystem == null || sourceId == null) {
            return Optional.empty();
        }
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getSourceSystem, sourceSystem);
        wrapper.eq(ConfigurationPo::getSourceId, sourceId);
        wrapper.eq(ConfigurationPo::getRowValid, true);
        wrapper.last("LIMIT 1");
        ConfigurationPo po = configurationMapper.selectOne(wrapper);
        return Optional.ofNullable(configurationConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getCode, code);
        wrapper.eq(ConfigurationPo::getRowValid, true);
        return configurationMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByVariantCode(String variantCode) {
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getVariantCode, variantCode);
        wrapper.eq(ConfigurationPo::getRowValid, true);
        return configurationMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Configuration> findAll(int page, int size, String variantCode, boolean includeInactive) {
        Page<ConfigurationPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getRowValid, true);
        if (StringUtils.hasText(variantCode)) {
            wrapper.eq(ConfigurationPo::getVariantCode, variantCode);
        }
        if (!includeInactive) {
            wrapper.eq(ConfigurationPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(ConfigurationPo::getCreateTime);
        Page<ConfigurationPo> result = configurationMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(configurationConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String variantCode, boolean includeInactive) {
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationPo::getRowValid, true);
        if (StringUtils.hasText(variantCode)) {
            wrapper.eq(ConfigurationPo::getVariantCode, variantCode);
        }
        if (!includeInactive) {
            wrapper.eq(ConfigurationPo::getStatus, "ACTIVE");
        }
        return configurationMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Configuration configuration) {
        ConfigurationPo po = configurationConverter.toPo(configuration);
        configurationMapper.updateById(po);
    }

    @Override
    public List<ConfigurationHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<ConfigurationHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConfigurationHistoryPo::getCode, code);
        wrapper.eq(ConfigurationHistoryPo::getRowValid, true);
        wrapper.orderByDesc(ConfigurationHistoryPo::getVersion);
        List<ConfigurationHistoryPo> poList = configurationHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(configurationHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Configuration> findByCodes(List<String> codes, boolean onlyActive) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<ConfigurationPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ConfigurationPo::getCode, codes);
        wrapper.eq(ConfigurationPo::getRowValid, true);
        if (onlyActive) {
            wrapper.eq(ConfigurationPo::getStatus, "ACTIVE");
        }
        List<ConfigurationPo> poList = configurationMapper.selectList(wrapper);
        return poList.stream()
                .map(configurationConverter::toDomain)
                .collect(Collectors.toList());
    }
}
