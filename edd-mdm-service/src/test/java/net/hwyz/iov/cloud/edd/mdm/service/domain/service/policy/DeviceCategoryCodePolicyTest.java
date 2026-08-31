package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备类别编码策略单元测试（CR-037 §3.1）
 *
 * @author hwyz_leo
 */
@DisplayName("DeviceCategoryCodePolicy 测试")
class DeviceCategoryCodePolicyTest {

    private DeviceCategoryCodePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DeviceCategoryCodePolicy();
    }

    @Nested
    @DisplayName("标准目录设备族 code 正例")
    class StandardCodeTests {

        @Test
        @DisplayName("24 个标准设备族 code 全部合法")
        void allStandardFamiliesValid() {
            String[] codes = {
                    "TBOX", "CCU", "DCU", "ZCU", "CGW", "BCM", "VCU", "BMS", "MCU",
                    "BRAKE_ECU", "EPS", "AIRBAG_ECU", "IHU", "CLUSTER", "HUD",
                    "CAM", "RADAR", "LIDAR", "USS", "OBC", "DCDC", "EVCC",
                    "ETH_SW", "V2X_OBU"
            };
            for (String code : codes) {
                assertTrue(policy.isValidDeviceFamilyCode(code), "应合法: " + code);
            }
        }

        @Test
        @DisplayName("短组合消歧 code 合法（BRAKE_ECU / AIRBAG_ECU）")
        void disambiguatedCodesValid() {
            assertTrue(policy.isValidDeviceFamilyCode("BRAKE_ECU"));
            assertTrue(policy.isValidDeviceFamilyCode("AIRBAG_ECU"));
        }
    }

    @Nested
    @DisplayName("格式非法反例")
    class FormatInvalidTests {

        @Test
        @DisplayName("小写/混合大小写被拒绝")
        void lowercaseRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("tbox"));
            assertFalse(policy.isValidDeviceFamilyCode("TBox"));
            assertFalse(policy.isValidDeviceFamilyCode("tBox_4G"));
        }

        @Test
        @DisplayName("首尾下划线/连续下划线被拒绝")
        void underscoreEdgeCasesRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("_TBOX"));
            assertFalse(policy.isValidDeviceFamilyCode("TBOX_"));
            assertFalse(policy.isValidDeviceFamilyCode("TBOX__4G"));
        }

        @Test
        @DisplayName("非 ASCII 字符被拒绝")
        void nonAsciiRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("TBOX车"));
            assertFalse(policy.isValidDeviceFamilyCode("TBOX_中文"));
        }

        @Test
        @DisplayName("空/空白被拒绝")
        void blankRejected() {
            assertFalse(policy.isValidDeviceFamilyCode(null));
            assertFalse(policy.isValidDeviceFamilyCode(""));
            assertFalse(policy.isValidDeviceFamilyCode("  "));
        }

        @Test
        @DisplayName("超过 16 字符被拒绝")
        void overLengthRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("ABCDEFGHIJKLMNOPQ"));
        }
    }

    @Nested
    @DisplayName("节点规格语义反例")
    class NodeSpecTests {

        @Test
        @DisplayName("通信制式（4G/5G）被拒绝")
        void cellularGenerationRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("TBOX_4G"));
            assertFalse(policy.isValidDeviceFamilyCode("TBOX_5G"));
        }

        @Test
        @DisplayName("安装方位（FRONT/REAR/LEFT/CORNER）被拒绝")
        void positionRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("CAM_FRONT"));
            assertFalse(policy.isValidDeviceFamilyCode("CAM_REAR"));
            assertFalse(policy.isValidDeviceFamilyCode("USS_LEFT"));
            assertFalse(policy.isValidDeviceFamilyCode("RADAR_CORNER_FL"));
        }

        @Test
        @DisplayName("功率/分辨率/线数/电压（11KW/8M/128/800V）被拒绝")
        void numericSpecRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("OBC_11KW"));
            assertFalse(policy.isValidDeviceFamilyCode("CAM_8M"));
            assertFalse(policy.isValidDeviceFamilyCode("LIDAR_128"));
            assertFalse(policy.isValidDeviceFamilyCode("DCDC_800V"));
        }

        @Test
        @DisplayName("硬件代次（GEN1）被拒绝")
        void generationRejected() {
            assertFalse(policy.isValidDeviceFamilyCode("DCU_GEN1"));
            assertFalse(policy.isValidDeviceFamilyCode("CCU_GEN2"));
        }
    }

    @Nested
    @DisplayName("validateCodeFormat")
    class ValidateCodeFormatTests {

        @Test
        @DisplayName("非法 code 抛出异常")
        void invalidThrows() {
            assertThrows(net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryCodeFormatInvalidException.class,
                    () -> policy.validateCodeFormat("TBOX_4G"));
        }

        @Test
        @DisplayName("合法 code 不抛异常")
        void validPasses() {
            policy.validateCodeFormat("TBOX");
            policy.validateCodeFormat("DCU");
        }
    }
}
