package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Series;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SeriesRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.SeriesConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SeriesMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SeriesPo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车系仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SeriesRepositoryImpl implements SeriesRepository {

    private final SeriesMapper seriesMapper;
    private final SeriesConverter seriesConverter;

    @Override
    public Series save(Series series) {
        SeriesPo po = seriesConverter.toPo(series);
        if (po.getId() == null) {
            seriesMapper.insert(po);
        } else {
            seriesMapper.updateById(po);
        }
        return seriesConverter.toDomain(po);
    }

    @Override
    public Optional<Series> findById(Long id) {
        SeriesPo po = seriesMapper.selectById(id);
        return Optional.ofNullable(seriesConverter.toDomain(po));
    }

    @Override
    public Optional<Series> findByCode(String code) {
        LambdaQueryWrapper<SeriesPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeriesPo::getCode, code);
        wrapper.eq(SeriesPo::getRowValid, true);
        SeriesPo po = seriesMapper.selectOne(wrapper);
        return Optional.ofNullable(seriesConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<SeriesPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeriesPo::getCode, code);
        wrapper.eq(SeriesPo::getRowValid, true);
        return seriesMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Series> findAll(int page, int size, String brandCode, boolean includeInactive) {
        Page<SeriesPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SeriesPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeriesPo::getRowValid, true);
        if (StringUtils.hasText(brandCode)) {
            wrapper.eq(SeriesPo::getBrandCode, brandCode);
        }
        if (!includeInactive) {
            wrapper.eq(SeriesPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(SeriesPo::getCreateTime);
        Page<SeriesPo> result = seriesMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(seriesConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String brandCode, boolean includeInactive) {
        LambdaQueryWrapper<SeriesPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeriesPo::getRowValid, true);
        if (StringUtils.hasText(brandCode)) {
            wrapper.eq(SeriesPo::getBrandCode, brandCode);
        }
        if (!includeInactive) {
            wrapper.eq(SeriesPo::getStatus, "ACTIVE");
        }
        return seriesMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Series series) {
        SeriesPo po = seriesConverter.toPo(series);
        seriesMapper.updateById(po);
    }
}
