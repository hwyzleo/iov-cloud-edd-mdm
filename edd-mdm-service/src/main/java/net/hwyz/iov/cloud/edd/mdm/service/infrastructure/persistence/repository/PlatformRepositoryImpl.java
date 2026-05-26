package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PlatformConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PlatformMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformPo;
import org.springframework.stereotype.Repository;

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

    @Override
    public Platform save(Platform platform) {
        PlatformPo po = platformConverter.toPo(platform);
        if (po.getId() == null) {
            platformMapper.insert(po);
        } else {
            platformMapper.updateById(po);
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
}
