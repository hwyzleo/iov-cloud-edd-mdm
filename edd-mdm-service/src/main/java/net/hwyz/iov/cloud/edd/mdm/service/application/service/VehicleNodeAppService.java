package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.assembler.VehicleNodeDomainAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeDeleteCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VehicleNodeListByOtaTypeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VehicleNodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.VehicleNodeDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.VehicleNodeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VehicleNodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SecurityLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.VehicleNodeDeletionDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车载节点应用服务（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleNodeAppService {

    private final VehicleNodeRepository vehicleNodeRepository;
    private final VehicleNodeDeletionDomainService vehicleNodeDeletionDomainService;
    private final OutboxService outboxService;

    /**
     * 创建车载节点
     *
     * @param cmd 创建命令
     * @return 车载节点DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleNodeDto create(VehicleNodeCreateCmd cmd) {
        log.info("创建车载节点: {}", cmd.getNodeCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 校验 nodeCode 唯一
        if (vehicleNodeRepository.existsByCode(cmd.getNodeCode())) {
            VehicleNode existing = vehicleNodeRepository.findByCode(cmd.getNodeCode()).orElse(null);
            String existingStatus = existing != null && existing.getStatus() != null ? existing.getStatus().name() : "UNKNOWN";
            throw new VehicleNodeDuplicateCodeException(cmd.getNodeCode(), existingStatus);
        }

        VehicleNode node = VehicleNodeDomainAssembler.toDomain(cmd, createBy);
        node = vehicleNodeRepository.save(node, "CREATE");

        outboxService.publishVehicleNodeCreatedEvent(node);

        return VehicleNodeDomainAssembler.toDto(node);
    }

    /**
     * 更新车载节点
     *
     * @param nodeCode 业务主键
     * @param cmd      更新命令
     * @return 车载节点DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleNodeDto update(String nodeCode, VehicleNodeUpdateCmd cmd) {
        log.info("更新车载节点: {}", nodeCode);

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        VehicleNode node = vehicleNodeRepository.findByCode(nodeCode)
                .orElseThrow(() -> new VehicleNodeNotExistException("车载节点不存在: " + nodeCode));

        node.update(
                cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getNodeType() != null ? NodeType.valueOf(cmd.getNodeType()) : null,
                cmd.getFunctionalDomain() != null ? FunctionalDomain.valueOf(cmd.getFunctionalDomain()) : null,
                cmd.getDeviceCategory(), cmd.getIsCoreNode(),
                cmd.getOtaSupportType() != null ? OtaSupportType.valueOf(cmd.getOtaSupportType()) : null,
                cmd.getHsmCapability() != null ? HsmCapability.valueOf(cmd.getHsmCapability()) : null,
                cmd.getSecurityLevel() != null ? SecurityLevel.valueOf(cmd.getSecurityLevel()) : null,
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy
        );

        node = vehicleNodeRepository.save(node, "UPDATE");

        outboxService.publishVehicleNodeUpdatedEvent(node);

        return VehicleNodeDomainAssembler.toDto(node);
    }

    /**
     * 失效车载节点
     *
     * @param nodeCode 业务主键
     * @param operator 操作人
     * @return 车载节点DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public VehicleNodeDto deactivate(String nodeCode, String operator) {
        log.info("失效车载节点: {}", nodeCode);

        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        VehicleNode node = vehicleNodeRepository.findByCode(nodeCode)
                .orElseThrow(() -> new VehicleNodeNotExistException("车载节点不存在: " + nodeCode));

        node.deactivate(operator);

        node = vehicleNodeRepository.save(node, "DEACTIVATE");

        outboxService.publishVehicleNodeUpdatedEvent(node);

        return VehicleNodeDomainAssembler.toDto(node);
    }

    /**
     * 删除车载节点
     *
     * @param cmd 删除命令
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(VehicleNodeDeleteCmd cmd) {
        log.info("删除车载节点: {}", cmd.getNodeCode());

        String operator = cmd.getOperator();
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        VehicleNode node = vehicleNodeRepository.findByCode(cmd.getNodeCode())
                .orElseThrow(() -> new VehicleNodeNotExistException("车载节点不存在: " + cmd.getNodeCode()));

        vehicleNodeDeletionDomainService.checkAndDelete(node, operator, cmd.isForceDelete());

        outboxService.publishVehicleNodeDeletedEvent(node, cmd.isForceDelete());
    }

    /**
     * 根据nodeCode查询车载节点
     *
     * @param nodeCode 业务主键
     * @return 车载节点DTO
     */
    public VehicleNodeDto queryByCode(String nodeCode) {
        VehicleNode node = vehicleNodeRepository.findByCode(nodeCode)
                .orElseThrow(() -> new VehicleNodeNotExistException("车载节点不存在: " + nodeCode));
        return VehicleNodeDomainAssembler.toDto(node);
    }

    /**
     * 分页查询车载节点列表
     *
     * @param query 查询条件
     * @return 分页结果
     */
    public Page<VehicleNodeDto> list(VehicleNodeQuery query) {
        int page = query.getPage() != null ? query.getPage() : 1;
        int size = query.getSize() != null ? query.getSize() : 20;

        List<VehicleNode> nodes = vehicleNodeRepository.list(
                query.getNodeType(), query.getFunctionalDomain(), query.getOtaSupportType(),
                query.getIsCoreNode(), query.getStatus(), page, size
        );
        long total = vehicleNodeRepository.count(
                query.getNodeType(), query.getFunctionalDomain(), query.getOtaSupportType(),
                query.getIsCoreNode(), query.getStatus()
        );

        List<VehicleNodeDto> dtoList = nodes.stream()
                .map(VehicleNodeDomainAssembler::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, PageRequest.of(page - 1, size), total);
    }

    /**
     * 列出所有ACTIVE车载节点
     *
     * @return 车载节点DTO列表
     */
    public List<VehicleNodeDto> listAllActive() {
        return vehicleNodeRepository.listAllActive().stream()
                .map(VehicleNodeDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 按OTA支持类型查询车载节点
     *
     * @param query 查询条件
     * @return 车载节点DTO列表
     */
    public List<VehicleNodeDto> listByOtaType(VehicleNodeListByOtaTypeQuery query) {
        OtaSupportType otaType = OtaSupportType.valueOf(query.getOtaSupportType());
        return vehicleNodeRepository.listByOtaType(otaType).stream()
                .map(VehicleNodeDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 全量快照（供下游 Bootstrap 与对账）
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 分页结果
     */
    public Page<VehicleNodeDto> snapshot(boolean includeInactive, int page, int size) {
        List<VehicleNode> nodes = vehicleNodeRepository.snapshot(includeInactive, page, size);
        long total = vehicleNodeRepository.snapshotCount(includeInactive);

        List<VehicleNodeDto> dtoList = nodes.stream()
                .map(VehicleNodeDomainAssembler::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, PageRequest.of(page - 1, size), total);
    }

    /**
     * 查询历史版本
     *
     * @param nodeCode 业务主键
     * @return 历史版本列表
     */
    public List<VehicleNodeHistory> queryHistory(String nodeCode) {
        return vehicleNodeRepository.findHistoryByCode(nodeCode);
    }
}
