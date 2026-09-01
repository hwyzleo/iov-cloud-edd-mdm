package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类名称策略单元测试（CR-039 §5.2）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryNamePolicy 测试")
class MaterialCategoryNamePolicyTest {

    private MaterialCategoryNamePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new MaterialCategoryNamePolicy();
    }

    @Nested
    @DisplayName("名称标准化")
    class NormalizeTests {

        @Test
        @DisplayName("trim 首尾空白")
        void trimWhitespace() {
            assertEquals("brake system", policy.normalize("  Brake System  "));
        }

        @Test
        @DisplayName("连续空白折叠为一个空格")
        void collapseWhitespace() {
            assertEquals("brake system", policy.normalize("Brake   System"));
        }

        @Test
        @DisplayName("英文 Unicode case-fold")
        void caseFold() {
            assertEquals("component / part", policy.normalize("Component / Part"));
        }

        @Test
        @DisplayName("中文全半角空白统一")
        void chineseFullHalfWidth() {
            assertEquals("零部件", policy.normalize("零 部件"));
            assertEquals("零部件", policy.normalize("零\u3000部件"));
        }

        @Test
        @DisplayName("null 返回 null")
        void nullName() {
            assertEquals(null, policy.normalize(null));
        }
    }

    @Nested
    @DisplayName("防重查找")
    class FindDuplicateTests {

        private MaterialCategory category(String code, String name, String nameLocal) {
            return MaterialCategory.builder()
                    .code(code).name(name).nameLocal(nameLocal).rowValid(true)
                    .build();
        }

        @Test
        @DisplayName("英文名标准化后相同则命中")
        void englishDuplicateFound() {
            List<MaterialCategory> existing = List.of(category("MC_CMP_BODY", "Body Structure & Closures", "车身结构与闭合"));
            Optional<MaterialCategory> dup = policy.findDuplicate(existing, "Body Structure  & Closures", "车身");
            assertTrue(dup.isPresent());
            assertEquals("MC_CMP_BODY", dup.get().getCode());
        }

        @Test
        @DisplayName("中文名标准化后相同则命中")
        void chineseDuplicateFound() {
            List<MaterialCategory> existing = List.of(category("MC_CMP_BODY", "Body Structure & Closures", "车身结构与闭合"));
            Optional<MaterialCategory> dup = policy.findDuplicate(existing, "Body", "车身 结构与闭合");
            assertTrue(dup.isPresent());
            assertEquals("MC_CMP_BODY", dup.get().getCode());
        }

        @Test
        @DisplayName("无重复返回空")
        void noDuplicate() {
            List<MaterialCategory> existing = List.of(category("MC_CMP_BODY", "Body Structure & Closures", "车身结构与闭合"));
            assertFalse(policy.findDuplicate(existing, "Chassis", "底盘").isPresent());
        }

        @Test
        @DisplayName("空列表返回空")
        void emptyList() {
            assertFalse(policy.findDuplicate(List.of(), "Brake", "制动").isPresent());
        }
    }
}
