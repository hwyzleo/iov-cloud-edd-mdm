package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartGenerationUpgradeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartImportCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartMinorRevisionCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PartQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PartHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PartRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.PartNumberingDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 零件应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartAppService {

    private final PartRepository partRepository;
    private final PartNumberingDomainService partNumberingDomainService;
    private final OutboxService outboxService;
    private final SoftwareBaselineRepository softwareBaselineRepository;

    /**
     * 创建零件
     *
     * @param cmd 创建命令
     * @return 零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto createPart(PartCreateCmd cmd) {
        log.info("创建零件");

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 系统发号
        boolean isSoftware = cmd.getIsSoftware() != null && cmd.getIsSoftware();
        boolean isAssembly = cmd.getIsAssembly() != null && cmd.getIsAssembly();
        PartCode partCode = partNumberingDomainService.generatePartCode(isSoftware, isAssembly);
        log.info("系统发号生成零件号: {}", partCode.code());

        Part part = Part.create(
                partCode, cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getCategoryCode(), PartType.valueOf(cmd.getPartType()),
                cmd.getVehicleNodeCode(), cmd.getSupplierCode(),
                cmd.getIsSoftware(), cmd.getIsAssembly(), cmd.getFotaUpgradeable(), cmd.getIsSafetyCritical(),
                cmd.getIsKeyPart() != null ? KeyPartLevel.valueOf(cmd.getIsKeyPart()) : null,
                cmd.getIsRegulatoryPart(), cmd.getIsFramePart(),
                cmd.getIsAccuratelyTraced(), cmd.getFfaCode(), cmd.getFfaDesc(),
                cmd.getIsDigitate(), cmd.getInitialModel(), cmd.getProductionCode(),
                cmd.getFirstProductionDate(), cmd.getDesigner(), cmd.getDesignerDept(),
                cmd.getUom(), cmd.getDrawingNo(), cmd.getDrawingVersion(),
                cmd.getWeight(), cmd.getWeightUom(),
                LifecycleStage.valueOf(cmd.getLifecycleStage()), cmd.getSubstitutePartCode(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );

        part = partRepository.save(part, "CREATE");

        outboxService.publishPartCreatedEvent(part);

        return toDto(part);
    }

    /**
     * 更新零件
     *
     * @param cmd 更新命令
     * @return 零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto updatePart(PartUpdateCmd cmd) {
        log.info("更新零件: {}", cmd.getCode());

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Part part = partRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + cmd.getCode()));

        part.update(
                cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getCategoryCode(), PartType.valueOf(cmd.getPartType()),
                cmd.getVehicleNodeCode(), cmd.getSupplierCode(),
                cmd.getIsSoftware(), cmd.getIsAssembly(), cmd.getFotaUpgradeable(), cmd.getIsSafetyCritical(),
                cmd.getIsKeyPart() != null ? KeyPartLevel.valueOf(cmd.getIsKeyPart()) : null,
                cmd.getIsRegulatoryPart(), cmd.getIsFramePart(),
                cmd.getIsAccuratelyTraced(), cmd.getFfaCode(), cmd.getFfaDesc(),
                cmd.getIsDigitate(), cmd.getInitialModel(), cmd.getProductionCode(),
                cmd.getFirstProductionDate(), cmd.getDesigner(), cmd.getDesignerDept(),
                cmd.getUom(), cmd.getDrawingNo(), cmd.getDrawingVersion(),
                cmd.getWeight(), cmd.getWeightUom(),
                LifecycleStage.valueOf(cmd.getLifecycleStage()), cmd.getSubstitutePartCode(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy
        );

        part = partRepository.save(part, "UPDATE");

        outboxService.publishPartUpdatedEvent(part);

        return toDto(part);
    }

    /**
     * 失效零件
     *
     * @param code     零件code
     * @param modifyBy 修改人
     * @return 零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto deactivatePart(String code, String modifyBy) {
        log.info("失效零件: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Part part = partRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + code));

        part.deactivate(modifyBy);

        part = partRepository.save(part, "DEACTIVATE");

        outboxService.publishPartUpdatedEvent(part);

        return toDto(part);
    }

    /**
     * 删除零件
     *
     * @param code        零件code
     * @param operator    操作人
     * @param forceDelete 是否强制删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePart(String code, String operator, boolean forceDelete) {
        log.info("删除零件: {} forceDelete={}", code, forceDelete);

        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        Part part = partRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + code));

        if (part.getStatus() == PartStatus.DRAFT || forceDelete) {
            partRepository.delete(part, operator, forceDelete);
        } else {
            if (partRepository.existsBySubstitutePartCode(code)) {
                throw new PartHasDownstreamRefException(code, 1);
            }
            long swBaselineRefCount = softwareBaselineRepository.countByPartCode(code);
            if (swBaselineRefCount > 0) {
                throw new PartHasDownstreamRefException(code, swBaselineRefCount);
            }
            partRepository.delete(part, operator, false);
        }

        outboxService.publishPartDeletedEvent(part, forceDelete);
    }

    /**
     * 根据code获取零件
     *
     * @param code 零件code
     * @return 零件DTO
     */
    public PartDto getPartByCode(String code) {
        Part part = partRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + code));
        return toDto(part);
    }

    /**
     * 分页查询零件列表
     *
     * @param query 查询条件
     * @return 零件DTO列表
     */
    public List<PartDto> listParts(PartQuery query) {
        String status = query.isIncludeInactive() ? null : "ACTIVE";
        List<Part> parts = partRepository.list(
                query.getKeyword(),
                query.getCategoryCode(), query.getPartType(), query.getVehicleNodeCode(),
                query.getSupplierCode(), query.getLifecycleStage(), query.getIsSoftware(), query.getIsAssembly(),
                status, query.getPage(), query.getSize()
        );
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询零件总数
     *
     * @param keyword         关键词（模糊搜索 code/name/nameLocal）
     * @param categoryCode    物料分类编码
     * @param partType        零件类型
     * @param vehicleNodeCode 车辆节点编码
     * @param supplierCode    供应商编码
     * @param lifecycleStage  生命周期阶段
     * @param isSoftware      是否软件零件
     * @param isAssembly      是否总成件
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countParts(String keyword, String categoryCode, String partType, String vehicleNodeCode,
                           String supplierCode, String lifecycleStage, Boolean isSoftware, Boolean isAssembly,
                           boolean includeInactive) {
        String status = includeInactive ? null : "ACTIVE";
        return partRepository.count(keyword, categoryCode, partType, vehicleNodeCode, supplierCode,
                lifecycleStage, isSoftware, isAssembly, status);
    }

    /**
     * 查询所有ACTIVE零件
     *
     * @param isSoftware 是否软件零件
     * @param isAssembly 是否总成件
     * @return 零件DTO列表
     */
    public List<PartDto> listAllActiveParts(Boolean isSoftware, Boolean isAssembly) {
        List<Part> parts = partRepository.listAllActive(isSoftware, isAssembly);
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取零件全量快照
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 零件DTO列表
     */
    public List<PartDto> snapshot(boolean includeInactive, int page, int size) {
        List<Part> parts = partRepository.snapshot(includeInactive, page, size);
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取零件快照总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long snapshotCount(boolean includeInactive) {
        return partRepository.snapshotCount(includeInactive);
    }

    /**
     * 按物料分类查询零件列表
     *
     * @param categoryCode 物料分类编码
     * @return 零件DTO列表
     */
    public List<PartDto> listByCategory(String categoryCode) {
        List<Part> parts = partRepository.listByCategory(categoryCode);
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 按车辆节点查询零件列表
     *
     * @param vehicleNodeCode 车辆节点编码
     * @return 零件DTO列表
     */
    public List<PartDto> listByVehicleNode(String vehicleNodeCode) {
        List<Part> parts = partRepository.listByVehicleNode(vehicleNodeCode);
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 按供应商查询零件列表
     *
     * @param supplierCode 供应商编码
     * @return 零件DTO列表
     */
    public List<PartDto> listBySupplier(String supplierCode) {
        List<Part> parts = partRepository.listBySupplier(supplierCode);
        return parts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询零件历史版本列表
     *
     * @param code 零件code
     * @return 历史版本DTO列表
     */
    public List<PartHistoryDto> listPartHistory(String code) {
        List<PartHistory> historyList = partRepository.findHistoryByCode(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 代次升级（互换性变更）
     *
     * @param cmd 代次升级命令
     * @return 新零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto upgradeGeneration(PartGenerationUpgradeCmd cmd) {
        log.info("代次升级: {}", cmd.getCode());

        String operator = cmd.getOperator();
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        // 查找原记录
        Part originalPart = partRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + cmd.getCode()));

        // 计算下一代次
        PartCode newPartCode = partNumberingDomainService.nextGeneration(cmd.getCode());
        if (newPartCode == null) {
            throw new IllegalStateException("代次溢出（超过ZZ）: " + cmd.getCode());
        }

        // 创建新记录
        Part newPart = originalPart.upgradeGeneration(newPartCode, operator);
        newPart = partRepository.save(newPart, "CREATE");

        // 发布事件
        outboxService.publishPartCreatedEvent(newPart);

        log.info("代次升级完成: {} -> {}", cmd.getCode(), newPartCode.code());
        return toDto(newPart);
    }

    /**
     * 小修订（仅升图纸版本）
     *
     * @param cmd 小修订命令
     * @return 零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto minorRevision(PartMinorRevisionCmd cmd) {
        log.info("小修订: {}", cmd.getCode());

        String operator = cmd.getOperator();
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        // 查找记录
        Part part = partRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("零件不存在: " + cmd.getCode()));

        // 小修订
        part.minorRevision(cmd.getDrawingVersion(), operator);

        // 持久化
        part = partRepository.save(part, "UPDATE");

        // 发布事件
        outboxService.publishPartUpdatedEvent(part);

        log.info("小修订完成: {}", cmd.getCode());
        return toDto(part);
    }

    /**
     * 导入零件（存量迁移带号）
     *
     * @param cmd 导入命令
     * @return 零件DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PartDto importPart(PartImportCmd cmd) {
        log.info("导入零件: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 校验code唯一
        if (partRepository.existsByCode(cmd.getCode())) {
            throw new IllegalArgumentException("零件code已存在: " + cmd.getCode());
        }

        // 反解code
        PartCode partCode = partNumberingDomainService.parseCode(cmd.getCode());

        // 创建聚合根
        Part part = Part.create(partCode, cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getCategoryCode(), cmd.getPartType() != null ? PartType.valueOf(cmd.getPartType()) : null,
                null, null,
                cmd.getIsSoftware(), cmd.getIsAssembly(), cmd.getFotaUpgradeable(), cmd.getIsSafetyCritical(),
                cmd.getIsKeyPart() != null ? KeyPartLevel.valueOf(cmd.getIsKeyPart()) : null,
                cmd.getIsRegulatoryPart(), cmd.getIsFramePart(),
                cmd.getIsAccuratelyTraced(), cmd.getFfaCode(), cmd.getFfaDesc(),
                cmd.getIsDigitate(), cmd.getInitialModel(), cmd.getProductionCode(),
                cmd.getFirstProductionDate(), cmd.getDesigner(), cmd.getDesignerDept(),
                cmd.getUom(), cmd.getDrawingNo(), cmd.getDrawingVersion(),
                cmd.getWeight(), cmd.getWeightUom(),
                cmd.getLifecycleStage() != null ? LifecycleStage.valueOf(cmd.getLifecycleStage()) : null,
                cmd.getSubstitutePartCode(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);

        // 设置为IMPORT来源
        part.setNumberingSource(net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NumberingSource.IMPORT);

        // 持久化
        part = partRepository.save(part, "CREATE");

        // 发布事件
        outboxService.publishPartCreatedEvent(part);

        log.info("导入零件完成: {}", cmd.getCode());
        return toDto(part);
    }

    /**
     * 领域对象转换为DTO
     *
     * @param part 零件聚合根
     * @return 零件DTO
     */
    private PartDto toDto(Part part) {
        return PartDto.builder()
                .id(part.getId())
                .code(part.getCode())
                .baseNo(part.getBaseNo())
                .numberingSource(part.getNumberingSource() != null ? part.getNumberingSource().name() : null)
                .name(part.getName())
                .nameLocal(part.getNameLocal())
                .description(part.getDescription())
                .categoryCode(part.getCategoryCode())
                .partType(part.getPartType() != null ? part.getPartType().name() : null)
                .vehicleNodeCode(part.getVehicleNodeCode())
                .supplierCode(part.getSupplierCode())
                .isSoftware(part.getIsSoftware())
                .isAssembly(part.getIsAssembly())
                .fotaUpgradeable(part.getFotaUpgradeable())
                .isSafetyCritical(part.getIsSafetyCritical())
                .isKeyPart(part.getIsKeyPart() != null ? part.getIsKeyPart().name() : null)
                .isRegulatoryPart(part.getIsRegulatoryPart())
                .isFramePart(part.getIsFramePart())
                .isAccuratelyTraced(part.getIsAccuratelyTraced())
                .ffaCode(part.getFfaCode())
                .ffaDesc(part.getFfaDesc())
                .isDigitate(part.getIsDigitate())
                .initialModel(part.getInitialModel())
                .productionCode(part.getProductionCode())
                .firstProductionDate(part.getFirstProductionDate())
                .designer(part.getDesigner())
                .designerDept(part.getDesignerDept())
                .uom(part.getUom())
                .drawingNo(part.getDrawingNo())
                .drawingVersion(part.getDrawingVersion())
                .weight(part.getWeight())
                .weightUom(part.getWeightUom())
                .lifecycleStage(part.getLifecycleStage() != null ? part.getLifecycleStage().name() : null)
                .substitutePartCode(part.getSubstitutePartCode())
                .sourceSystem(part.getSourceSystem())
                .sourceId(part.getSourceId())
                .sourceVersion(part.getSourceVersion())
                .ingestionChannel(part.getIngestionChannel())
                .ingestionTime(part.getIngestionTime())
                .sourcePayloadHash(part.getSourcePayloadHash())
                .version(part.getVersion())
                .effectiveFrom(part.getEffectiveFrom())
                .effectiveTo(part.getEffectiveTo())
                .status(part.getStatus() != null ? part.getStatus().name() : null)
                .createBy(part.getCreateBy())
                .createTime(part.getCreateTime())
                .modifyBy(part.getModifyBy())
                .modifyTime(part.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 零件历史版本实体
     * @return 零件历史版本DTO
     */
    private PartHistoryDto convertHistoryToDto(PartHistory history) {
        return PartHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .description(history.getDescription())
                .categoryCode(history.getCategoryCode())
                .partType(history.getPartType() != null ? history.getPartType().name() : null)
                .vehicleNodeCode(history.getVehicleNodeCode())
                .supplierCode(history.getSupplierCode())
                .isSoftware(history.getIsSoftware())
                .fotaUpgradeable(history.getFotaUpgradeable())
                .isSafetyCritical(history.getIsSafetyCritical())
                .isKeyPart(history.getIsKeyPart() != null ? history.getIsKeyPart().name() : null)
                .isRegulatoryPart(history.getIsRegulatoryPart())
                .isFramePart(history.getIsFramePart())
                .isAccuratelyTraced(history.getIsAccuratelyTraced())
                .ffaCode(history.getFfaCode())
                .ffaDesc(history.getFfaDesc())
                .isDigitate(history.getIsDigitate())
                .initialModel(history.getInitialModel())
                .productionCode(history.getProductionCode())
                .firstProductionDate(history.getFirstProductionDate())
                .designer(history.getDesigner())
                .designerDept(history.getDesignerDept())
                .uom(history.getUom())
                .drawingNo(history.getDrawingNo())
                .drawingVersion(history.getDrawingVersion())
                .weight(history.getWeight())
                .weightUom(history.getWeightUom())
                .lifecycleStage(history.getLifecycleStage() != null ? history.getLifecycleStage().name() : null)
                .substitutePartCode(history.getSubstitutePartCode())
                .sourceSystem(history.getSourceSystem())
                .sourceId(history.getSourceId())
                .sourceVersion(history.getSourceVersion())
                .ingestionChannel(history.getIngestionChannel())
                .ingestionTime(history.getIngestionTime())
                .sourcePayloadHash(history.getSourcePayloadHash())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus() != null ? history.getStatus().name() : null)
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .createBy(history.getCreateBy())
                .createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy())
                .modifyTime(history.getModifyTime())
                .build();
    }
}
