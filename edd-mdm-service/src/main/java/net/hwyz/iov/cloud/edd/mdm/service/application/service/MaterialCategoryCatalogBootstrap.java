package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryCatalogStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MaterialCategoryCatalogLoader;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 物料品类标准目录 Bootstrap（CR-039 §6）
 * <p>
 * 受控幂等初始化：按 L1 → L2 → L3 拓扑顺序处理 101 项；对每条记录先按 code 查询，
 * 不存在则通过 MaterialCategoryAppService.createMaterialCategory 创建为 ACTIVE
 * （source=LOCAL，确保主表、history、outbox 与审计链路一致，且不绕过 AppService 与 Domain Policy）；
 * 完全一致则跳过；已存在但名称/父级/语义不一致则记录冲突并跳过，不覆盖业务数据。
 * 逐条独立事务；单条失败不回滚已成功条目；父节点未就绪时子树标记 dependency failed，其他分支继续。
 * 重复执行保持幂等。目录非法（加载/静态校验失败）时标记 Catalog INVALID，禁用 preview/bootstrap。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialCategoryCatalogBootstrap {

    /**
     * 目录初始化执行人（系统身份）
     */
    public static final String BOOTSTRAP_OPERATOR = "system";

    private final MaterialCategoryCatalogLoader catalogLoader;
    private final MaterialCategoryAppService materialCategoryAppService;

    /**
     * 执行标准目录初始化（幂等，按 L1 → L2 → L3 拓扑顺序）
     *
     * @return 导入统计结果（含目录版本、操作者、起止时间与冲突/失败/依赖失败明细）
     */
    public MaterialCategoryCatalogBootstrapResult bootstrap() {
        List<MaterialCategoryCatalogEntry> entries;
        int version;
        try {
            entries = catalogLoader.load();
            version = catalogLoader.loadVersion();
        } catch (IllegalStateException e) {
            log.error("Material Category 标准目录非法，禁用 Bootstrap: {}", e.getMessage());
            return MaterialCategoryCatalogBootstrapResult.builder()
                    .catalogStatus(MaterialCategoryCatalogStatus.INVALID.name())
                    .error(e.getMessage())
                    .build();
        }
        Date startedAt = new Date();
        MaterialCategoryCatalogBootstrapResult result = MaterialCategoryCatalogBootstrapResult.builder()
                .catalogStatus(MaterialCategoryCatalogStatus.VALID.name())
                .catalogVersion(version)
                .operator(BOOTSTRAP_OPERATOR)
                .startedAt(startedAt)
                .build();
        log.info("Material Category 标准目录 Bootstrap 开始: 版本={}, 条目数={}", version, entries.size());

        Set<String> readyCodes = new HashSet<>();
        for (MaterialCategoryCatalogEntry entry : topologicalOrder(entries)) {
            // 父节点未就绪（不存在且未成功创建/跳过一致）：子树标记 dependency failed
            if (entry.getLevel() > 1 && !readyCodes.contains(entry.getParentCode())) {
                result.incrementDependencyFailed();
                result.addDetail(entry.getCode() + ": 父节点 " + entry.getParentCode() + " 未就绪（依赖失败），子树跳过");
                log.warn("Material Category 标准目录 Bootstrap 依赖失败跳过: {}, 父节点={}",
                        entry.getCode(), entry.getParentCode());
                continue;
            }
            try {
                if (!materialCategoryAppService.existsMaterialCategory(entry.getCode())) {
                    materialCategoryAppService.createMaterialCategory(toCreateCmd(entry));
                    result.incrementCreated();
                    readyCodes.add(entry.getCode());
                    log.info("Material Category 标准目录 Bootstrap 创建: {}", entry.getCode());
                } else {
                    MaterialCategoryDto existing = materialCategoryAppService.getMaterialCategoryByCode(entry.getCode());
                    if (matches(existing, entry)) {
                        result.incrementSkipped();
                        readyCodes.add(entry.getCode());
                        log.debug("Material Category 标准目录 Bootstrap 跳过（完全一致）: {}", entry.getCode());
                    } else {
                        result.incrementConflicted();
                        result.addDetail(entry.getCode() + ": 已存在但名称/父级/语义不一致，跳过（不覆盖业务数据）");
                        log.warn("Material Category 标准目录 Bootstrap 冲突跳过: {}, 现存 name={}/nameLocal={}/parentCode={}",
                                entry.getCode(), existing.getName(), existing.getNameLocal(), existing.getParentCode());
                    }
                }
            } catch (Exception e) {
                result.incrementFailed();
                result.addDetail(entry.getCode() + ": " + e.getMessage());
                log.error("Material Category 标准目录 Bootstrap 创建失败: {}", entry.getCode(), e);
            }
        }
        result.setFinishedAt(new Date());
        log.info("Material Category 标准目录 Bootstrap 完成: created={}, skipped={}, conflicted={}, "
                        + "failed={}, dependencyFailed={}",
                result.getCreated(), result.getSkipped(), result.getConflicted(),
                result.getFailed(), result.getDependencyFailed());
        return result;
    }

    /**
     * 执行标准目录预检（初始化前预览）
     *
     * @return 目录版本、total=101、level1/2/3Count、catalogStatus 及每项 Initialized/Missing/Conflict 明细
     */
    public MaterialCategoryCatalogPreviewResult preview() {
        List<MaterialCategoryCatalogEntry> entries;
        int version;
        try {
            entries = catalogLoader.load();
            version = catalogLoader.loadVersion();
        } catch (IllegalStateException e) {
            log.error("Material Category 标准目录非法，禁用 Preview: {}", e.getMessage());
            return MaterialCategoryCatalogPreviewResult.builder()
                    .catalogStatus(MaterialCategoryCatalogStatus.INVALID.name())
                    .error(e.getMessage())
                    .build();
        }
        long level1 = entries.stream().filter(e -> e.getLevel() == 1).count();
        long level2 = entries.stream().filter(e -> e.getLevel() == 2).count();
        long level3 = entries.stream().filter(e -> e.getLevel() == 3).count();
        MaterialCategoryCatalogPreviewResult result = MaterialCategoryCatalogPreviewResult.builder()
                .catalogStatus(MaterialCategoryCatalogStatus.VALID.name())
                .catalogVersion(version)
                .total(entries.size())
                .level1Count((int) level1)
                .level2Count((int) level2)
                .level3Count((int) level3)
                .build();
        for (MaterialCategoryCatalogEntry entry : topologicalOrder(entries)) {
            if (!materialCategoryAppService.existsMaterialCategory(entry.getCode())) {
                result.setMissing(result.getMissing() + 1);
                result.addItem(MaterialCategoryCatalogPreviewResult.ItemStatus.builder()
                        .code(entry.getCode()).level(entry.getLevel())
                        .status(MaterialCategoryCatalogPreviewResult.ItemStatus.STATUS_MISSING).build());
            } else {
                MaterialCategoryDto existing = materialCategoryAppService.getMaterialCategoryByCode(entry.getCode());
                if (matches(existing, entry)) {
                    result.setInitialized(result.getInitialized() + 1);
                    result.addItem(MaterialCategoryCatalogPreviewResult.ItemStatus.builder()
                            .code(entry.getCode()).level(entry.getLevel())
                            .status(MaterialCategoryCatalogPreviewResult.ItemStatus.STATUS_INITIALIZED).build());
                } else {
                    result.setConflicted(result.getConflicted() + 1);
                    result.addConflict(entry.getCode() + ": 已存在但名称/父级/语义不一致（不会覆盖）");
                    result.addItem(MaterialCategoryCatalogPreviewResult.ItemStatus.builder()
                            .code(entry.getCode()).level(entry.getLevel())
                            .status(MaterialCategoryCatalogPreviewResult.ItemStatus.STATUS_CONFLICT).build());
                }
            }
        }
        return result;
    }

    private List<MaterialCategoryCatalogEntry> topologicalOrder(List<MaterialCategoryCatalogEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparing(MaterialCategoryCatalogEntry::getLevel)
                        .thenComparing(e -> e.getSortOrder() == null ? 0 : e.getSortOrder())
                        .thenComparing(MaterialCategoryCatalogEntry::getCode))
                .toList();
    }

    private MaterialCategoryCreateCmd toCreateCmd(MaterialCategoryCatalogEntry entry) {
        return MaterialCategoryCreateCmd.builder()
                .code(entry.getCode())
                .name(entry.getName())
                .nameLocal(entry.getNameLocal())
                .description(entry.getDescription())
                .parentCode(entry.getParentCode())
                .createBy(BOOTSTRAP_OPERATOR)
                .build();
    }

    private boolean matches(MaterialCategoryDto existing, MaterialCategoryCatalogEntry entry) {
        return Objects.equals(existing.getName(), entry.getName())
                && Objects.equals(existing.getNameLocal(), entry.getNameLocal())
                && Objects.equals(existing.getDescription(), entry.getDescription())
                && Objects.equals(existing.getParentCode(), entry.getParentCode());
    }
}
