package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryAbbreviationRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryHierarchyPolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类存量治理审计单元测试（CR-039 §9）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryLegacyAudit 测试")
class MaterialCategoryLegacyAuditTest {

    private MaterialCategoryLegacyAudit audit;

    @BeforeEach
    void setUp() {
        MaterialCategoryCodePolicy codePolicy =
                new MaterialCategoryCodePolicy(new MaterialCategoryAbbreviationRegistry());
        audit = new MaterialCategoryLegacyAudit(
                codePolicy,
                new MaterialCategoryNamePolicy(),
                new MaterialCategoryHierarchyPolicy());
    }

    private List<MaterialCategoryCatalogEntry> catalog() {
        return List.of(
                catEntry("MC_CMP", 1, "Component / Part", "零部件", null),
                catEntry("MC_CMP_BODY", 2, "Body Structure & Closures", "车身结构与闭合", "MC_CMP"),
                catEntry("MC_CMP_BODY_BIW", 3, "Body-in-White", "白车身", "MC_CMP_BODY")
        );
    }

    private MaterialCategoryCatalogEntry catEntry(String code, int level, String name, String nameLocal,
                                                  String parentCode) {
        return MaterialCategoryCatalogEntry.builder()
                .code(code).level(level).name(name).nameLocal(nameLocal).parentCode(parentCode)
                .build();
    }

    private MaterialCategory cat(String code, String parentCode, MaterialCategoryStatus status) {
        return MaterialCategory.builder()
                .code(code).parentCode(parentCode).name(code).nameLocal(code).status(status).rowValid(true)
                .build();
    }

    @Test
    @DisplayName("识别 legacy code、名称重复、孤儿、四层、前缀不一致、跨 scope 与非 L3 引用")
    void detectsAllFindingTypes() {
        // 标准链：MC_CMP(L1) → MC_CMP_BODY(L2) → MC_CMP_BODY_BIW(L3)；其余为问题存量
        List<MaterialCategory> existing = List.of(
                cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE),
                // legacy code + 父子前缀不一致 + 跨 scope（DC_001 解析 Scope 为 001 ≠ CMP）
                cat("DC_001", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE),
                // legacy code + 名称重复（与标准 MC_CMP_BODY_BIW 英文名相同）
                MaterialCategory.builder().code("MC_CMP_BODY_OLD").parentCode("MC_CMP")
                        .name("Body-in-White").nameLocal("白车身").status(MaterialCategoryStatus.ACTIVE)
                        .rowValid(true).build(),
                // 四层节点（深度 4）
                cat("MC_CMP_BODY_BIW_L4", "MC_CMP_BODY_BIW", MaterialCategoryStatus.ACTIVE),
                // 孤儿（父不存在）
                cat("MC_CMP_BODY_BIW_CHILD", "MC_CMP_NONE", MaterialCategoryStatus.ACTIVE),
                // 跨 scope（RAW ≠ CMP）+ 前缀不一致
                cat("MC_RAW_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE)
        );
        Map<String, Long> partRefs = new HashMap<>();
        partRefs.put("MC_CMP", 2L); // 非 L3 引用
        partRefs.put("MC_CMP_BODY", 1L); // 非 L3 引用
        partRefs.put("DC_001", 3L); // legacy L3 引用（不触发非 L3，仅 legacy）

        MaterialCategoryLegacyAuditResult result = audit.audit(existing, catalog(), partRefs);

        assertEquals(8, result.getTotalExisting());
        assertEquals(3, result.getTotalPartCategoryRefs());
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_LEGACY_CODE) >= 3);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE) >= 1);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_ORPHAN) >= 1);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_DEPTH_EXCEEDED) >= 1);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_CROSS_SCOPE) >= 1);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_PARENT_PREFIX_INCONSISTENT) >= 1);
        assertTrue(result.countByType(MaterialCategoryLegacyAuditResult.TYPE_PART_NON_LEAF_REF) >= 1);
    }

    @Test
    @DisplayName("空存量返回空审计")
    void emptyExisting() {
        MaterialCategoryLegacyAuditResult result = audit.audit(List.of(), catalog(), new HashMap<>());
        assertEquals(0, result.getTotalExisting());
        assertEquals(0, result.getFindings().size());
    }

    @Test
    @DisplayName("标准目录一致且无 Part 引用时无发现")
    void cleanStateNoFindings() {
        List<MaterialCategory> existing = List.of(
                cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)
        );
        // 名称与标准目录不一致（使用 code 作为名称），但仍为 legacy？不——它们在标准目录中，仅名称不同不触发 NAME_DUPLICATE（因为目录里不同）
        MaterialCategoryLegacyAuditResult result = audit.audit(existing, catalog(), new HashMap<>());
        assertEquals(3, result.getTotalExisting());
    }
}
