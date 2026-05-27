package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlatformHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PlatformConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PlatformHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PlatformHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PlatformMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 平台仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class PlatformRepositoryImpl implements PlatformRepository {

    private final PlatformMapper platformMapper;
    private final PlatformConverter platformConverter;
    private final PlatformHistoryMapper platformHistoryMapper;
    private final PlatformHistoryConverter platformHistoryConverter;

    @Override
    public Platform save(Platform platform, String operationType) {
        PlatformPo po = platformConverter.toPo(platform);
        if (po.getId() == null) {
            platformMapper.insert(po);
        } else {
            platformMapper.updateById(po);
        }
        if (operationType != null) {
            PlatformHistoryPo historyPo = PlatformHistoryPo.builder()
                    .entityId(po.getId())
                    .code(po.getCode())
                    .name(po.getName())
                    .nameLocal(po.getNameLocal())
                    .platformType(po.getPlatformType())
                    .architecture(po.getArchitecture())
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
            platformHistoryMapper.insert(historyPo);
        }
        return platformConverter.toDomain(po);
    }

    @Override
    public Optional<Platform> findById(Long id) {
        PlatformPo po = platformMapper.selectById(id);
        return Optional.ofNullable(platformConverter.toDomain(po));
    }

    @Override
    public Optional<Platform> findByCode(String code) {
        LambdaQueryWrapper<PlatformPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformPo::getCode, code);
        wrapper.eq(PlatformPo::getRowValid, true);
        PlatformPo po = platformMapper.selectOne(wrapper);
        return Optional.ofNullable(platformConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<PlatformPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformPo::getCode, code);
        wrapper.eq(PlatformPo::getRowValid, true);
        return platformMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Platform> findAll(int page, int size, boolean includeInactive) {
        Page<PlatformPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PlatformPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PlatformPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(PlatformPo::getCreateTime);
        Page<PlatformPo> result = platformMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(platformConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive) {
        LambdaQueryWrapper<PlatformPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PlatformPo::getStatus, "ACTIVE");
        }
        return platformMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Platform platform) {
        PlatformPo po = platformConverter.toPo(platform);
        platformMapper.updateById(po);
    }

    @Override
    public List<PlatformHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<PlatformHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlatformHistoryPo::getCode, code);
        wrapper.eq(PlatformHistoryPo::getRowValid, true);
        wrapper.orderByDesc(PlatformHistoryPo::getVersion);
        List<PlatformHistoryPo> poList = platformHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(platformHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
