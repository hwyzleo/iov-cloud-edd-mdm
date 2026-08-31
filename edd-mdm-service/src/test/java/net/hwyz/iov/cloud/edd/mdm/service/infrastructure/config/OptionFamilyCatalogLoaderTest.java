package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCategoryPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogTier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选项族标准目录加载器单元测试（CR-035 §5.1 目录一致性校验）
 *
 * @author hwyz_leo
 */
@DisplayName("OptionFamilyCatalogLoader 测试")
class OptionFamilyCatalogLoaderTest {

    private OptionFamilyCatalogLoader loader;

    @BeforeEach
    void setUp() {
        loader = new OptionFamilyCatalogLoader(new OptionFamilyCodePolicy(), new OptionFamilyNamePolicy());
    }

    @Nested
    @DisplayName("真实标准目录资源")
    class RealCatalogTests {

        @Test
        @DisplayName("目录资源加载成功且条目数正确（Core 22 + Conditional 34）")
        void loadRealCatalog() {
            List<OptionFamilyCatalogEntry> entries = loader.load();
            long coreCount = entries.stream().filter(e -> e.getTier() == OptionFamilyCatalogTier.CORE).count();
            long conditionalCount = entries.stream().filter(e -> e.getTier() == OptionFamilyCatalogTier.CONDITIONAL).count();
            assertEquals(22, coreCount);
            assertEquals(34, conditionalCount);
            assertEquals(56, entries.size());
        }

        @Test
        @DisplayName("所有 Core 条目 name/category/激活条件符合规范")
        void coreEntriesWellFormed() {
            List<OptionFamilyCatalogEntry> entries = loader.load();
            List<OptionFamilyCatalogEntry> core = entries.stream()
                    .filter(e -> e.getTier() == OptionFamilyCatalogTier.CORE).toList();
            assertTrue(core.stream().allMatch(e -> e.getActivationCondition() == null));
            assertTrue(core.stream().allMatch(e -> e.getDescription() == null));
        }
    }

    @Nested
    @DisplayName("目录自身一致性校验（静态检查项）")
    class SelfConsistencyTests {

        @Test
        @DisplayName("code 重复被拒绝")
        void duplicateCodeRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR}
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Wheel, nameLocal: 轮毂, category: EXTERIOR}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("前缀与 category 不一致被拒绝")
        void prefixCategoryMismatchRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: INTERIOR}
                    """;
            assertThrows(OptionFamilyCategoryPrefixMismatchException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("非标准格式 code 被拒绝")
        void nonStandardCodeRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("英文名重复被拒绝")
        void duplicateEnglishNameRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR}
                      - {tier: CORE, code: OF_EXT_BODY_COLOR_2, name: "  body   color ", nameLocal: 轮毂, category: EXTERIOR}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("CONDITIONAL 缺少 activationCondition 被拒绝")
        void conditionalWithoutActivationConditionRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CONDITIONAL, code: OF_EXT_STYLE_PACKAGE, name: Exterior Styling Package, nameLocal: 外观风格套件, category: EXTERIOR}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("CORE 携带 activationCondition 被拒绝")
        void coreWithActivationConditionRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR, activationCondition: 不应存在}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("非法 tier 被拒绝")
        void invalidTierRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: EXTENSION, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("非法 category 被拒绝")
        void invalidCategoryRejected() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: INVALID}
                    """;
            assertThrows(IllegalStateException.class, () -> loader.parse(yaml));
        }

        @Test
        @DisplayName("合法目录解析成功")
        void validCatalogParsed() {
            String yaml = """
                    version: 1
                    optionFamilies:
                      - {tier: CORE, code: OF_EXT_BODY_COLOR, name: Body Color, nameLocal: 车身颜色, category: EXTERIOR}
                      - {tier: CONDITIONAL, code: OF_EXT_STYLE_PACKAGE, name: Exterior Styling Package, nameLocal: 外观风格套件, category: EXTERIOR, activationCondition: 存在不可拆外观套件}
                    """;
            List<OptionFamilyCatalogEntry> entries = loader.parse(yaml);
            assertEquals(2, entries.size());
            assertEquals(OptionFamilyCatalogTier.CORE, entries.get(0).getTier());
            assertEquals("存在不可拆外观套件", entries.get(1).getActivationCondition());
        }
    }
}
