package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选项族名称策略单元测试（CR-035 §3.3）
 *
 * @author hwyz_leo
 */
@DisplayName("OptionFamilyNamePolicy 测试")
class OptionFamilyNamePolicyTest {

    private OptionFamilyNamePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new OptionFamilyNamePolicy();
    }

    @Nested
    @DisplayName("名称标准化")
    class NormalizeTests {

        @Test
        @DisplayName("trim 首尾空白 + 连续空白折叠")
        void trimAndCollapse() {
            assertEquals("body color", policy.normalize("  Body   Color  "));
            assertEquals("body color", policy.normalize("\tBody\tColor\n"));
        }

        @Test
        @DisplayName("英文 Unicode case-fold（大小写不敏感）")
        void caseFold() {
            assertEquals("body color", policy.normalize("BODY COLOR"));
            assertEquals("body color", policy.normalize("Body Color"));
        }

        @Test
        @DisplayName("中文名称移除全角/半角空格差异")
        void chineseSpaceNormalization() {
            assertEquals("车身颜色", policy.normalize("车身颜色"));
            assertEquals("车身颜色", policy.normalize("车身 颜色"));
            assertEquals("车身颜色", policy.normalize("车身　颜色")); // 全角空格
            assertEquals("车身颜色", policy.normalize(" 车身 颜色 "));
        }

        @Test
        @DisplayName("null 名称返回 null")
        void nullName() {
            assertNull(policy.normalize(null));
        }
    }

    @Nested
    @DisplayName("名称防重")
    class DuplicateTests {

        @Test
        @DisplayName("英文名标准化后重复被命中")
        void englishDuplicateFound() {
            OptionFamily existing = family("OF_EXT_BODY_COLOR", "Body Color", "车身颜色");
            Optional<OptionFamily> dup = policy.findDuplicate(List.of(existing), "  body   color ", null);
            assertTrue(dup.isPresent());
            assertEquals("OF_EXT_BODY_COLOR", dup.get().getCode());
        }

        @Test
        @DisplayName("中文名全半角空格差异重复被命中")
        void chineseDuplicateFound() {
            OptionFamily existing = family("OF_EXT_BODY_COLOR", "Body Color", "车身颜色");
            Optional<OptionFamily> dup = policy.findDuplicate(List.of(existing), "Wheel", "车身 颜色");
            assertTrue(dup.isPresent());
        }

        @Test
        @DisplayName("无重复返回空")
        void noDuplicate() {
            OptionFamily existing = family("OF_EXT_BODY_COLOR", "Body Color", "车身颜色");
            Optional<OptionFamily> dup = policy.findDuplicate(List.of(existing), "Wheel", "轮毂");
            assertFalse(dup.isPresent());
        }

        @Test
        @DisplayName("英文名跨语言不误判（英文 vs 中文名）")
        void crossLanguageNotMatched() {
            OptionFamily existing = family("OF_EXT_BODY_COLOR", "Body Color", "车身颜色");
            // 新族英文名 "车身颜色"（非 Title Case 英文）与现存中文名相同，但只做英文对英文、中文对中文比对
            Optional<OptionFamily> dup = policy.findDuplicate(List.of(existing), "车身颜色", "BODY COLOR");
            assertFalse(dup.isPresent());
        }

        @Test
        @DisplayName("空现存族列表返回空")
        void emptyExisting() {
            assertFalse(policy.findDuplicate(List.of(), "Body Color", "车身颜色").isPresent());
        }
    }

    private OptionFamily family(String code, String name, String nameLocal) {
        return OptionFamily.builder()
                .code(code)
                .name(name)
                .nameLocal(nameLocal)
                .category(OptionFamilyCategory.EXTERIOR)
                .status(OptionFamilyStatus.ACTIVE)
                .rowValid(true)
                .build();
    }
}
