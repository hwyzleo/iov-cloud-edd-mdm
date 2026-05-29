package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.MaterialCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.MaterialCategoryHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料分类应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialCategoryAppService {

    private final MaterialCategoryRepository materialCategoryRepository;
    private final OutboxService outboxService;

    /**
     * 创建物料分类
     *
     * @param cmd 创建命令
     * @return 物料分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MaterialCategoryDto createMaterialCategory(MaterialCategoryCreateCmd cmd) {
        log.info("创建物料分类: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        MaterialCategory category = MaterialCategory.create(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getParentCode(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );

        category = materialCategoryRepository.save(category, "CREATE");

        outboxService.publishMaterialCategoryCreatedEvent(category);

        return toDto(category);
    }

    /**
     * 更新物料分类
     *
     * @param cmd 更新命令
     * @return 物料分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MaterialCategoryDto updateMaterialCategory(MaterialCategoryUpdateCmd cmd) {
        log.info("更新物料分类: {}", cmd.getCode());

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        MaterialCategory category = materialCategoryRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("物料分类不存在: " + cmd.getCode()));

        category.update(
                cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getParentCode(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy
        );

        category = materialCategoryRepository.save(category, "UPDATE");

        outboxService.publishMaterialCategoryUpdatedEvent(category);

        return toDto(category);
    }

    /**
     * 失效物料分类
     *
     * @param code     物料分类code
     * @param modifyBy 修改人
     * @return 物料分类DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public MaterialCategoryDto deactivateMaterialCategory(String code, String modifyBy) {
        log.info("失效物料分类: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        MaterialCategory category = materialCategoryRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("物料分类不存在: " + code));

        category.deactivate(modifyBy);

        category = materialCategoryRepository.save(category, "DEACTIVATE");

        outboxService.publishMaterialCategoryUpdatedEvent(category);

        return toDto(category);
    }

    /**
     * 删除物料分类
     *
     * @param code     物料分类code
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterialCategory(String code, String operator) {
        log.info("删除物料分类: {}", code);

        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        MaterialCategory category = materialCategoryRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("物料分类不存在: " + code));

        if (materialCategoryRepository.hasChildren(code)) {
            throw new IllegalStateException("物料分类存在子分类，不允许删除: " + code);
        }

        if (materialCategoryRepository.hasParts(code)) {
            throw new IllegalStateException("物料分类下存在零件，不允许删除: " + code);
        }

        materialCategoryRepository.delete(category, operator);

        outboxService.publishMaterialCategoryDeletedEvent(category, false);
    }

    /**
     * 根据code获取物料分类
     *
     * @param code 物料分类code
     * @return 物料分类DTO
     */
    public MaterialCategoryDto getMaterialCategoryByCode(String code) {
        MaterialCategory category = materialCategoryRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("物料分类不存在: " + code));
        return toDto(category);
    }

    /**
     * 分页查询物料分类列表
     *
     * @param query 查询条件
     * @return 物料分类DTO列表
     */
    public List<MaterialCategoryDto> listMaterialCategories(MaterialCategoryQuery query) {
        String status = query.isIncludeInactive() ? null : "ACTIVE";
        List<MaterialCategory> categories = materialCategoryRepository.list(
                query.getParentCode(), status, query.getPage(), query.getSize()
        );
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询物料分类总数
     *
     * @param parentCode      父分类编码
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countMaterialCategories(String parentCode, boolean includeInactive) {
        String status = includeInactive ? null : "ACTIVE";
        return materialCategoryRepository.count(parentCode, status);
    }

    /**
     * 查询所有ACTIVE物料分类
     *
     * @return 物料分类DTO列表
     */
    public List<MaterialCategoryDto> listAllActiveMaterialCategories() {
        List<MaterialCategory> categories = materialCategoryRepository.listAllActive();
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取物料分类树形结构
     *
     * @return 物料分类DTO列表
     */
    public List<MaterialCategoryDto> tree() {
        List<MaterialCategory> categories = materialCategoryRepository.tree();
        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询物料分类历史版本列表
     *
     * @param code 物料分类code
     * @return 历史版本DTO列表
     */
    public List<MaterialCategoryHistoryDto> listMaterialCategoryHistory(String code) {
        List<MaterialCategoryHistory> historyList = materialCategoryRepository.findHistoryByCode(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 领域对象转换为DTO
     *
     * @param category 物料分类聚合根
     * @return 物料分类DTO
     */
    private MaterialCategoryDto toDto(MaterialCategory category) {
        return MaterialCategoryDto.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .nameLocal(category.getNameLocal())
                .description(category.getDescription())
                .parentCode(category.getParentCode())
                .sourceSystem(category.getSourceSystem())
                .sourceId(category.getSourceId())
                .sourceVersion(category.getSourceVersion())
                .ingestionChannel(category.getIngestionChannel())
                .ingestionTime(category.getIngestionTime())
                .sourcePayloadHash(category.getSourcePayloadHash())
                .version(category.getVersion())
                .effectiveFrom(category.getEffectiveFrom())
                .effectiveTo(category.getEffectiveTo())
                .status(category.getStatus() != null ? category.getStatus().name() : null)
                .createBy(category.getCreateBy())
                .createTime(category.getCreateTime())
                .modifyBy(category.getModifyBy())
                .modifyTime(category.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 物料分类历史版本实体
     * @return 物料分类历史版本DTO
     */
    private MaterialCategoryHistoryDto convertHistoryToDto(MaterialCategoryHistory history) {
        return MaterialCategoryHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .description(history.getDescription())
                .parentCode(history.getParentCode())
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
