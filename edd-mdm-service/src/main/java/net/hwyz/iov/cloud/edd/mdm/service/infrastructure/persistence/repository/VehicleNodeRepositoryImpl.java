package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VehicleNodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.VehicleNodeConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.VehicleNodeHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.VehicleNodeHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.VehicleNodeMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VehicleNodeHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VehicleNodePo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 车载节点仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class VehicleNodeRepositoryImpl implements VehicleNodeRepository {

    private final VehicleNodeMapper vehicleNodeMapper;
    private final VehicleNodeConverter vehicleNodeConverter;
    private final VehicleNodeHistoryMapper vehicleNodeHistoryMapper;
    private final VehicleNodeHistoryConverter vehicleNodeHistoryConverter;

    @Override
    public VehicleNode save(VehicleNode vehicleNode, String operationType) {
        VehicleNodePo po = vehicleNodeConverter.toPo(vehicleNode);
        if (po.getId() == null) {
            vehicleNodeMapper.insert(po);
        } else {
            vehicleNodeMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType, false);
        }
        return vehicleNodeConverter.toDomain(po);
    }

    @Override
    public Optional<VehicleNode> findById(Long id) {
        VehicleNodePo po = vehicleNodeMapper.selectById(id);
        return Optional.ofNullable(vehicleNodeConverter.toDomain(po));
    }

    @Override
    public Optional<VehicleNode> findByCode(String nodeCode) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getNodeCode, nodeCode);
        wrapper.eq(VehicleNodePo::getRowValid, true);
        VehicleNodePo po = vehicleNodeMapper.selectOne(wrapper);
        return Optional.ofNullable(vehicleNodeConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String nodeCode) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getNodeCode, nodeCode);
        wrapper.eq(VehicleNodePo::getRowValid, true);
        return vehicleNodeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(VehicleNode vehicleNode, String operator, boolean forceDelete) {
        VehicleNodePo po = vehicleNodeConverter.toPo(vehicleNode);
        insertHistory(po, "DELETE", forceDelete);
        vehicleNodeMapper.deleteById(po.getId());
    }

    @Override
    public List<VehicleNode> list(String nodeType, String functionalDomain, String otaSupportType,
                                  Boolean isCoreNode, String status, int page, int size) {
        Page<VehicleNodePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VehicleNodePo> wrapper = buildListWrapper(nodeType, functionalDomain, otaSupportType, isCoreNode, status);
        wrapper.orderByDesc(VehicleNodePo::getCreateTime);
        Page<VehicleNodePo> result = vehicleNodeMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(vehicleNodeConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String nodeType, String functionalDomain, String otaSupportType,
                      Boolean isCoreNode, String status) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = buildListWrapper(nodeType, functionalDomain, otaSupportType, isCoreNode, status);
        return vehicleNodeMapper.selectCount(wrapper);
    }

    @Override
    public List<VehicleNode> listAllActive() {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getRowValid, true);
        wrapper.eq(VehicleNodePo::getStatus, "ACTIVE");
        wrapper.orderByAsc(VehicleNodePo::getNodeCode);
        return vehicleNodeMapper.selectList(wrapper).stream()
                .map(vehicleNodeConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleNode> snapshot(boolean includeInactive, int page, int size) {
        Page<VehicleNodePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(VehicleNodePo::getStatus, "ACTIVE");
        }
        wrapper.orderByAsc(VehicleNodePo::getId);
        Page<VehicleNodePo> result = vehicleNodeMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(vehicleNodeConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long snapshotCount(boolean includeInactive) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(VehicleNodePo::getStatus, "ACTIVE");
        }
        return vehicleNodeMapper.selectCount(wrapper);
    }

    @Override
    public List<VehicleNode> listByOtaType(OtaSupportType otaSupportType) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getRowValid, true);
        wrapper.eq(VehicleNodePo::getStatus, "ACTIVE");
        wrapper.eq(VehicleNodePo::getOtaSupportType, otaSupportType.name());
        wrapper.orderByAsc(VehicleNodePo::getNodeCode);
        return vehicleNodeMapper.selectList(wrapper).stream()
                .map(vehicleNodeConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByDeviceCategory(String deviceCategoryCode) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getDeviceCategory, deviceCategoryCode);
        wrapper.eq(VehicleNodePo::getRowValid, true);
        return vehicleNodeMapper.selectCount(wrapper);
    }

    @Override
    public List<VehicleNodeHistory> findHistoryByCode(String nodeCode) {
        LambdaQueryWrapper<VehicleNodeHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodeHistoryPo::getNodeCode, nodeCode);
        wrapper.eq(VehicleNodeHistoryPo::getRowValid, true);
        wrapper.orderByDesc(VehicleNodeHistoryPo::getVersion);
        return vehicleNodeHistoryMapper.selectList(wrapper).stream()
                .map(vehicleNodeHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<VehicleNodePo> buildListWrapper(String nodeType, String functionalDomain,
                                                               String otaSupportType, Boolean isCoreNode, String status) {
        LambdaQueryWrapper<VehicleNodePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleNodePo::getRowValid, true);
        if (nodeType != null && !nodeType.isBlank()) {
            wrapper.eq(VehicleNodePo::getNodeType, nodeType);
        }
        if (functionalDomain != null && !functionalDomain.isBlank()) {
            wrapper.eq(VehicleNodePo::getFunctionalDomain, functionalDomain);
        }
        if (otaSupportType != null && !otaSupportType.isBlank()) {
            wrapper.eq(VehicleNodePo::getOtaSupportType, otaSupportType);
        }
        if (isCoreNode != null) {
            wrapper.eq(VehicleNodePo::getIsCoreNode, isCoreNode);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(VehicleNodePo::getStatus, status);
        } else {
            wrapper.eq(VehicleNodePo::getStatus, "ACTIVE");
        }
        return wrapper;
    }

    private void insertHistory(VehicleNodePo po, String operationType, boolean forceDelete) {
        Date now = new Date();
        VehicleNodeHistoryPo historyPo = VehicleNodeHistoryPo.builder()
                .entityId(po.getId())
                .nodeCode(po.getNodeCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .nodeType(po.getNodeType())
                .functionalDomain(po.getFunctionalDomain())
                .deviceCategory(po.getDeviceCategory())
                .isCoreNode(po.getIsCoreNode())
                .otaSupportType(po.getOtaSupportType())
                .hsmCapability(po.getHsmCapability())
                .securityLevel(po.getSecurityLevel())
                .source(po.getSource())
                .externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion())
                .lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(po.getStatus())
                .operationType(operationType)
                .snapshotTime(now)
                .operator(po.getModifyBy())
                .forceDelete(forceDelete)
                .createBy(po.getModifyBy())
                .createTime(now)
                .modifyBy(po.getModifyBy())
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
        vehicleNodeHistoryMapper.insert(historyPo);
    }
}
