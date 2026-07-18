package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PartHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PartConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.PartHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PartHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PartMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 零件仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class PartRepositoryImpl implements PartRepository {

    private final PartMapper partMapper;
    private final PartConverter partConverter;
    private final PartHistoryMapper partHistoryMapper;
    private final PartHistoryConverter partHistoryConverter;

    @Override
    public Part save(Part part, String operationType) {
        PartPo po = partConverter.toPo(part);
        if (po.getId() == null) {
            partMapper.insert(po);
        } else {
            partMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType, false);
        }
        return partConverter.toDomain(po);
    }

    @Override
    public Optional<Part> findByCode(String code) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getCode, code);
        wrapper.eq(PartPo::getRowValid, true);
        PartPo po = partMapper.selectOne(wrapper);
        return Optional.ofNullable(partConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getCode, code);
        wrapper.eq(PartPo::getRowValid, true);
        return partMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsBySubstitutePartCode(String substitutePartCode) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getSubstitutePartCode, substitutePartCode);
        wrapper.eq(PartPo::getRowValid, true);
        return partMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(Part part, String operator, boolean forceDelete) {
        PartPo po = partConverter.toPo(part);
        insertHistory(po, "DELETE", forceDelete);
        partMapper.deleteById(po.getId());
    }

    @Override
    public List<Part> list(String keyword, String categoryCode, String partType, String vehicleNodeCode,
                           String supplierCode, String lifecycleStage, Boolean isSoftware, Boolean isAssembly,
                           String status, int page, int size) {
        Page<PartPo> pageParam = new Page<>(page, size);
        QueryWrapper<PartPo> wrapper = buildListWrapper(keyword, categoryCode, partType, vehicleNodeCode,
                supplierCode, lifecycleStage, isSoftware, isAssembly, status);
        wrapper.orderByDesc("create_time");
        Page<PartPo> result = partMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String keyword, String categoryCode, String partType, String vehicleNodeCode,
                      String supplierCode, String lifecycleStage, Boolean isSoftware, Boolean isAssembly, String status) {
        QueryWrapper<PartPo> wrapper = buildListWrapper(keyword, categoryCode, partType, vehicleNodeCode,
                supplierCode, lifecycleStage, isSoftware, isAssembly, status);
        return partMapper.selectCount(wrapper);
    }

    @Override
    public List<Part> listAllActive(Boolean isSoftware, Boolean isAssembly) {
        QueryWrapper<PartPo> wrapper = new QueryWrapper<>();
        wrapper.eq("row_valid", true);
        wrapper.eq("status", "ACTIVE");
        if (isSoftware != null) {
            wrapper.eq("is_software", isSoftware);
        }
        if (isAssembly != null) {
            wrapper.eq("is_assembly", isAssembly);
        }
        wrapper.orderByAsc("code");
        return partMapper.selectList(wrapper).stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Part> snapshot(boolean includeInactive, int page, int size) {
        Page<PartPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PartPo::getStatus, "ACTIVE");
        }
        wrapper.orderByAsc(PartPo::getId);
        Page<PartPo> result = partMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long snapshotCount(boolean includeInactive) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(PartPo::getStatus, "ACTIVE");
        }
        return partMapper.selectCount(wrapper);
    }

    @Override
    public List<Part> listByCategory(String categoryCode) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getRowValid, true);
        wrapper.eq(PartPo::getStatus, "ACTIVE");
        wrapper.eq(PartPo::getCategoryCode, categoryCode);
        wrapper.orderByAsc(PartPo::getCode);
        return partMapper.selectList(wrapper).stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Part> listByVehicleNode(String vehicleNodeCode) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getRowValid, true);
        wrapper.eq(PartPo::getStatus, "ACTIVE");
        wrapper.eq(PartPo::getVehicleNodeCode, vehicleNodeCode);
        wrapper.orderByAsc(PartPo::getCode);
        return partMapper.selectList(wrapper).stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Part> listBySupplier(String supplierCode) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getRowValid, true);
        wrapper.eq(PartPo::getStatus, "ACTIVE");
        wrapper.eq(PartPo::getSupplierCode, supplierCode);
        wrapper.orderByAsc(PartPo::getCode);
        return partMapper.selectList(wrapper).stream()
                .map(partConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PartHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<PartHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartHistoryPo::getCode, code);
        wrapper.orderByDesc(PartHistoryPo::getVersion);
        return partHistoryMapper.selectList(wrapper).stream()
                .map(partHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Part> findLatestByBaseNo(String baseNo) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getBaseNo, baseNo);
        wrapper.eq(PartPo::getRowValid, true);
        wrapper.orderByDesc(PartPo::getCode);
        wrapper.last("LIMIT 1");
        PartPo po = partMapper.selectOne(wrapper);
        return Optional.ofNullable(partConverter.toDomain(po));
    }

    private QueryWrapper<PartPo> buildListWrapper(String keyword, String categoryCode, String partType,
                                                      String vehicleNodeCode, String supplierCode,
                                                      String lifecycleStage, Boolean isSoftware, Boolean isAssembly,
                                                      String status) {
        QueryWrapper<PartPo> wrapper = new QueryWrapper<>();
        wrapper.eq("row_valid", true);
        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + keyword.trim() + "%";
            wrapper.and(w -> w.like("code", pattern)
                    .or().like("name", pattern)
                    .or().like("name_local", pattern));
        }
        if (categoryCode != null && !categoryCode.isBlank()) {
            wrapper.eq("category_code", categoryCode);
        }
        if (partType != null && !partType.isBlank()) {
            wrapper.eq("part_type", partType);
        }
        if (vehicleNodeCode != null && !vehicleNodeCode.isBlank()) {
            wrapper.eq("vehicle_node_code", vehicleNodeCode);
        }
        if (supplierCode != null && !supplierCode.isBlank()) {
            wrapper.eq("supplier_code", supplierCode);
        }
        if (lifecycleStage != null && !lifecycleStage.isBlank()) {
            wrapper.eq("lifecycle_stage", lifecycleStage);
        }
        if (isSoftware != null) {
            wrapper.eq("is_software", isSoftware);
        }
        if (isAssembly != null) {
            wrapper.eq("is_assembly", isAssembly);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq("status", status);
        } else {
            wrapper.eq("status", "ACTIVE");
        }
        return wrapper;
    }

    private void insertHistory(PartPo po, String operationType, boolean forceDelete) {
        Date now = new Date();
        PartHistoryPo historyPo = PartHistoryPo.builder()
                .entityId(po.getId())
                .operationType(operationType)
                .snapshotTime(now)
                .operator(po.getModifyBy())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .categoryCode(po.getCategoryCode())
                .partType(po.getPartType())
                .vehicleNodeCode(po.getVehicleNodeCode())
                .supplierCode(po.getSupplierCode())
                .isSoftware(po.getIsSoftware())
                .fotaUpgradeable(po.getFotaUpgradeable())
                .isSafetyCritical(po.getIsSafetyCritical())
                .isKeyPart(po.getIsKeyPart())
                .isRegulatoryPart(po.getIsRegulatoryPart())
                .isFramePart(po.getIsFramePart())
                .isAccuratelyTraced(po.getIsAccuratelyTraced())
                .ffaCode(po.getFfaCode())
                .ffaDesc(po.getFfaDesc())
                .isDigitate(po.getIsDigitate())
                .initialModel(po.getInitialModel())
                .uom(po.getUom())
                .drawingNo(po.getDrawingNo())
                .drawingVersion(po.getDrawingVersion())
                .weight(po.getWeight())
                .weightUom(po.getWeightUom())
                .lifecycleStage(po.getLifecycleStage())
                .substitutePartCode(po.getSubstitutePartCode())
                .productionCode(po.getProductionCode())
                .firstProductionDate(po.getFirstProductionDate())
                .designer(po.getDesigner())
                .designerDept(po.getDesignerDept())
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
        partHistoryMapper.insert(historyPo);
    }
}
