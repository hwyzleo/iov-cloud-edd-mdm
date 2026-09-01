package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFamilyPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFormatInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选项码编码策略单元测试（CR-040 §2/§3）
 *
 * @author hwyz_leo
 */
@DisplayName("OptionCodeCodePolicy 测试")
class OptionCodeCodePolicyTest {

    private OptionCodeCodePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new OptionCodeCodePolicy();
    }

    @Nested
    @DisplayName("九类 category 前缀合法 code")
    class NinePrefixValidCodeTests {

        @ParameterizedTest(name = "{0} 应合法")
        @CsvSource({
                "OC_EXT_BODY_COLOR_BLACK",
                "OC_INT_SEAT_UPHOLSTERY_NAPPA_LEATHER",
                "OC_PWR_DRIVE_TYPE_AWD",
                "OC_CHS_SUSPENSION_ADAPTIVE",
                "OC_SMART_ADAS_SENSOR_LIDAR",
                "OC_COMF_CLIMATE_CONTROL_DUAL_ZONE",
                "OC_SAFE_AIRBAG_FRONT",
                "OC_ACC_CHARGING_EQUIPMENT_WALLBOX",
                "OC_OTH_STEERING_POSITION_LEFT"
        })
        void validCode(String code) {
            assertTrue(policy.isValidFormat(code));
        }
    }

    @Nested
    @DisplayName("标准族与 _X_ 扩展族派生主干")
    class DerivedStemTests {

        @Test
        @DisplayName("标准族派生主干")
        void standardFamilyStem() {
            assertEquals("OC_EXT_BODY_COLOR_", policy.deriveExpectedStem("OF_EXT_BODY_COLOR"));
            assertEquals("OC_PWR_DRIVE_TYPE_", policy.deriveExpectedStem("OF_PWR_DRIVE_TYPE"));
            assertEquals("OC_INT_SEAT_UPHOLSTERY_", policy.deriveExpectedStem("OF_INT_SEAT_UPHOLSTERY"));
        }

        @Test
        @DisplayName("企业扩展族（_X_）派生主干")
        void extensionFamilyStem() {
            assertEquals("OC_EXT_X_SPECIAL_PAINT_", policy.deriveExpectedStem("OF_EXT_X_SPECIAL_PAINT"));
            assertEquals("OC_SMART_X_SENSOR_CLEANING_", policy.deriveExpectedStem("OF_SMART_X_SENSOR_CLEANING"));
        }

        @Test
        @DisplayName("legacy 族（非 OF_ 开头）无法派生主干")
        void legacyFamilyReturnsNull() {
            assertNull(policy.deriveExpectedStem("COLOR"));
            assertNull(policy.deriveExpectedStem("OF"));
            assertNull(policy.deriveExpectedStem(null));
        }

        @Test
        @DisplayName("扩展族下新建扩展值 code 合法且族匹配")
        void extensionFamilyValueValid() {
            assertTrue(policy.isValidFormat("OC_EXT_X_SPECIAL_PAINT_MATTE_GRAY"));
            policy.validateFamilyMatch("OC_EXT_X_SPECIAL_PAINT_MATTE_GRAY", "OF_EXT_X_SPECIAL_PAINT");
        }
    }

    @Nested
    @DisplayName("validateFormat 校验（812127）")
    class ValidateFormatTests {

        @ParameterizedTest(name = "{0} 应非法")
        @CsvSource({
                "OC_EXT",                 // 缺少 FAMILY_SEMANTIC 与 VALUE
                "OC_EXT_",                // 尾随下划线
                "oc_ext_body_color_black",// 小写
                "OC_ext_BODY_COLOR_BLACK",// 混合大小写
                "OC_EXT BODY_COLOR_BLACK",// 空格
                "OC_EXT_BODY_COLOR-BLACK",// 连字符
                "OC_EXT_BODY_COLOR_黑色",  // 非 ASCII
                "OC_EXT__BODY_COLOR_BLACK",// 连续下划线
                "_OC_EXT_BODY_COLOR_BLACK",// 首部下划线
                "OC_EXT_BODY_COLOR_BLACK_",// 尾随下划线
                "OC_WHEEL_BLACK"          // 非法前缀（WHEEL 不在九类中）
        })
        void invalidFormat(String code) {
            assertFalse(policy.isValidFormat(code));
            assertThrows(OptionCodeFormatInvalidException.class, () -> policy.validateFormat(code));
        }

        @Test
        @DisplayName("null / 空白 code 抛格式异常")
        void nullOrBlankThrows() {
            assertThrows(OptionCodeFormatInvalidException.class, () -> policy.validateFormat(null));
            assertThrows(OptionCodeFormatInvalidException.class, () -> policy.validateFormat(" "));
        }

        @Test
        @DisplayName("行业缩写与数字值合法")
        void industryAbbreviationAndDigitsValid() {
            assertTrue(policy.isValidFormat("OC_PWR_DRIVE_TYPE_AWD"));
            assertTrue(policy.isValidFormat("OC_PWR_DRIVE_TYPE_FWD"));
            assertTrue(policy.isValidFormat("OC_COMF_HUD_PROJECTOR_HUD"));
            assertTrue(policy.isValidFormat("OC_EXT_BODY_COLOR_2026"));
        }

        @Test
        @DisplayName("64 字符边界：恰好 64 合法，超过 64 抛 812127")
        void lengthBoundary() {
            // 构造恰好 64 字符的合法 code：OC_EXT_BODY_COLOR_ + 补齐 A
            String stem = "OC_EXT_BODY_COLOR_";
            String ok = stem + "A".repeat(OptionCodeCodePolicy.CODE_MAX_LENGTH - stem.length());
            assertEquals(64, ok.length());
            assertTrue(policy.isValidFormat(ok));
            policy.validateFormat(ok);

            // 65 字符超限：正则仍匹配但 validateFormat 按长度拒绝
            String tooLong = stem + "A".repeat(OptionCodeCodePolicy.CODE_MAX_LENGTH - stem.length() + 1);
            assertTrue(tooLong.length() > 64);
            assertTrue(policy.isValidFormat(tooLong));
            assertThrows(OptionCodeFormatInvalidException.class, () -> policy.validateFormat(tooLong));
        }
    }

    @Nested
    @DisplayName("validateFamilyMatch 校验（812128）")
    class ValidateFamilyMatchTests {

        @Test
        @DisplayName("code 与所属族派生主干一致通过")
        void familyMatchPasses() {
            policy.validateFamilyMatch("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR");
            policy.validateFamilyMatch("OC_PWR_DRIVE_TYPE_AWD", "OF_PWR_DRIVE_TYPE");
        }

        @Test
        @DisplayName("格式合法但所属族主干不一致抛 812128")
        void familyMismatchThrows() {
            // 跨族：前缀/语义与所属族不同
            assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> policy.validateFamilyMatch("OC_EXT_WHEEL_BLACK", "OF_EXT_BODY_COLOR"));
            // 缺少 FAMILY_SEMANTIC 段（OC_PWR_AWD 不以 OC_PWR_DRIVE_TYPE_ 开头）
            assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> policy.validateFamilyMatch("OC_PWR_AWD", "OF_PWR_DRIVE_TYPE"));
            // 仅主干无 VALUE
            assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> policy.validateFamilyMatch("OC_EXT_BODY_COLOR", "OF_EXT_BODY_COLOR"));
        }

        @Test
        @DisplayName("legacy 族（无法派生主干）新建 code 抛 812128")
        void legacyFamilyMismatchThrows() {
            assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> policy.validateFamilyMatch("OC_EXT_BODY_COLOR_BLACK", "COLOR"));
        }
    }

    @Nested
    @DisplayName("validateCreate 组合校验")
    class ValidateCreateTests {

        @Test
        @DisplayName("合法 code 通过")
        void validCreatePasses() {
            policy.validateCreate("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR");
        }

        @Test
        @DisplayName("格式非法优先抛 812127")
        void invalidFormatFirst() {
            assertThrows(OptionCodeFormatInvalidException.class,
                    () -> policy.validateCreate("CLR_BLACK", "OF_EXT_BODY_COLOR"));
        }

        @Test
        @DisplayName("格式合法但族不一致抛 812128")
        void familyMismatchSecond() {
            assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> policy.validateCreate("OC_EXT_WHEEL_BLACK", "OF_EXT_BODY_COLOR"));
        }
    }

    @Nested
    @DisplayName("存量审计辅助判定")
    class AuditHelperTests {

        @Test
        @DisplayName("非法字符集识别")
        void invalidCharset() {
            assertTrue(policy.containsInvalidCharset("oc_ext_body_color_black"));
            assertTrue(policy.containsInvalidCharset("OC_EXT_BODY_COLOR-BLACK"));
            assertTrue(policy.containsInvalidCharset("OC_EXT_BODY_COLOR_BLACK "));
            assertTrue(policy.containsInvalidCharset("OC_EXT_BODY_COLOR_黑色"));
            assertFalse(policy.containsInvalidCharset("OC_EXT_BODY_COLOR_BLACK"));
        }

        @Test
        @DisplayName("连续下划线 / 首尾下划线识别")
        void underscoreIssues() {
            assertTrue(policy.hasConsecutiveOrEdgeUnderscores("OC_EXT__BODY_COLOR_BLACK"));
            assertTrue(policy.hasConsecutiveOrEdgeUnderscores("_OC_EXT_BODY_COLOR_BLACK"));
            assertTrue(policy.hasConsecutiveOrEdgeUnderscores("OC_EXT_BODY_COLOR_BLACK_"));
            assertFalse(policy.hasConsecutiveOrEdgeUnderscores("OC_EXT_BODY_COLOR_BLACK"));
        }

        @Test
        @DisplayName("仅前缀无 VALUE 识别")
        void prefixOnly() {
            assertTrue(policy.isPrefixOnly("OC_EXT"));
            assertTrue(policy.isPrefixOnly("OC_EXT_"));
            assertTrue(policy.isPrefixOnly("OC_PWR_"));
            assertFalse(policy.isPrefixOnly("OC_EXT_BODY_COLOR_BLACK"));
        }
    }
}
