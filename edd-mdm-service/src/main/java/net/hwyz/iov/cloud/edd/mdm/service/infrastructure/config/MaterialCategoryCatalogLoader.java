package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryAbbreviationRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryNamePolicy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 物料品类标准目录加载器（CR-039 §3）
 * <p>
 * 读取版本化标准目录资源（masterdata/material-category-catalog-v1.yaml），
 * 并对目录自身一致性做静态校验：
 * <ol>
 *   <li>总数固定 101，分层数量固定为 L1=4、L2=19、L3=78</li>
 *   <li>code、name、nameLocal 唯一且字段完整</li>
 *   <li>code 满足 ^MC_[A-Z0-9]+(?:_[A-Z0-9]+)*$，仅使用受控缩写，且总长度不超过 32 字符</li>
 *   <li>L1 无父；L2 只能指向 L1；L3 只能指向 L2</li>
 *   <li>子节点 code 必须以 parentCode + "_" 开头</li>
 *   <li>标准目录无 _X_、第四层、环路、孤儿或跨 scope 挂接</li>
 *   <li>目录声明的 level 与按 parent 链计算的深度一致</li>
 * </ol>
 * 目录自身非法时抛出 IllegalStateException（标记 Catalog INVALID），禁用 preview/bootstrap 并告警，
 * 但不阻断服务 readiness。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialCategoryCatalogLoader {

    /**
     * 版本化标准目录资源路径
     */
    public static final String CATALOG_RESOURCE = "masterdata/material-category-catalog-v1.yaml";

    /**
     * 标准目录固定规模（CR-039 §1/§3）
     */
    public static final int TOTAL_COUNT = 101;
    public static final int LEVEL1_COUNT = 4;
    public static final int LEVEL2_COUNT = 19;
    public static final int LEVEL3_COUNT = 78;

    private final MaterialCategoryCodePolicy codePolicy;
    private final MaterialCategoryNamePolicy namePolicy;
    private final MaterialCategoryAbbreviationRegistry abbreviationRegistry;

    /**
     * 从 classpath 加载并校验标准目录
     *
     * @return 目录条目列表（101 项：4 L1 + 19 L2 + 78 L3）
     * @throws IllegalStateException 目录资源缺失或目录自身一致性校验失败
     */
    public List<MaterialCategoryCatalogEntry> load() {
        try (InputStream is = new ClassPathResource(CATALOG_RESOURCE).getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            return parseAndValidate(root);
        } catch (IOException e) {
            throw new IllegalStateException("读取物料品类标准目录资源失败: " + CATALOG_RESOURCE, e);
        }
    }

    /**
     * 从 classpath 加载标准目录版本
     *
     * @return 目录版本号
     */
    public int loadVersion() {
        try (InputStream is = new ClassPathResource(CATALOG_RESOURCE).getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            return resolveVersion(root);
        } catch (IOException e) {
            throw new IllegalStateException("读取物料品类标准目录资源失败: " + CATALOG_RESOURCE, e);
        }
    }

    /**
     * 解析 YAML 中的目录版本号（供测试直接注入 YAML 字符串）
     */
    public int parseVersion(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);
        return resolveVersion(root);
    }

    /**
     * 解析并校验 YAML 内容（供测试直接注入 YAML 字符串）
     *
     * @return 目录条目列表
     */
    public List<MaterialCategoryCatalogEntry> parse(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);
        return parseAndValidate(root);
    }

    private int resolveVersion(Map<String, Object> root) {
        Object version = root == null ? null : root.get("version");
        if (!(version instanceof Number)) {
            throw new IllegalStateException("物料品类标准目录缺少 version");
        }
        return ((Number) version).intValue();
    }

    @SuppressWarnings("unchecked")
    private List<MaterialCategoryCatalogEntry> parseAndValidate(Map<String, Object> root) {
        if (root == null) {
            throw new IllegalStateException("物料品类标准目录为空");
        }
        Object listObj = root.get("materialCategories");
        if (!(listObj instanceof List)) {
            throw new IllegalStateException("物料品类标准目录缺少 materialCategories 列表");
        }
        List<MaterialCategoryCatalogEntry> entries = new ArrayList<>();
        for (Object item : (List<?>) listObj) {
            if (!(item instanceof Map)) {
                continue;
            }
            Map<String, Object> m = (Map<String, Object>) item;
            String code = (String) m.get("code");
            Integer level = m.get("level") instanceof Number ? ((Number) m.get("level")).intValue() : null;
            String name = (String) m.get("name");
            String nameLocal = (String) m.get("nameLocal");
            String parentCode = (String) m.get("parentCode");
            String description = (String) m.get("description");
            List<String> aliases = m.get("aliases") instanceof List
                    ? new ArrayList<>((List<String>) m.get("aliases"))
                    : new ArrayList<>();
            Integer sortOrder = m.get("sortOrder") instanceof Number
                    ? ((Number) m.get("sortOrder")).intValue()
                    : 0;
            entries.add(MaterialCategoryCatalogEntry.builder()
                    .code(code).level(level).name(name).nameLocal(nameLocal)
                    .parentCode(parentCode).description(description)
                    .aliases(aliases).sortOrder(sortOrder)
                    .build());
        }
        validateSelfConsistency(entries);
        return entries;
    }

    /**
     * 目录自身一致性静态校验（CR-039 §3）
     */
    private void validateSelfConsistency(List<MaterialCategoryCatalogEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            throw new IllegalStateException("物料品类标准目录为空");
        }
        Map<String, MaterialCategoryCatalogEntry> byCode = new HashMap<>();
        Set<String> codes = new HashSet<>();
        Set<String> names = new HashSet<>();
        Set<String> nameLocals = new HashSet<>();
        for (MaterialCategoryCatalogEntry entry : entries) {
            if (entry.getCode() == null || entry.getCode().isBlank()
                    || entry.getName() == null || entry.getName().isBlank()
                    || entry.getNameLocal() == null || entry.getNameLocal().isBlank()) {
                throw new IllegalStateException("目录条目必须同时具备 code/name/nameLocal: " + entry.getCode());
            }
            if (entry.getLevel() == null || entry.getLevel() < 1 || entry.getLevel() > 3) {
                throw new IllegalStateException("目录条目 level 必须为 1/2/3: " + entry.getCode());
            }
            if (!codePolicy.isStandardFormat(entry.getCode())) {
                throw new IllegalStateException(
                        "目录条目 code 不符合层级语义格式或总长度超过 32: " + entry.getCode());
            }
            // 仅使用受控缩写：Scope 必须在受控字典内
            String scope = codePolicy.resolveScopeAbbreviation(entry.getCode());
            if (!abbreviationRegistry.isScopeAbbreviation(scope)) {
                throw new IllegalStateException("目录条目 Scope 缩写未受控: " + entry.getCode());
            }
            if (entry.getLevel() >= 2) {
                String domain = codePolicy.resolveDomainAbbreviation(entry.getCode());
                if (!abbreviationRegistry.isDomainAbbreviation(scope, domain)) {
                    throw new IllegalStateException("目录条目 Domain 缩写未受控: " + entry.getCode());
                }
            }
            // 标准目录不允许 _X_ 扩展项
            if (codePolicy.isExtensionCode(entry.getCode())) {
                throw new IllegalStateException("标准目录不允许 _X_ 扩展项: " + entry.getCode());
            }
            // L1 无父；L2/L3 必须有父
            if (entry.getLevel() == 1) {
                if (entry.getParentCode() != null && !entry.getParentCode().isBlank()) {
                    throw new IllegalStateException("L1 目录条目不应有父节点: " + entry.getCode());
                }
            } else if (entry.getParentCode() == null || entry.getParentCode().isBlank()) {
                throw new IllegalStateException("L2/L3 目录条目必须有父节点: " + entry.getCode());
            }
            // 子节点 code 必须以 parentCode + "_" 开头
            if (entry.getParentCode() != null && !entry.getParentCode().isBlank()
                    && !entry.getCode().startsWith(entry.getParentCode() + "_")) {
                throw new IllegalStateException(
                        "目录条目子节点 code 必须以 parentCode + '_' 开头: " + entry.getCode());
            }
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
            byCode.put(entry.getCode(), entry);
        }

        // 父子关系 + 深度一致性 + 跨 scope + 环路
        for (MaterialCategoryCatalogEntry entry : entries) {
            if (entry.getLevel() == 1) {
                continue;
            }
            MaterialCategoryCatalogEntry parent = byCode.get(entry.getParentCode());
            if (parent == null) {
                throw new IllegalStateException("目录条目父节点不存在（孤儿）: " + entry.getCode()
                        + " -> " + entry.getParentCode());
            }
            // L2 只能指向 L1；L3 只能指向 L2
            if (entry.getLevel() == 2 && parent.getLevel() != 1) {
                throw new IllegalStateException("L2 目录条目父节点必须是 L1: " + entry.getCode());
            }
            if (entry.getLevel() == 3 && parent.getLevel() != 2) {
                throw new IllegalStateException("L3 目录条目父节点必须是 L2: " + entry.getCode());
            }
            // 跨 scope 挂接
            String childScope = codePolicy.resolveScopeAbbreviation(entry.getCode());
            String parentScope = codePolicy.resolveScopeAbbreviation(entry.getParentCode());
            if (childScope != null && parentScope != null && !childScope.equals(parentScope)) {
                throw new IllegalStateException("目录条目跨 scope 挂接: " + entry.getCode());
            }
            // 声明 level 与按 parent 链计算的深度一致
            int depth = computeCatalogDepth(byCode, entry.getCode());
            if (depth != entry.getLevel()) {
                throw new IllegalStateException("目录条目声明 level 与父链深度不一致: " + entry.getCode()
                        + "（声明=" + entry.getLevel() + "，实际=" + depth + "）");
            }
            // L3 仅使用受控 Family 词（MC_<SCOPE>_<DOMAIN> 之后全部 token，如 WHEEL_TIRE / TRACTION_BATTERY）
            if (entry.getLevel() == 3) {
                String[] tokens = entry.getCode().split("_");
                String family = String.join("_", Arrays.copyOfRange(tokens, 3, tokens.length));
                if (!abbreviationRegistry.isApprovedFamilyShortName(family)) {
                    throw new IllegalStateException("目录条目 L3 Family 词未受控: " + entry.getCode());
                }
            }
        }

        // 固定规模
        long level1 = entries.stream().filter(e -> e.getLevel() == 1).count();
        long level2 = entries.stream().filter(e -> e.getLevel() == 2).count();
        long level3 = entries.stream().filter(e -> e.getLevel() == 3).count();
        if (entries.size() != TOTAL_COUNT || level1 != LEVEL1_COUNT
                || level2 != LEVEL2_COUNT || level3 != LEVEL3_COUNT) {
            throw new IllegalStateException("物料品类标准目录规模必须固定为 " + TOTAL_COUNT
                    + "（L1=" + LEVEL1_COUNT + "，L2=" + LEVEL2_COUNT + "，L3=" + LEVEL3_COUNT + "），"
                    + "当前: 总=" + entries.size() + "，L1=" + level1 + "，L2=" + level2 + "，L3=" + level3);
        }
        log.info("Material Category 标准目录校验通过: 总={}, L1={}, L2={}, L3={}",
                entries.size(), level1, level2, level3);
    }

    /**
     * 按目录 parentCode 链计算深度（含环路检测）
     */
    private int computeCatalogDepth(Map<String, MaterialCategoryCatalogEntry> byCode, String code) {
        int depth = 0;
        Set<String> visited = new HashSet<>();
        String current = code;
        while (current != null && !current.isBlank()) {
            if (!visited.add(current)) {
                throw new IllegalStateException("目录条目层级形成环路: " + code);
            }
            MaterialCategoryCatalogEntry entry = byCode.get(current);
            if (entry == null) {
                break;
            }
            depth++;
            current = entry.getParentCode();
        }
        return depth;
    }
}
