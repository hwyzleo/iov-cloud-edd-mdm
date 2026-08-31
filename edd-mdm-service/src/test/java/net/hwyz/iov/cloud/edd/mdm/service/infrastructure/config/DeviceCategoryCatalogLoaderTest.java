package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备类别标准目录加载器单元测试（CR-037 §5.1 目录一致性校验）
 *
 * @author hwyz_leo
 */
@DisplayName("DeviceCategoryCatalogLoader 测试")
class DeviceCategoryCatalogLoaderTest {

    private DeviceCategoryCatalogLoader loader;

    @BeforeEach
    void setUp() {
        loader = new DeviceCategoryCatalogLoader(new DeviceCategoryCodePolicy(), new DeviceCategoryNamePolicy());
    }

    @Nested
    @DisplayName("真实标准目录资源")
    class RealCatalogTests {

        @Test
        @DisplayName("目录资源加载成功且固定 24 个设备族")
        void loadRealCatalog() {
            List<DeviceCategoryCatalogEntry> entries = loader.load();
            assertEquals(24, entries.size());
            assertEquals(1, loader.loadVersion());
        }

        @Test
        @DisplayName("所有条目 code/name/nameLocal 非空且 code 唯一")
        void allEntriesWellFormed() {
            List<DeviceCategoryCatalogEntry> entries = loader.load();
            assertTrue(entries.stream().allMatch(e -> e.getCode() != null && !e.getCode().isBlank()));
            assertTrue(entries.stream().allMatch(e -> e.getName() != null && !e.getName().isBlank()));
            assertTrue(entries.stream().allMatch(e -> e.getNameLocal() != null && !e.getNameLocal().isBlank()));
            assertEquals(24, entries.stream().map(DeviceCategoryCatalogEntry::getCode).distinct().count());
        }

        @Test
        @DisplayName("目录条目不含 tier/category/nodeType/recommendedNodeTypes（加载成功即证明）")
        void noForbiddenAttributes() {
            // 若含扁平字典禁止属性，load() 会在 parse 阶段抛出 IllegalStateException
            List<DeviceCategoryCatalogEntry> entries = loader.load();
            assertEquals(24, entries.size());
        }

        @Test
        @DisplayName("TBOX 条目含 legacy 同义词 aliases")
        void tboxHasAliases() {
            DeviceCategoryCatalogEntry tbox = loader.load().stream()
                    .filter(e -> "TBOX".equals(e.getCode())).findFirst().orElseThrow();
            assertTrue(tbox.getAliases().contains("TCU"));
            assertTrue(tbox.getAliases().contains("DC_TBOX"));
        }
    }

    @Nested
    @DisplayName("目录自身一致性校验（静态检查项）")
    class SelfConsistencyTests {

        @Test
        @DisplayName("数量不足 24 被拒绝")
        void wrongCountRejected() {
            assertThrows(IllegalStateException.class,
                    () -> loader.parse(String.join("\n", yaml(24).subList(0, 2))));
        }

        @Test
        @DisplayName("code 重复被拒绝")
        void duplicateCodeRejected() {
            String[] rows = yaml(24).toArray(new String[0]);
            rows[5] = "  - {code: FAMILY01, name: Family Six, nameLocal: 类别六}";
            assertThrows(IllegalStateException.class, () -> loader.parse(String.join("\n", rows)));
        }

        @Test
        @DisplayName("缺少 nameLocal 被拒绝")
        void missingNameLocalRejected() {
            String[] rows = yaml(24).toArray(new String[0]);
            rows[5] = "  - {code: FAMILY06, name: Family Six}";
            assertThrows(IllegalStateException.class, () -> loader.parse(String.join("\n", rows)));
        }

        @Test
        @DisplayName("code 携带节点规格语义被拒绝")
        void specCodeRejected() {
            String[] rows = yaml(24).toArray(new String[0]);
            rows[5] = "  - {code: TBOX_4G, name: Telematics Box, nameLocal: 车载通信终端}";
            assertThrows(IllegalStateException.class, () -> loader.parse(String.join("\n", rows)));
        }

        @Test
        @DisplayName("目录条目含 tier 属性被拒绝")
        void forbiddenAttributeRejected() {
            String[] rows = yaml(24).toArray(new String[0]);
            rows[5] = "  - tier: CORE\n    code: FAMILY06\n    name: Family Six\n    nameLocal: 类别六";
            assertThrows(IllegalStateException.class, () -> loader.parse(String.join("\n", rows)));
        }

        @Test
        @DisplayName("name 标准化后重复被拒绝")
        void nameDuplicateRejected() {
            String[] rows = yaml(24).toArray(new String[0]);
            rows[5] = "  - {code: FAMILY06, name: Family  ONE, nameLocal: 类别六}";
            assertThrows(IllegalStateException.class, () -> loader.parse(String.join("\n", rows)));
        }
    }

    /**
     * 生成 N 行合法目录条目（code=FAMILY01..24, name=Family One..Twenty Four, nameLocal=类别一..二十四）
     */
    private List<String> yaml(int count) {
        java.util.ArrayList<String> rows = new java.util.ArrayList<>();
        rows.add("version: 1");
        rows.add("deviceCategories:");
        for (int i = 1; i <= count; i++) {
            rows.add(String.format("  - {code: FAMILY%02d, name: Family %s, nameLocal: 类别%s}",
                    i, numberWord(i), chineseNumber(i)));
        }
        return rows;
    }

    private String numberWord(int i) {
        String[] words = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen",
                "Eighteen", "Nineteen", "Twenty", "Twenty One", "Twenty Two", "Twenty Three", "Twenty Four"};
        return words[i - 1];
    }

    private String chineseNumber(int i) {
        String[] words = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十",
                "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
                "二十一", "二十二", "二十三", "二十四"};
        return words[i - 1];
    }
}
