package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryCodeFormatInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类编码策略单元测试（CR-039 §5.1）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryCodePolicy 测试")
class MaterialCategoryCodePolicyTest {

    private MaterialCategoryCodePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new MaterialCategoryCodePolicy(new MaterialCategoryAbbreviationRegistry());
    }

    @Nested
    @DisplayName("标准/扩展 code 正例")
    class StandardCodeTests {

        @Test
        @DisplayName("L1/L2/L3 标准 code 全部合法")
        void standardCodesValid() {
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP", null));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_RAW", null));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_SW", null));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_IND", null));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_BODY", "MC_CMP"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_EGY_HV_BATTERY", "MC_CMP_EGY"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_CHS_BRAKE", "MC_CMP_CHS"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_ADAS_LIDAR", "MC_CMP_ADAS"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_SW_EMB_BASIC", "MC_SW_EMB"));
        }

        @Test
        @DisplayName("多段受控 Family（WHEEL_TIRE / TRACTION_BATTERY / POWER_CONVERSION）合法")
        void multiTokenFamilyValid() {
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_CHS_WHEEL_TIRE", "MC_CMP_CHS"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_EGY_TRACTION_BATTERY", "MC_CMP_EGY"));
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_EGY_POWER_CONVERSION", "MC_CMP_EGY"));
        }

        @Test
        @DisplayName("企业扩展 L3 code 合法（_X_，父级为标准 L2）")
        void extensionCodeValid() {
            assertDoesNotThrow(() -> policy.validateCodeFormat("MC_CMP_BODY_X_EXTRA", "MC_CMP_BODY"));
            assertTrue(policy.isExtensionCode("MC_CMP_BODY_X_EXTRA"));
            assertFalse(policy.isExtensionCode("MC_CMP_BODY_BIW"));
        }
    }

    @Nested
    @DisplayName("格式非法反例（812924）")
    class FormatInvalidTests {

        @Test
        @DisplayName("非 MC_ 前缀/小写/非 ASCII 被拒绝")
        void badPrefixRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("CMP", null));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("mc_cmp", null));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_分类", null));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("", null));
        }

        @Test
        @DisplayName("未知 Scope 被拒绝")
        void unknownScopeRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_XXX", null));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_XXX", "MC_CMP"));
        }

        @Test
        @DisplayName("跨 Scope 的 Domain 被拒绝")
        void crossScopeDomainRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_RAW_BODY", "MC_RAW"));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_SW_EMB", "MC_CMP"));
        }

        @Test
        @DisplayName("父子前缀不一致被拒绝")
        void parentPrefixMismatchRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_EXT_LIGHTING", "MC_CMP_BODY"));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY", "MC_RAW"));
        }

        @Test
        @DisplayName("超 32 字符被拒绝")
        void overLengthRejected() {
            String longFamily = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // 26 字符，超 Family 上限，总长 > 32
            String longCode = "MC_CMP_EGY_" + longFamily;
            assertTrue(longCode.length() > 32);
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat(longCode, "MC_CMP_EGY"));
        }

        @Test
        @DisplayName("L3 Family 词超长/非法被拒绝")
        void badFamilyRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY_BODYINWHITESTRUCTURE", "MC_CMP_BODY"));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY_biw", "MC_CMP_BODY"));
        }

        @Test
        @DisplayName("L1 携带多余 token 被拒绝")
        void l1ExtraTokensRejected() {
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY", null));
        }

        @Test
        @DisplayName("扩展仅限 _X_ 位置，非法扩展/超长 family 被拒绝")
        void malformedExtensionRejected() {
            // 悬空 _X_（family 过短）被拒绝
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY_X", "MC_CMP_BODY"));
            // 标准 L3 family 过长（>16）被拒绝
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY_TOKEN1_TOKEN2_TOKEN3", "MC_CMP_BODY"));
            // 扩展 family 过长被拒绝
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("MC_CMP_BODY_X_TOKEN1_TOKEN2_TOKEN3", "MC_CMP_BODY"));
        }
    }
}
