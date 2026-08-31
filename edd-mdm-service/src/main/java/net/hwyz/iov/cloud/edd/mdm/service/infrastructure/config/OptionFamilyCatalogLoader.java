package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogTier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyNamePolicy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 选项族标准目录加载器（CR-035 §5.1）
 * <p>
 * 读取版本化标准目录资源（masterdata/option-family-catalog-v1.yaml），
 * 并对目录自身一致性做静态校验：code 标准格式、前缀/category 一致、
 * code/双语名称唯一、CONDITIONAL 必含启用条件、CORE 不得带启用条件。
 * 目录内容以 MDM-REQ-CR-035 §4 为准；Extension 示例不写入目录资源。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OptionFamilyCatalogLoader {

    /**
     * 版本化标准目录资源路径
     */
    public static final String CATALOG_RESOURCE = "masterdata/option-family-catalog-v1.yaml";

    private final OptionFamilyCodePolicy codePolicy;
    private final OptionFamilyNamePolicy namePolicy;

    /**
     * 从 classpath 加载并校验标准目录
     *
     * @return 目录条目列表
     * @throws IllegalStateException 目录资源缺失或目录自身一致性校验失败
     */
    public List<OptionFamilyCatalogEntry> load() {
        try (InputStream is = new ClassPathResource(CATALOG_RESOURCE).getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            return parseAndValidate(root);
        } catch (IOException e) {
            throw new IllegalStateException("读取选项族标准目录资源失败: " + CATALOG_RESOURCE, e);
        }
    }

    /**
     * 解析并校验 YAML 内容（供测试直接注入 YAML 字符串）
     *
     * @param yamlContent YAML 内容
     * @return 目录条目列表
     */
    public List<OptionFamilyCatalogEntry> parse(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);
        return parseAndValidate(root);
    }

    @SuppressWarnings("unchecked")
    private List<OptionFamilyCatalogEntry> parseAndValidate(Map<String, Object> root) {
        if (root == null) {
            throw new IllegalStateException("选项族标准目录为空");
        }
        Object listObj = root.get("optionFamilies");
        if (!(listObj instanceof List)) {
            throw new IllegalStateException("选项族标准目录缺少 optionFamilies 列表");
        }
        List<OptionFamilyCatalogEntry> entries = new ArrayList<>();
        for (Object item : (List<?>) listObj) {
            if (!(item instanceof Map)) {
                continue;
            }
            Map<String, Object> m = (Map<String, Object>) item;
            OptionFamilyCatalogTier tier = parseTier((String) m.get("tier"));
            String code = (String) m.get("code");
            String name = (String) m.get("name");
            String nameLocal = (String) m.get("nameLocal");
            OptionFamilyCategory category = parseCategory((String) m.get("category"));
            String description = (String) m.get("description");
            String activationCondition = (String) m.get("activationCondition");
            entries.add(OptionFamilyCatalogEntry.builder()
                    .tier(tier)
                    .code(code)
                    .name(name)
                    .nameLocal(nameLocal)
                    .category(category)
                    .description(description)
                    .activationCondition(activationCondition)
                    .build());
        }
        validateSelfConsistency(entries);
        return entries;
    }

    /**
     * 目录自身一致性静态校验
     */
    private void validateSelfConsistency(List<OptionFamilyCatalogEntry> entries) {
        Set<String> codes = new HashSet<>();
        Set<String> names = new HashSet<>();
        Set<String> nameLocals = new HashSet<>();
        for (OptionFamilyCatalogEntry entry : entries) {
            if (entry.getTier() == null) {
                throw new IllegalStateException("目录条目缺少 tier: " + entry.getCode());
            }
            // 目录只允许标准格式 code（Extension 不进入目录资源）
            if (!codePolicy.isStandardFormat(entry.getCode())) {
                throw new IllegalStateException("目录条目 code 非标准格式: " + entry.getCode());
            }
            codePolicy.validateCategoryConsistency(entry.getCode(), entry.getCategory());
            if (!codes.add(entry.getCode())) {
                throw new IllegalStateException("目录条目 code 重复: " + entry.getCode());
            }
            String normalizedName = namePolicy.normalize(entry.getName());
            String normalizedNameLocal = namePolicy.normalize(entry.getNameLocal());
            if (normalizedName != null && !names.add(normalizedName)) {
                throw new IllegalStateException("目录条目英文名重复: " + entry.getCode());
            }
            if (normalizedNameLocal != null && !nameLocals.add(normalizedNameLocal)) {
                throw new IllegalStateException("目录条目中文名重复: " + entry.getCode());
            }
            if (entry.getTier() == OptionFamilyCatalogTier.CONDITIONAL
                    && (entry.getActivationCondition() == null || entry.getActivationCondition().isBlank())) {
                throw new IllegalStateException("CONDITIONAL 条目缺少 activationCondition: " + entry.getCode());
            }
            if (entry.getTier() == OptionFamilyCatalogTier.CORE
                    && entry.getActivationCondition() != null && !entry.getActivationCondition().isBlank()) {
                throw new IllegalStateException("CORE 条目不应包含 activationCondition: " + entry.getCode());
            }
        }
    }

    private OptionFamilyCatalogTier parseTier(String tier) {
        if (tier == null || tier.isBlank()) {
            throw new IllegalStateException("目录条目 tier 为空");
        }
        try {
            return OptionFamilyCatalogTier.valueOf(tier.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("目录条目 tier 非法: " + tier);
        }
    }

    private OptionFamilyCategory parseCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalStateException("目录条目 category 为空");
        }
        try {
            return OptionFamilyCategory.valueOf(category.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("目录条目 category 非法: " + category);
        }
    }
}
