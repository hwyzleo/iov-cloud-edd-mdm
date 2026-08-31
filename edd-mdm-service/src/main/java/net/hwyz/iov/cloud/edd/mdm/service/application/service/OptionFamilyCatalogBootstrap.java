package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogTier;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.OptionFamilyCatalogLoader;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 选项族标准目录 Bootstrap（CR-035 §5.3）
 * <p>
 * 受配置开关控制的一次性幂等导入：只读取 tier=CORE 的条目；对每条 Core 记录先按 code 查询，
 * 不存在则通过 OptionFamilyAppService.createOptionFamily 创建为 ACTIVE（确保主表、history、
 * outbox 与审计链路一致）；完全一致则跳过；已存在但名称/category 不一致则记录冲突并跳过，不覆盖业务数据。
 * 单条失败不回滚已成功项；重复执行保持幂等。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptionFamilyCatalogBootstrap {

    /**
     * 目录初始化执行人（系统身份，来源为 LOCAL）
     */
    public static final String BOOTSTRAP_OPERATOR = "system";

    private final OptionFamilyCatalogLoader catalogLoader;
    private final OptionFamilyAppService optionFamilyAppService;

    /**
     * 执行标准目录导入（幂等）
     *
     * @return 导入统计结果
     */
    public OptionFamilyCatalogBootstrapResult bootstrap() {
        List<OptionFamilyCatalogEntry> entries = catalogLoader.load();
        OptionFamilyCatalogBootstrapResult result = OptionFamilyCatalogBootstrapResult.builder().build();
        long coreCount = entries.stream().filter(e -> e.getTier() == OptionFamilyCatalogTier.CORE).count();
        log.info("Option Family 标准目录 Bootstrap 开始: 目录条目={}, CORE={}", entries.size(), coreCount);
        for (OptionFamilyCatalogEntry entry : entries) {
            // 只导入 CORE；Conditional/Extension 不自动创建
            if (entry.getTier() != OptionFamilyCatalogTier.CORE) {
                continue;
            }
            try {
                if (!optionFamilyAppService.existsOptionFamily(entry.getCode())) {
                    optionFamilyAppService.createOptionFamily(toCreateCmd(entry));
                    result.incrementCreated();
                    log.info("Option Family 标准目录 Bootstrap 创建选项族: {}", entry.getCode());
                } else {
                    OptionFamilyDto existing = optionFamilyAppService.getOptionFamilyByCode(entry.getCode());
                    if (matches(existing, entry)) {
                        result.incrementSkipped();
                        log.debug("Option Family 标准目录 Bootstrap 跳过（完全一致）: {}", entry.getCode());
                    } else {
                        result.incrementConflicted();
                        result.addDetail(entry.getCode()
                                + ": 已存在但名称/category 不一致，跳过（不覆盖业务数据）");
                        log.warn("Option Family 标准目录 Bootstrap 冲突跳过: {}, 现存 name={}/nameLocal={}/category={}",
                                entry.getCode(), existing.getName(), existing.getNameLocal(), existing.getCategory());
                    }
                }
            } catch (Exception e) {
                result.incrementFailed();
                result.addDetail(entry.getCode() + ": " + e.getMessage());
                log.error("Option Family 标准目录 Bootstrap 创建选项族失败: {}", entry.getCode(), e);
            }
        }
        log.info("Option Family 标准目录 Bootstrap 完成: created={}, skipped={}, conflicted={}, failed={}",
                result.getCreated(), result.getSkipped(), result.getConflicted(), result.getFailed());
        return result;
    }

    private OptionFamilyCreateCmd toCreateCmd(OptionFamilyCatalogEntry entry) {
        return OptionFamilyCreateCmd.builder()
                .code(entry.getCode())
                .name(entry.getName())
                .nameLocal(entry.getNameLocal())
                .description(entry.getDescription())
                .category(entry.getCategory() != null ? entry.getCategory().name() : null)
                .createBy(BOOTSTRAP_OPERATOR)
                .build();
    }

    private boolean matches(OptionFamilyDto existing, OptionFamilyCatalogEntry entry) {
        return Objects.equals(existing.getName(), entry.getName())
                && Objects.equals(existing.getNameLocal(), entry.getNameLocal())
                && Objects.equals(existing.getCategory(),
                        entry.getCategory() != null ? entry.getCategory().name() : null)
                && Objects.equals(existing.getDescription(), entry.getDescription());
    }
}
