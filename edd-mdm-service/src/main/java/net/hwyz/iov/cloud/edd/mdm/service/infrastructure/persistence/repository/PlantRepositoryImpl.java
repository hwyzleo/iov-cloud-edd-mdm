package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PlantConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PlantHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PlantHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PlantMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlantHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlantPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工厂仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class PlantRepositoryImpl implements PlantRepository {

    private final PlantMapper plantMapper;
    private final PlantConverter plantConverter;
    private final PlantHistoryMapper plantHistoryMapper;
    private final PlantHistoryConverter plantHistoryConverter;

    @Override
    public Plant save(Plant plant, String operationType) {
        PlantPo po = plantConverter.toPo(plant);
        if (po.getId() == null) {
            plantMapper.insert(po);
        } else {
            plantMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType, false);
        }
        return plantConverter.toDomain(po);
    }

    @Override
    public Optional<Plant> findById(Long id) {
        PlantPo po = plantMapper.selectById(id);
        return Optional.ofNullable(plantConverter.toDomain(po));
    }

    @Override
    public Optional<Plant> findByCode(String code) {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getCode, code);
        wrapper.eq(PlantPo::getRowValid, true);
        PlantPo po = plantMapper.selectOne(wrapper);
        return Optional.ofNullable(plantConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getCode, code);
        wrapper.eq(PlantPo::getRowValid, true);
        return plantMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(Plant plant, String operator, boolean forceDelete) {
        PlantPo po = plantConverter.toPo(plant);
        insertHistory(po, "DELETE", forceDelete);
        plantMapper.deleteById(po.getId());
    }

    @Override
    public List<Plant> list(String plantType, String country, String status, int page, int size) {
        Page<PlantPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PlantPo> wrapper = buildListWrapper(plantType, country, status);
        wrapper.orderByDesc(PlantPo::getCreateTime);
        Page<PlantPo> result = plantMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(plantConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String plantType, String country, String status) {
        LambdaQueryWrapper<PlantPo> wrapper = buildListWrapper(plantType, country, status);
        return plantMapper.selectCount(wrapper);
    }

    @Override
    public List<Plant> listAllActive() {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getRowValid, true);
        wrapper.eq(PlantPo::getStatus, "ACTIVE");
        wrapper.orderByAsc(PlantPo::getCode);
        return plantMapper.selectList(wrapper).stream()
                .map(plantConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Plant> snapshot(boolean includeInactive, int page, int size) {
        Page<PlantPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PlantPo::getStatus, "ACTIVE");
        }
        wrapper.orderByAsc(PlantPo::getId);
        Page<PlantPo> result = plantMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(plantConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long snapshotCount(boolean includeInactive) {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PlantPo::getStatus, "ACTIVE");
        }
        return plantMapper.selectCount(wrapper);
    }

    @Override
    public List<Plant> listByPlantType(String plantType) {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getRowValid, true);
        wrapper.eq(PlantPo::getStatus, "ACTIVE");
        wrapper.eq(PlantPo::getPlantType, plantType);
        wrapper.orderByAsc(PlantPo::getCode);
        return plantMapper.selectList(wrapper).stream()
                .map(plantConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlantHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<PlantHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantHistoryPo::getCode, code);
        wrapper.orderByDesc(PlantHistoryPo::getVersion);
        return plantHistoryMapper.selectList(wrapper).stream()
                .map(plantHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<PlantPo> buildListWrapper(String plantType, String country, String status) {
        LambdaQueryWrapper<PlantPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlantPo::getRowValid, true);
        if (plantType != null && !plantType.isBlank()) {
            wrapper.eq(PlantPo::getPlantType, plantType);
        }
        if (country != null && !country.isBlank()) {
            wrapper.eq(PlantPo::getCountry, country);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(PlantPo::getStatus, status);
        } else {
            wrapper.eq(PlantPo::getStatus, "ACTIVE");
        }
        return wrapper;
    }

    private void insertHistory(PlantPo po, String operationType, boolean forceDelete) {
        Date now = new Date();
        PlantHistoryPo historyPo = PlantHistoryPo.builder()
                .entityId(po.getId())
                .operationType(operationType)
                .snapshotTime(now)
                .operator(po.getModifyBy())
                .code(po.getCode())
                .name(po.getName())
                .nameEn(po.getNameEn())
                .shortName(po.getShortName())
                .description(po.getDescription())
                .plantType(po.getPlantType())
                .legalEntityCode(po.getLegalEntityCode())
                .costCenterCode(po.getCostCenterCode())
                .country(po.getCountry())
                .province(po.getProvince())
                .city(po.getCity())
                .address(po.getAddress())
                .longitude(po.getLongitude())
                .latitude(po.getLatitude())
                .timezone(po.getTimezone())
                .annualCapacity(po.getAnnualCapacity())
                .productionLines(po.getProductionLines())
                .operationalStartDate(po.getOperationalStartDate())
                .mesInstance(po.getMesInstance())
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
                .createBy(po.getModifyBy())
                .createTime(now)
                .modifyBy(po.getModifyBy())
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
        plantHistoryMapper.insert(historyPo);
    }
}
