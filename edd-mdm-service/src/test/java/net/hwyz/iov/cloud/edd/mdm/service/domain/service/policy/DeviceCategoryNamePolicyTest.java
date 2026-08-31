package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备类别名称策略单元测试（CR-037 §3.2）
 *
 * @author hwyz_leo
 */
@DisplayName("DeviceCategoryNamePolicy 测试")
class DeviceCategoryNamePolicyTest {

    private DeviceCategoryNamePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new DeviceCategoryNamePolicy();
    }

    @Nested
    @DisplayName("normalize 标准化")
    class NormalizeTests {

        @Test
        @DisplayName("trim 首尾空白")
        void trimsWhitespace() {
            assertEquals("telematics control unit", policy.normalize("  Telematics Control Unit  "));
        }

        @Test
        @DisplayName("连续空白折叠为一个空格")
        void collapsesWhitespace() {
            assertEquals("telematics control unit", policy.normalize("Telematics   Control\tUnit"));
        }

        @Test
        @DisplayName("英文 Unicode case-fold（全角/不间断空格归一）")
        void caseFoldAndFullWidthSpace() {
            assertEquals("telematics box", policy.normalize("Telematics\u3000Box"));
            assertEquals("telematics box", policy.normalize("Telematics\u00A0Box"));
        }

        @Test
        @DisplayName("中文名称移除全半角空格差异")
        void chineseWhitespaceRemoved() {
            assertEquals("车载通信终端", policy.normalize("车载 通信终端"));
            assertEquals("车载通信终端", policy.normalize("车载\u3000通信终端"));
        }

        @Test
        @DisplayName("null 返回 null")
        void nullReturnsNull() {
            assertNull(policy.normalize(null));
        }
    }

    @Nested
    @DisplayName("findDuplicate 防重")
    class FindDuplicateTests {

        @Test
        @DisplayName("英文名标准化后重复被命中")
        void englishNameDuplicateFound() {
            DeviceCategory existing = category("TBOX_LEGACY", "Telematics   Control Unit", "旧名称");
            Optional<DeviceCategory> dup = policy.findDuplicate(
                    Collections.singletonList(existing),
                    "Telematics Control Unit", "车载通信终端");
            assertTrue(dup.isPresent());
            assertEquals("TBOX_LEGACY", dup.get().getCode());
        }

        @Test
        @DisplayName("中文名全半角空白差异命中")
        void chineseNameDuplicateFound() {
            DeviceCategory existing = category("CAM_LEGACY", "Camera", "车载\u3000摄像头");
            Optional<DeviceCategory> dup = policy.findDuplicate(
                    Collections.singletonList(existing),
                    "Camera", "车载 摄像头");
            assertTrue(dup.isPresent());
            assertEquals("CAM_LEGACY", dup.get().getCode());
        }

        @Test
        @DisplayName("无重复返回空")
        void noDuplicateReturnsEmpty() {
            DeviceCategory existing = category("TBOX", "Telematics Control Unit", "车载通信终端");
            Optional<DeviceCategory> dup = policy.findDuplicate(
                    Collections.singletonList(existing),
                    "Camera", "摄像头");
            assertTrue(dup.isEmpty());
        }

        @Test
        @DisplayName("空列表返回空")
        void emptyListReturnsEmpty() {
            assertTrue(policy.findDuplicate(List.of(), "Camera", "摄像头").isEmpty());
        }
    }

    private DeviceCategory category(String code, String name, String nameLocal) {
        return DeviceCategory.create(code, name, nameLocal, null, 0, null, null, "admin");
    }
}
