package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryNamePolicy;
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
 * 设备类别标准目录加载器（CR-037 §5）
 * <p>
 * 读取版本化标准目录资源（masterdata/device-category-catalog-v1.yaml），
 * 并对目录自身一致性做静态校验：
 * <ul>
 *   <li>目录固定包含 24 个默认设备族，不存在 Core / Conditional / Extension 分支</li>
 *   <li>code、name、nameLocal 在目录内唯一</li>
 *   <li>每条记录均具备 code、name 和 nameLocal</li>
 *   <li>code 为受控设备族格式且不含通信制式、安装方位、功率、分辨率、线数或硬件代次等节点级规格</li>
 *   <li>目录记录不包含 tier、category、parentCode、nodeType、recommendedNodeTypes 属性</li>
 *   <li>aliases 只能用于检索和 legacy 映射，不生成第二条同义类别</li>
 * </ul>
 * 目录自身非法时抛出 IllegalStateException（标记 Catalog INVALID），禁止初始化；
 * 由于初始化仅在显式触发（启动开关默认关闭 / 后台动作）时执行，不阻断服务 readiness。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceCategoryCatalogLoader {

    /**
     * 版本化标准目录资源路径
     */
    public static final String CATALOG_RESOURCE = "masterdata/device-category-catalog-v1.yaml";

    /**
     * 默认设备族数量（CR-037 固定 24）
     */
    public static final int DEFAULT_DEVICE_FAMILY_COUNT = 24;

    /**
     * 目录禁止携带的运行期属性（DeviceCategory 为扁平字典）
     */
    private static final Set<String> FORBIDDEN_ATTRIBUTES = Set.of(
            "tier", "category", "parentCode", "nodeType", "recommendedNodeTypes");

    private final DeviceCategoryCodePolicy codePolicy;
    private final DeviceCategoryNamePolicy namePolicy;

    /**
     * 从 classpath 加载并校验标准目录
     *
     * @return 目录条目列表（24 个标准设备族）
     * @throws IllegalStateException 目录资源缺失或目录自身一致性校验失败
     */
    public List<DeviceCategoryCatalogEntry> load() {
        try (InputStream is = new ClassPathResource(CATALOG_RESOURCE).getInputStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(is);
            return parseAndValidate(root);
        } catch (IOException e) {
            throw new IllegalStateException("读取设备类别标准目录资源失败: " + CATALOG_RESOURCE, e);
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
            throw new IllegalStateException("读取设备类别标准目录资源失败: " + CATALOG_RESOURCE, e);
        }
    }

    /**
     * 解析 YAML 中的目录版本号（供测试直接注入 YAML 字符串）
     *
     * @param yamlContent YAML 内容
     * @return 目录版本号
     */
    public int parseVersion(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);
        return resolveVersion(root);
    }

    private int resolveVersion(Map<String, Object> root) {
        Object version = root == null ? null : root.get("version");
        if (!(version instanceof Number)) {
            throw new IllegalStateException("设备类别标准目录缺少 version");
        }
        return ((Number) version).intValue();
    }

    /**
     * 解析并校验 YAML 内容（供测试直接注入 YAML 字符串）
     *
     * @param yamlContent YAML 内容
     * @return 目录条目列表
     */
    public List<DeviceCategoryCatalogEntry> parse(String yamlContent) {
        Yaml yaml = new Yaml();
        Map<String, Object> root = yaml.load(yamlContent);
        return parseAndValidate(root);
    }

    @SuppressWarnings("unchecked")
    private List<DeviceCategoryCatalogEntry> parseAndValidate(Map<String, Object> root) {
        if (root == null) {
            throw new IllegalStateException("设备类别标准目录为空");
        }
        Object listObj = root.get("deviceCategories");
        if (!(listObj instanceof List)) {
            throw new IllegalStateException("设备类别标准目录缺少 deviceCategories 列表");
        }
        List<DeviceCategoryCatalogEntry> entries = new ArrayList<>();
        for (Object item : (List<?>) listObj) {
            if (!(item instanceof Map)) {
                continue;
            }
            Map<String, Object> m = (Map<String, Object>) item;
            // 扁平字典：禁止运行期层级/父级/推荐节点类型属性
            for (String forbidden : FORBIDDEN_ATTRIBUTES) {
                if (m.containsKey(forbidden)) {
                    throw new IllegalStateException(
                            "目录条目不应包含扁平字典属性 " + forbidden + ": " + m.get("code"));
                }
            }
            String code = (String) m.get("code");
            String name = (String) m.get("name");
            String nameLocal = (String) m.get("nameLocal");
            String description = (String) m.get("description");
            List<String> aliases = m.get("aliases") instanceof List
                    ? new ArrayList<>((List<String>) m.get("aliases"))
                    : new ArrayList<>();
            Integer sortOrder = m.get("sortOrder") instanceof Number
                    ? ((Number) m.get("sortOrder")).intValue()
                    : 0;
            entries.add(DeviceCategoryCatalogEntry.builder()
                    .code(code)
                    .name(name)
                    .nameLocal(nameLocal)
                    .description(description)
                    .aliases(aliases)
                    .sortOrder(sortOrder)
                    .build());
        }
        validateSelfConsistency(entries);
        return entries;
    }

    /**
     * 目录自身一致性静态校验（CR-037 §5.1）
     * <p>
     * 先逐条校验必填/格式/唯一性，最后校验设备族数量固定为 24（便于逐项定位非法条目）。
     */
    private void validateSelfConsistency(List<DeviceCategoryCatalogEntry> entries) {
        Set<String> codes = new HashSet<>();
        Set<String> names = new HashSet<>();
        Set<String> nameLocals = new HashSet<>();
        for (DeviceCategoryCatalogEntry entry : entries) {
            if (entry.getCode() == null || entry.getCode().isBlank()
                    || entry.getName() == null || entry.getName().isBlank()
                    || entry.getNameLocal() == null || entry.getNameLocal().isBlank()) {
                throw new IllegalStateException("目录条目必须同时具备 code/name/nameLocal: " + entry.getCode());
            }
            if (!codePolicy.isValidDeviceFamilyCode(entry.getCode())) {
                throw new IllegalStateException(
                        "目录条目 code 非设备族格式或包含节点规格语义: " + entry.getCode());
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
        }
        if (entries.size() != DEFAULT_DEVICE_FAMILY_COUNT) {
            throw new IllegalStateException(
                    "设备类别标准目录设备族数量必须固定为 " + DEFAULT_DEVICE_FAMILY_COUNT + "，当前: " + entries.size());
        }
    }
}
