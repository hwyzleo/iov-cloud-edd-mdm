package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.CarLineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.CarLineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.CarLineConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.CarLineHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.CarLineHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.CarLineMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.CarLineHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.CarLinePo;
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
public class CarLineRepositoryImpl implements CarLineRepository {

    private final CarLineMapper carLineMapper;
    private final CarLineConverter carLineConverter;
    private final CarLineHistoryMapper carLineHistoryMapper;
    private final CarLineHistoryConverter carLineHistoryConverter;

    @Override
    public CarLine save(CarLine carLine) {
        CarLinePo po = carLineConverter.toPo(carLine);
        if (po.getId() == null) {
            carLineMapper.insert(po);
        } else {
            carLineMapper.updateById(po);
        }
        return carLineConverter.toDomain(po);
    }

    @Override
    public Optional<CarLine> findById(Long id) {
        CarLinePo po = carLineMapper.selectById(id);
        return Optional.ofNullable(carLineConverter.toDomain(po));
    }

    @Override
    public Optional<CarLine> findByCode(String code) {
        LambdaQueryWrapper<CarLinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarLinePo::getCode, code);
        wrapper.eq(CarLinePo::getRowValid, true);
        CarLinePo po = carLineMapper.selectOne(wrapper);
        return Optional.ofNullable(carLineConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<CarLinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarLinePo::getCode, code);
        wrapper.eq(CarLinePo::getRowValid, true);
        return carLineMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<CarLine> findAll(int page, int size, String brandCode, boolean includeInactive) {
        Page<CarLinePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CarLinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarLinePo::getRowValid, true);
        if (StringUtils.hasText(brandCode)) {
            wrapper.eq(CarLinePo::getBrandCode, brandCode);
        }
        if (!includeInactive) {
            wrapper.eq(CarLinePo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(CarLinePo::getCreateTime);
        Page<CarLinePo> result = carLineMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(carLineConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String brandCode, boolean includeInactive) {
        LambdaQueryWrapper<CarLinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarLinePo::getRowValid, true);
        if (StringUtils.hasText(brandCode)) {
            wrapper.eq(CarLinePo::getBrandCode, brandCode);
        }
        if (!includeInactive) {
            wrapper.eq(CarLinePo::getStatus, "ACTIVE");
        }
        return carLineMapper.selectCount(wrapper);
    }

    @Override
    public void delete(CarLine carLine) {
        CarLinePo po = carLineConverter.toPo(carLine);
        carLineMapper.updateById(po);
    }

    @Override
    public List<CarLineHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<CarLineHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CarLineHistoryPo::getCode, code);
        wrapper.eq(CarLineHistoryPo::getRowValid, true);
        wrapper.orderByDesc(CarLineHistoryPo::getVersion);
        List<CarLineHistoryPo> poList = carLineHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(carLineHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
