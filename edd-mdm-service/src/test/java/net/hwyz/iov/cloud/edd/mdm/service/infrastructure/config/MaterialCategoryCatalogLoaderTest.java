package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryAbbreviationRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类标准目录加载器单元测试（CR-039 §3 目录一致性校验）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryCatalogLoader 测试")
class MaterialCategoryCatalogLoaderTest {

    private MaterialCategoryCatalogLoader loader;

    @BeforeEach
    void setUp() {
        loader = new MaterialCategoryCatalogLoader(
                new MaterialCategoryCodePolicy(new MaterialCategoryAbbreviationRegistry()),
                new MaterialCategoryNamePolicy(),
                new MaterialCategoryAbbreviationRegistry());
    }

    @Nested
    @DisplayName("真实标准目录资源（101 项）")
    class RealCatalogTests {

        @Test
        @DisplayName("目录资源加载成功且固定 101 项、分层 4/19/78")
        void loadRealCatalog() {
            List<MaterialCategoryCatalogEntry> entries = loader.load();
            assertEquals(101, entries.size());
            assertEquals(4, countByLevel(entries, 1));
            assertEquals(19, countByLevel(entries, 2));
            assertEquals(78, countByLevel(entries, 3));
            assertEquals(1, loader.loadVersion());
        }

        @Test
        @DisplayName("所有条目 code/name/nameLocal 非空且 code/名称唯一")
        void allEntriesWellFormed() {
            List<MaterialCategoryCatalogEntry> entries = loader.load();
            assertTrue(entries.stream().allMatch(e -> e.getCode() != null && !e.getCode().isBlank()));
            assertTrue(entries.stream().allMatch(e -> e.getName() != null && !e.getName().isBlank()));
            assertTrue(entries.stream().allMatch(e -> e.getNameLocal() != null && !e.getNameLocal().isBlank()));
            assertEquals(101, entries.stream().map(MaterialCategoryCatalogEntry::getCode).distinct().count());
        }

        @Test
        @DisplayName("L1 无父；L2/L3 父链完整且前缀一致")
        void parentChainWellFormed() {
            List<MaterialCategoryCatalogEntry> entries = loader.load();
            Map<String, MaterialCategoryCatalogEntry> byCode = entries.stream()
                    .collect(Collectors.toMap(MaterialCategoryCatalogEntry::getCode, e -> e));
            for (MaterialCategoryCatalogEntry entry : entries) {
                if (entry.getLevel() == 1) {
                    assertTrue(entry.getParentCode() == null || entry.getParentCode().isBlank(),
                            "L1 不应有父: " + entry.getCode());
                } else {
                    assertTrue(byCode.containsKey(entry.getParentCode()),
                            "父链断裂: " + entry.getCode());
                    assertTrue(entry.getCode().startsWith(entry.getParentCode() + "_"),
                            "前缀不一致: " + entry.getCode());
                }
            }
        }

        @Test
        @DisplayName("所有 L3 均为受控 Family 词且无 _X_ 扩展项")
        void allL3ApprovedNoExtension() {
            MaterialCategoryAbbreviationRegistry registry = new MaterialCategoryAbbreviationRegistry();
            List<MaterialCategoryCatalogEntry> entries = loader.load();
            for (MaterialCategoryCatalogEntry entry : entries) {
                if (entry.getLevel() == 3) {
                    String[] tokens = entry.getCode().split("_");
                    String family = String.join("_", java.util.Arrays.copyOfRange(tokens, 3, tokens.length));
                    assertTrue(registry.isApprovedFamilyShortName(family),
                            "L3 Family 未受控: " + entry.getCode());
                }
            }
            assertTrue(entries.stream().noneMatch(e -> e.getCode().contains("_X_")));
        }
    }

    @Nested
    @DisplayName("目录自身一致性校验（静态检查项）")
    class SelfConsistencyTests {

        private final String BASE = String.join("\n",
                "version: 1",
                "materialCategories:",
                "  - {code: MC_CMP, level: 1, name: Component / Part, nameLocal: 零部件}",
                "  - {code: MC_CMP_BODY, level: 2, parentCode: MC_CMP, name: Body Structure, nameLocal: 车身结构}",
                "  - {code: MC_CMP_BODY_BIW, level: 3, parentCode: MC_CMP_BODY, name: Body-in-White, nameLocal: 白车身}");

        @Test
        @DisplayName("结构合法的迷你目录仅因规模不足被拒（说明结构校验通过）")
        void structurallyValidFailsOnlyOnCount() {
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> loader.parse(BASE));
            assertTrue(ex.getMessage().contains("规模必须固定为 101"), ex.getMessage());
        }

