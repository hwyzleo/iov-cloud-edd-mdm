package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryHierarchyPolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryNamePolicy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 物料品类存量治理审计（CR-039 §9）
 * <p>
 * 识别存量 MaterialCategory 中的 legacy code、重复名称、父子前缀不一致、孤儿节点、深度>3、
 * 跨 scope 挂接及非 L3 Part 引用，输出审计报告供治理人员人工确认 legacy→standard 映射后
 * 分批收敛；不自动改写、不自动合并、不自动失效、不直接改 code。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialCategoryLegacyAudit {

    private final MaterialCategoryCodePolicy codePolicy;
    private final MaterialCategoryNamePolicy namePolicy;
    private final MaterialCategoryHierarchyPolicy hierarchyPolicy;

    /**
     * 对现存品类执行存量治理审计
     *
     * @param existing 现存品类（row_valid=1）
     * @param catalog  标准目录条目
     * @param partRefCountByCategory Part.categoryCode → 引用数量（可为空）
     * @return 审计报告
     */
    public MaterialCategoryLegacyAuditResult audit(
            List<MaterialCategory> existing,
            List<MaterialCategoryCatalogEntry> catalog,
            Map<String, Long> partRefCountByCategory) {
        MaterialCategoryLegacyAuditResult result = MaterialCategoryLegacyAuditResult.builder()
                .totalExisting(existing == null ? 0 : existing.size())
                .totalPartCategoryRefs(partRefCountByCategory == null ? 0 : partRefCountByCategory.size())
                .build();

        Map<String, MaterialCategoryCatalogEntry> codeToEntry = new HashMap<>();
        Map<String, String> normalizedNameToCode = new HashMap<>();
        Map<String, String> normalizedNameLocalToCode = new HashMap<>();
        if (catalog != null) {
            for (MaterialCategoryCatalogEntry entry : catalog) {
                codeToEntry.put(entry.getCode(), entry);
                String nn = namePolicy.normalize(entry.getName());
                if (nn != null) {
                    normalizedNameToCode.put(nn, entry.getCode());
                }
                String nloc = namePolicy.normalize(entry.getNameLocal());
                if (nloc != null) {
                    normalizedNameLocalToCode.put(nloc, entry.getCode());
                }
            }
        }

        if (existing == null || existing.isEmpty()) {
            log.info("Material Category 存量治理审计完成: 无现存品类");
            return result;
        }

        Map<String, MaterialCategory> byCode = existing.stream()
                .collect(Collectors.toMap(MaterialCategory::getCode, c -> c, (a, b) -> a));

        for (MaterialCategory category : existing) {
            String code = category.getCode();
            String parentCode = category.getParentCode();

            // 1) legacy code：不在标准目录
            if (!codeToEntry.containsKey(code)) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_LEGACY_CODE,
                        "code 不在标准目录中，建议人工确认 legacy_category_code → standard_l3_code 映射后收敛", null));
            }

            // 2) 名称重复：标准化后名称与标准目录一致（不同 code）
            String nn = namePolicy.normalize(category.getName());
            String nloc = namePolicy.normalize(category.getNameLocal());
            if (nn != null && normalizedNameToCode.containsKey(nn)
                    && !normalizedNameToCode.get(nn).equals(code)) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE,
                        "英文名标准化后与标准目录重复", normalizedNameToCode.get(nn)));
            } else if (nloc != null && normalizedNameLocalToCode.containsKey(nloc)
                    && !normalizedNameLocalToCode.get(nloc).equals(code)) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE,
                        "中文名标准化后与标准目录重复", normalizedNameLocalToCode.get(nloc)));
            }

            // 3) 孤儿节点：parentCode 指向不存在的父级
            if (parentCode != null && !parentCode.isBlank() && !byCode.containsKey(parentCode)) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_ORPHAN,
                        "parentCode 指向不存在的父级: " + parentCode, null));
            }

            // 4) 深度>3：沿 parentCode 链计算深度
            int depth = hierarchyPolicy.computeDepth(byCode, code);
            if (depth > MaterialCategoryHierarchyPolicy.MAX_DEPTH) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_DEPTH_EXCEEDED,
                        "层级深度超过 3（当前 " + depth + "），禁止第四层", null));
            }

            // 5) 父子前缀不一致：子节点 code 未以 parentCode + "_" 开头
            if (parentCode != null && !parentCode.isBlank() && !code.startsWith(parentCode + "_")) {
                result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_PARENT_PREFIX_INCONSISTENT,
                        "子节点 code 未以 parentCode + '_' 开头: parentCode=" + parentCode, null));
            }

            // 6) 跨 scope 挂接：子节点 Scope 与父节点 Scope 不一致
            if (parentCode != null && !parentCode.isBlank() && byCode.containsKey(parentCode)) {
                String childScope = codePolicy.resolveScopeAbbreviation(code);
                String parentScope = codePolicy.resolveScopeAbbreviation(parentCode);
                if (childScope != null && parentScope != null && !childScope.equals(parentScope)) {
                    result.addFinding(finding(code, MaterialCategoryLegacyAuditResult.TYPE_CROSS_SCOPE,
                            "子节点 Scope(" + childScope + ") 与父节点 Scope(" + parentScope + ") 不一致", null));
                }
            }
        }

        // 7) 非 L3 Part 引用：Part.categoryCode 指向非叶子（深度!=3 或存在 ACTIVE 子节点）
        if (partRefCountByCategory != null) {
            Map<String, Long> activeChildCountByCode = new HashMap<>();
            Set<String> nonLeafRefs = new HashSet<>();
            for (MaterialCategory category : existing) {
                if (category.getParentCode() != null && !category.getParentCode().isBlank()) {
                    activeChildCountByCode.merge(category.getParentCode(), 1L, Long::sum);
                }
            }
            for (String referencedCode : partRefCountByCategory.keySet()) {
                if (!byCode.containsKey(referencedCode)) {
                    continue;
                }
                int refDepth = hierarchyPolicy.computeDepth(byCode, referencedCode);
                boolean hasActiveChild = activeChildCountByCode.getOrDefault(referencedCode, 0L) > 0;
                if (refDepth != MaterialCategoryHierarchyPolicy.MAX_DEPTH || hasActiveChild) {
                    if (nonLeafRefs.add(referencedCode)) {
                        result.addFinding(finding(referencedCode,
                                MaterialCategoryLegacyAuditResult.TYPE_PART_NON_LEAF_REF,
                                "被 Part 引用但非可归类 L3 叶子（深度=" + refDepth + "），需迁移到标准 L3 叶子", null));
                    }
                }
            }
        }

        log.info("Material Category 存量治理审计完成: 现存={}, legacy={}, 名称重复={}, 孤儿={}, 深度超限={}, "
                        + "父前缀不一致={}, 跨scope={}, 非L3引用={}",
                result.getTotalExisting(),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_LEGACY_CODE),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_ORPHAN),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_DEPTH_EXCEEDED),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_PARENT_PREFIX_INCONSISTENT),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_CROSS_SCOPE),
                result.countByType(MaterialCategoryLegacyAuditResult.TYPE_PART_NON_LEAF_REF));
        return result;
    }

    private MaterialCategoryLegacyAuditResult.Finding finding(String code, String type, String detail,
                                                              String suggestedStandardCode) {
        return MaterialCategoryLegacyAuditResult.Finding.builder()
                .code(code).type(type).detail(detail).suggestedStandardCode(suggestedStandardCode)
                .build();
    }
}
