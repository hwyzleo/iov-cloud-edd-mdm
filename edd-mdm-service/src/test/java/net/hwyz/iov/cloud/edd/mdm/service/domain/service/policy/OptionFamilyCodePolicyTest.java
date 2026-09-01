package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCategoryPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
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
 * 选项族编码策略单元测试（CR-035 §3.1/§3.2）
 *
 * @author hwyz_leo
 */
@DisplayName("OptionFamilyCodePolicy 测试")
class OptionFamilyCodePolicyTest {

    private OptionFamilyCodePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new OptionFamilyCodePolicy();
    }

    @Nested
    @DisplayName("八类前缀与 category 正反向映射")
    class PrefixCategoryMappingTests {

        @ParameterizedTest(name = "{0} 前缀 -> {1}")
        @CsvSource({
                "EXT, EXTERIOR",
                "INT, INTERIOR",
                "PWR, POWERTRAIN",
                "CHS, CHASSIS",
                "SMART, INTELLIGENT",
                "COMF, COMFORT",
                "SAFE, SAFETY",
                "ACC, ACCESSORY",
                "OTH, OTHER"
        })
        void prefixCategoryMapping(String prefix, OptionFamilyCategory category) {
            assertEquals(category, policy.categoryForCode("OF_" + prefix + "_SAMPLE"));
        }
    }

    @Nested
    @DisplayName("标准 code 校验")
    class StandardCodeTests {

        @ParameterizedTest(name = "{0} 应合法")
        @CsvSource({
                "OF_EXT_BODY_COLOR",
                "OF_EXT_TWO_TONE_BODY",
                "OF_INT_SEAT_UPHOLSTERY",
                "OF_PWR_BATTERY_CAPACITY",
                "OF_CHS_SUSPENSION",
                "OF_SMART_ADAS_SENSOR",
                "OF_COMF_CLIMATE_CONTROL",
                "OF_SAFE_AIRBAG",
                "OF_ACC_CHARGING_EQUIPMENT",
                "OF_OTH_STEERING_POSITION"
        })
        void standardFormatValid(String code) {
            assertTrue(policy.isStandardFormat(code));
            assertTrue(policy.isValidFormat(code));
        }

        @ParameterizedTest(name = "{0} 应非法")
        @CsvSource({
                "OF_EXT",                                   // 缺少语义段
                "OF_EXT_",                                  // 尾随下划线
                "OF__EXT_BODY",                             // 连续下划线
                "OF_EXT__BODY",                             // 连续下划线
                "_OF_EXT_BODY",                             // 首部非 OF_
                "OF_EXT_BODY_",                             // 尾随下划线
                "OF_EXT_BODY颜色",                          // 非 ASCII
                "OF_EXT_BODY-COLOR",                        // 非法字符
                "of_ext_body_color",                        // 小写
                "OF_ext_BODY_COLOR"                         // 混合大小写
        })
        void standardFormatInvalid(String code) {
            assertFalse(policy.isStandardFormat(code));
        }
    }

    @Nested
    @DisplayName("扩展 code 校验（X 命名空间）")
    class ExtensionCodeTests {

        @Test
        @DisplayName("合法扩展 code")
        void extensionFormatValid() {
            assertTrue(policy.isExtensionFormat("OF_EXT_X_OFFROAD_APPEARANCE"));
            assertTrue(policy.isExtensionFormat("OF_SMART_X_SENSOR_CLEANING"));
            assertTrue(policy.isValidFormat("OF_PWR_X_TRACK_PERFORMANCE"));
        }

        @Test
        @DisplayName("扩展 code 前缀与 category 一致")
        void extensionPrefixMapping() {
            assertEquals(OptionFamilyCategory.EXTERIOR, policy.categoryForCode("OF_EXT_X_OFFROAD_APPEARANCE"));
            assertEquals(OptionFamilyCategory.INTELLIGENT, policy.categoryForCode("OF_SMART_X_SENSOR_CLEANING"));
        }

        @ParameterizedTest(name = "{0} 应非法扩展格式")
        @CsvSource({
                "OF_EXT_X",                    // 缺语义段
                "OF_EXT_X_",                   // 尾随下划线
                "OF_EXT_XX_SOMETHING",         // 非单 X 命名空间（按标准格式语义段处理）
                "OF_EXT_X_BODY颜色"            // 非 ASCII
        })
        void extensionFormatInvalid(String code) {
            assertFalse(policy.isExtensionFormat(code));
        }
    }

    @Nested
    @DisplayName("validateCodeFormat 校验（812124）")
    class ValidateCodeFormatTests {

        @Test
        @DisplayName("合法 code 不抛异常")
        void validCodePasses() {
            policy.validateCodeFormat("OF_EXT_BODY_COLOR");
            policy.validateCodeFormat("OF_EXT_X_OFFROAD_APPEARANCE");
        }

        @Test
        @DisplayName("null / 空串抛格式异常")
        void nullCodeThrows() {
            assertThrows(OptionFamilyCodeFormatInvalidException.class, () -> policy.validateCodeFormat(null));
            assertThrows(OptionFamilyCodeFormatInvalidException.class, () -> policy.validateCodeFormat(" "));
        }

        @Test
        @DisplayName("小写 code 抛格式异常")
        void lowerCaseThrows() {
            assertThrows(OptionFamilyCodeFormatInvalidException.class, () -> policy.validateCodeFormat("of_ext_body_color"));
        }

        @Test
        @DisplayName("超长 code 抛格式异常（字段上限 64）")
        void tooLongThrows() {
            String code = "OF_EXT_" + "A".repeat(60);
            assertTrue(code.length() > 64);
            assertThrows(OptionFamilyCodeFormatInvalidException.class, () -> policy.validateCodeFormat(code));
        }
    }

    @Nested
    @DisplayName("validateCategoryConsistency 校验（812125）")
    class ValidateCategoryConsistencyTests {

        @Test
        @DisplayName("前缀与 category 一致通过")
        void consistentPasses() {
            policy.validateCategoryConsistency("OF_EXT_BODY_COLOR", OptionFamilyCategory.EXTERIOR);
            policy.validateCategoryConsistency("OF_PWR_BATTERY_CAPACITY", OptionFamilyCategory.POWERTRAIN);
        }

        @Test
        @DisplayName("前缀与 category 不一致抛异常")
        void mismatchThrows() {
            assertThrows(OptionFamilyCategoryPrefixMismatchException.class,
                    () -> policy.validateCategoryConsistency("OF_EXT_BODY_COLOR", OptionFamilyCategory.INTERIOR));
        }

        @Test
        @DisplayName("category 为 null 抛异常")
        void nullCategoryThrows() {
            assertThrows(OptionFamilyCategoryPrefixMismatchException.class,
                    () -> policy.validateCategoryConsistency("OF_EXT_BODY_COLOR", null));
        }
    }

    @Test
    @DisplayName("非法 code 提取前缀返回 null")
    void extractPrefixOnInvalid() {
        assertNull(policy.categoryForCode("INVALID"));
        assertNull(policy.categoryForCode(null));
    }
}
