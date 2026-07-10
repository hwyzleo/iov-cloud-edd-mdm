package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SwinManagedSystem实体测试
 *
 * @author hwyz_leo
 */
@DisplayName("SwinManagedSystem 实体测试")
class SwinManagedSystemTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 验证默认值")
        void create_success() {
            String swinCode = "SWIN001";
            String vehicleNodeCode = "VN001";
            String createBy = "testUser";

            SwinManagedSystem managedSystem = SwinManagedSystem.create(swinCode, vehicleNodeCode, createBy);

            assertNotNull(managedSystem);
            assertEquals(swinCode, managedSystem.getSwinCode());
            assertEquals(vehicleNodeCode, managedSystem.getVehicleNodeCode());
            assertEquals(createBy, managedSystem.getCreateBy());
            assertNotNull(managedSystem.getCreateTime());
            assertEquals(createBy, managedSystem.getModifyBy());
            assertNotNull(managedSystem.getModifyTime());
            assertEquals(0, managedSystem.getRowVersion());
            assertTrue(managedSystem.getRowValid());
            assertFalse(managedSystem.getIsTypeApprovalRelevant());
            assertNull(managedSystem.getApprovedSoftwareBaseline());
        }

        @Test
        @DisplayName("创建失败 - swinCode 为空")
        void create_blankSwinCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinManagedSystem.create("", "VN001", "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - swinCode 为 null")
        void create_nullSwinCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinManagedSystem.create(null, "VN001", "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - vehicleNodeCode 为空")
        void create_blankVehicleNodeCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinManagedSystem.create("SWIN001", "", "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - vehicleNodeCode 为 null")
        void create_nullVehicleNodeCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinManagedSystem.create("SWIN001", null, "testUser");
            });
        }
    }
}
