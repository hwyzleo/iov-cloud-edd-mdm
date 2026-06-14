package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.DeviceCategoryHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.DeviceCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.DeviceCategoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.DeviceCategoryHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.DeviceCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.DeviceCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.DeviceCategoryHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.DeviceCategoryPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 设备类别仓储实现（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class DeviceCategoryRepositoryImpl implements DeviceCategoryRepository {

    private final DeviceCategoryMapper deviceCategoryMapper;
    private final DeviceCategoryConverter deviceCategoryConverter;
    private final DeviceCategoryHistoryMapper deviceCategoryHistoryMapper;
    private final DeviceCategoryHistoryConverter deviceCategoryHistoryConverter;

    @Override
    public DeviceCategory save(DeviceCategory category, String operationType) {
        DeviceCategoryPo po = deviceCategoryConverter.toPo(category);
        if (po.getId() == null) {
            deviceCategoryMapper.insert(po);
        } else {
            deviceCategoryMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType);
        }
        return deviceCategoryConverter.toDomain(po);
    }

    @Override
    public Optional<DeviceCategory> findByCode(String code) {
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategoryPo::getCode, code);
        wrapper.eq(DeviceCategoryPo::getRowValid, true);
        DeviceCategoryPo po = deviceCategoryMapper.selectOne(wrapper);
        return Optional.ofNullable(deviceCategoryConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategoryPo::getCode, code);
        wrapper.eq(DeviceCategoryPo::getRowValid, true);
        return deviceCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(DeviceCategory category, String operator) {
        DeviceCategoryPo po = deviceCategoryConverter.toPo(category);
        insertHistory(po, "DELETE");
        deviceCategoryMapper.deleteById(po.getId());
    }

    @Override
    public List<DeviceCategory> list(String status, int page, int size) {
        Page<DeviceCategoryPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = buildListWrapper(status);
        wrapper.orderByAsc(DeviceCategoryPo::getSortOrder);
        wrapper.orderByAsc(DeviceCategoryPo::getCode);
        Page<DeviceCategoryPo> result = deviceCategoryMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(deviceCategoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String status) {
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = buildListWrapper(status);
        return deviceCategoryMapper.selectCount(wrapper);
    }

    @Override
    public List<DeviceCategory> listAllActive() {
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategoryPo::getRowValid, true);
        wrapper.eq(DeviceCategoryPo::getStatus, "ACTIVE");
        wrapper.orderByAsc(DeviceCategoryPo::getSortOrder);
        wrapper.orderByAsc(DeviceCategoryPo::getCode);
        return deviceCategoryMapper.selectList(wrapper).stream()
                .map(deviceCategoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceCategoryHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<DeviceCategoryHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategoryHistoryPo::getCode, code);
        wrapper.orderByDesc(DeviceCategoryHistoryPo::getVersion);
        return deviceCategoryHistoryMapper.selectList(wrapper).stream()
                .map(deviceCategoryHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<DeviceCategoryPo> buildListWrapper(String status) {
        LambdaQueryWrapper<DeviceCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceCategoryPo::getRowValid, true);
        if (status != null && !status.isBlank()) {
            wrapper.eq(DeviceCategoryPo::getStatus, status);
        } else {
            wrapper.eq(DeviceCategoryPo::getStatus, "ACTIVE");
        }
        return wrapper;
    }

    private void insertHistory(DeviceCategoryPo po, String operationType) {
        Date now = new Date();
        DeviceCategoryHistoryPo historyPo = DeviceCategoryHistoryPo.builder()
                .entityId(po.getId())
                .operationType(operationType)
                .snapshotTime(now)
                .operator(po.getModifyBy())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .sortOrder(po.getSortOrder())
                .source(po.getSource())
                .externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion())
                .lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(po.getStatus())
                .createBy(po.getModifyBy())
                .createTime(now)
                .modifyBy(po.getModifyBy())
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
        deviceCategoryHistoryMapper.insert(historyPo);
    }
}