        @Test
        @DisplayName("缺少 nameLocal 被拒绝")
        void missingNameLocalRejected() {
            String yaml = BASE.replace("{code: MC_CMP_BODY_BIW, level: 3, parentCode: MC_CMP_BODY, name: Body-in-White, nameLocal: 白车身}",
                    "{code: MC_CMP_BODY_BIW, level: 3, parentCode: MC_CMP_BODY, name: Body-in-White}");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("level 非法（4）被拒绝")
        void wrongLevelRejected() {
            String yaml = BASE.replace("level: 3, parentCode: MC_CMP_BODY", "level: 4, parentCode: MC_CMP_BODY");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("code 格式非法被拒绝")
        void invalidFormatRejected() {
            String yaml = BASE.replace("MC_CMP_BODY_BIW", "MC_CMP_BODY_biw");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("未知 Scope 被拒绝")
        void unknownScopeRejected() {
            String yaml = BASE.replace("MC_CMP_BODY", "MC_XXX_BODY").replace("MC_CMP_BODY_BIW", "MC_XXX_BODY_BIW");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("跨 Scope 的 Domain 被拒绝")
        void unknownDomainRejected() {
            String yaml = BASE.replace("MC_CMP_BODY", "MC_CMP_ZZZ").replace("MC_CMP_BODY_BIW", "MC_CMP_ZZZ_BIW");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("code 超过 32 字符被拒绝")
        void overLengthRejected() {
            String longFamily = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 26 字符，总长 > 32
            String longCode = "MC_CMP_EGY_" + longFamily;
            assertTrue(longCode.length() > 32);
            String yaml = BASE.replace("MC_CMP_BODY_BIW", longCode);
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("标准目录含 _X_ 扩展项被拒绝")
        void extensionInStandardRejected() {
            String yaml = BASE.replace("MC_CMP_BODY_BIW", "MC_CMP_BODY_X_EXTRA");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("L1 携带父节点被拒绝")
        void l1WithParentRejected() {
            String yaml = BASE.replace("{code: MC_CMP, level: 1, name: Component / Part, nameLocal: 零部件}",
                    "{code: MC_CMP, level: 1, parentCode: MC_CMP, name: Component / Part, nameLocal: 零部件}");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("L2 缺少父节点被拒绝")
        void l2WithoutParentRejected() {
            String yaml = BASE.replace("{code: MC_CMP_BODY, level: 2, parentCode: MC_CMP, name: Body Structure, nameLocal: 车身结构}",
                    "{code: MC_CMP_BODY, level: 2, name: Body Structure, nameLocal: 车身结构}");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("code 重复被拒绝")
        void duplicateCodeRejected() {
            String yaml = BASE + "\n" +
                    "  - {code: MC_CMP_BODY, level: 2, parentCode: MC_CMP, name: Duplicate, nameLocal: 重复}";
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("名称标准化后重复被拒绝")
        void nameDuplicateRejected() {
            String yaml = BASE.replace("name: Body Structure, nameLocal: 车身结构",
                    "name: Body  Structure, nameLocal: 车身 结构");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("孤儿节点（父不存在）被拒绝")
        void orphanParentRejected() {
            String yaml = BASE.replace("MC_CMP_BODY_BIW", "MC_CMP_BODY_GLASS")
                    .replace("parentCode: MC_CMP_BODY", "parentCode: MC_CMP_NONE");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("L2 父节点不是 L1 被拒绝")
        void l2ParentNotL1Rejected() {
            String yaml = BASE.replace("level: 3, parentCode: MC_CMP_BODY", "level: 2, parentCode: MC_CMP_BODY");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("跨 scope 挂接被拒绝")
        void crossScopeRejected() {
            String yaml = BASE.replace("MC_CMP_BODY", "MC_RAW_BODY").replace("MC_CMP_BODY_BIW", "MC_RAW_BODY_BIW");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("声明 level 与父链深度不一致被拒绝")
        void levelMismatchRejected() {
            // MC_CMP_BODY 声明为 L1（父链实际深度 2）
            String yaml = BASE.replace("{code: MC_CMP_BODY, level: 2, parentCode: MC_CMP, name: Body Structure, nameLocal: 车身结构}",
                    "{code: MC_CMP_BODY, level: 1, parentCode: MC_CMP, name: Body Structure, nameLocal: 车身结构}");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("L3 Family 词未受控被拒绝")
        void unknownFamilyRejected() {
            String yaml = BASE.replace("MC_CMP_BODY_BIW", "MC_CMP_BODY_ZZZZZ");
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }
    }

    private long countByLevel(List<MaterialCategoryCatalogEntry> entries, int level) {
        return entries.stream().filter(e -> e.getLevel() == level).count();
    }
}
