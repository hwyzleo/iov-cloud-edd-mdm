package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.DeviceCategoryCatalogLoader;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 设备类别标准目录 Bootstrap（CR-037 §6）
 * <p>
 * 受控幂等初始化：处理目录中的全部 24 个扁平设备族（不存在 Core / Conditional / Extension 分支）；
 * 对每条记录先按 code 查询，不存在则通过 DeviceCategoryAppService.createDeviceCategory 创建为
 * ACTIVE（source=MANUAL，确保主表、history、outbox 与审计链路一致）；完全一致则跳过；
 * 已存在但名称/语义不一致则记录冲突并跳过，不覆盖业务数据。
 * 逐条独立事务；单条失败不回滚已成功条目；重复执行保持幂等。
 * 目录非法（加载/静态校验失败）时抛出异常，禁止初始化。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceCategoryCatalogBootstrap {

    /**
     * 目录初始化执行人（系统身份）
     */
    public static final String BOOTSTRAP_OPERATOR = "system";

    private final DeviceCategoryCatalogLoader catalogLoader;
    private final DeviceCategoryAppService deviceCategoryAppService;

    /**
     * 执行标准目录初始化（幂等）
     *
     * @return 导入统计结果（含目录版本、操作者、起止时间与冲突/失败明细）
     */
    public DeviceCategoryCatalogBootstrapResult bootstrap() {
        List<DeviceCategoryCatalogEntry> entries = catalogLoader.load();
        int version = catalogLoader.loadVersion();
        Date startedAt = new Date();
        DeviceCategoryCatalogBootstrapResult result = DeviceCategoryCatalogBootstrapResult.builder()
                .catalogVersion(version)
                .operator(BOOTSTRAP_OPERATOR)
                .startedAt(startedAt)
                .build();
        log.info("Device Category 标准目录 Bootstrap 开始: 版本={}, 设备族数={}", version, entries.size());
        for (DeviceCategoryCatalogEntry entry : entries) {
            try {
                if (!deviceCategoryAppService.existsDeviceCategory(entry.getCode())) {
                    deviceCategoryAppService.createDeviceCategory(toCreateCmd(entry));
                    result.incrementCreated();
                    log.info("Device Category 标准目录 Bootstrap 创建设备类别: {}", entry.getCode());
                } else {
                    DeviceCategoryDto existing = deviceCategoryAppService.getDeviceCategoryByCode(entry.getCode());
                    if (matches(existing, entry)) {
                        result.incrementSkipped();
                        log.debug("Device Category 标准目录 Bootstrap 跳过（完全一致）: {}", entry.getCode());
                    } else {
                        result.incrementConflicted();
                        result.addDetail(entry.getCode()
                                + ": 已存在但名称/语义不一致，跳过（不覆盖业务数据）");
                        log.warn("Device Category 标准目录 Bootstrap 冲突跳过: {}, 现存 name={}/nameLocal={}",
                                entry.getCode(), existing.getName(), existing.getNameLocal());
                    }
                }
            } catch (Exception e) {
                result.incrementFailed();
                result.addDetail(entry.getCode() + ": " + e.getMessage());
                log.error("Device Category 标准目录 Bootstrap 创建设备类别失败: {}", entry.getCode(), e);
            }
        }
        result.setFinishedAt(new Date());
        log.info("Device Category 标准目录 Bootstrap 完成: created={}, skipped={}, conflicted={}, failed={}",
                result.getCreated(), result.getSkipped(), result.getConflicted(), result.getFailed());
        return result;
    }

    /**
     * 执行标准目录预检（初始化前预览）
     *
     * @return 目录版本、标准设备族数量、已初始化/待创建/冲突统计及冲突明细
     */
    public DeviceCategoryCatalogPreviewResult preview() {
        List<DeviceCategoryCatalogEntry> entries = catalogLoader.load();
        int version = catalogLoader.loadVersion();
        DeviceCategoryCatalogPreviewResult result = DeviceCategoryCatalogPreviewResult.builder()
                .catalogVersion(version)
                .standardFamilyCount(entries.size())
                .build();
        for (DeviceCategoryCatalogEntry entry : entries) {
            if (!deviceCategoryAppService.existsDeviceCategory(entry.getCode())) {
                result.setMissing(result.getMissing() + 1);
            } else {
                DeviceCategoryDto existing = deviceCategoryAppService.getDeviceCategoryByCode(entry.getCode());
                if (matches(existing, entry)) {
                    result.setInitialized(result.getInitialized() + 1);
                } else {
                    result.setConflicted(result.getConflicted() + 1);
                    result.addConflict(entry.getCode()
                            + ": 已存在但名称/语义不一致（不会覆盖）");
                }
            }
        }
        return result;
    }

    private DeviceCategoryCreateCmd toCreateCmd(DeviceCategoryCatalogEntry entry) {
        return DeviceCategoryCreateCmd.builder()
                .code(entry.getCode())
                .name(entry.getName())
                .nameLocal(entry.getNameLocal())
                .description(entry.getDescription())
                .sortOrder(entry.getSortOrder())
                .createBy(BOOTSTRAP_OPERATOR)
                .build();
    }

    private boolean matches(DeviceCategoryDto existing, DeviceCategoryCatalogEntry entry) {
        return Objects.equals(existing.getName(), entry.getName())
                && Objects.equals(existing.getNameLocal(), entry.getNameLocal())
                && Objects.equals(existing.getDescription(), entry.getDescription())
                && Objects.equals(existing.getSortOrder(), entry.getSortOrder());
    }
}
