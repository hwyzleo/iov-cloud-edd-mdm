package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备类别聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("DeviceCategory 聚合根测试")
class DeviceCategoryTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 验证默认值")
        void create_success() {
            DeviceCategory category = DeviceCategory.create(
                    "DC001", "摄像头", "Camera", "摄像头设备类别", 1,
                    null, null, "admin"
            );

            assertEquals("DC001", category.getCode());
            assertEquals("摄像头", category.getName());
            assertEquals("Camera", category.getNameLocal());
            assertEquals("摄像头设备类别", category.getDescription());
            assertEquals(1, category.getSortOrder());
            assertEquals("MANUAL", category.getSource());
            assertNull(category.getExternalRefId());
            assertNull(category.getExternalVersion());
            assertNotNull(category.getLastSyncTime());
            assertEquals(1, category.getVersion());
            assertEquals(DeviceCategoryStatus.ACTIVE, category.getStatus());
            assertEquals("admin", category.getCreateBy());
            assertEquals("admin", category.getModifyBy());
            assertEquals(0, category.getRowVersion());
            assertTrue(category.getRowValid());
        }

        @Test
        @DisplayName("创建成功 - sortOrder 为 null 时默认为 0")
        void create_nullSortOrder_defaultsToZero() {
            DeviceCategory category = DeviceCategory.create(
                    "DC001", "摄像头", null, null, null,
                    null, null, "admin"
            );

            assertEquals(0, category.getSortOrder());
        }

        @Test
        @DisplayName("创建失败 - effectiveFrom 晚于 effectiveTo")
        void create_invalidEffectiveDate_throwsException() {
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    DeviceCategory.create(
                            "DC001", "摄像头", null, null, null,
                            from, to, "admin"
                    )
            );
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            DeviceCategory category = createTestCategory();
            int originalVersion = category.getVersion();

            category.update("新名称", "New Name", "新描述", 2,
                    null, null, "modifier");

            assertEquals(originalVersion + 1, category.getVersion());
            assertEquals("新名称", category.getName());
            assertEquals("New Name", category.getNameLocal());
            assertEquals("新描述", category.getDescription());
            assertEquals(2, category.getSortOrder());
            assertEquals("modifier", category.getModifyBy());
        }

        @Test
        @DisplayName("更新成功 - sortOrder 为 null 时保持原值")
        void update_nullSortOrder_keepsOriginal() {
            DeviceCategory category = createTestCategory();
            int originalSortOrder = category.getSortOrder();

            category.update("新名称", null, null, null,
                    null, null, "modifier");

            assertEquals(originalSortOrder, category.getSortOrder());
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            DeviceCategory category = createTestCategory();

            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    category.update("新名称", null, null, null,
                            from, to, "modifier")
            );
        }
    }

    @Nested
    @DisplayName("deactivate 失效方法")
    class DeactivateTests {

        @Test
        @DisplayName("失效成功 - ACTIVE 状态")
        void deactivate_activeStatus_success() {
            DeviceCategory category = createTestCategory();
            assertEquals(DeviceCategoryStatus.ACTIVE, category.getStatus());

            category.deactivate("admin");

            assertEquals(DeviceCategoryStatus.INACTIVE, category.getStatus());
            assertNotNull(category.getEffectiveTo());
            assertEquals("admin", category.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            DeviceCategory category = createTestCategory();
            category.deactivate("admin");

            assertThrows(IllegalStateException.class, () ->
                    category.deactivate("admin")
            );
        }

        @Test
        @DisplayName("失效失败 - DRAFT 状态")
        void deactivate_draftStatus_throwsException() {
            DeviceCategory category = createTestCategory();
            category.setStatus(DeviceCategoryStatus.DRAFT);

            assertThrows(IllegalStateException.class, () ->
                    category.deactivate("admin")
            );
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            DeviceCategory category = createTestCategory();
            Date before = new Date();

            category.markAsDeleting("deleter");

            assertEquals("deleter", category.getModifyBy());
            assertNotNull(category.getModifyTime());
            assertTrue(category.getModifyTime().getTime() >= before.getTime());
        }
    }

    private DeviceCategory createTestCategory() {
        return DeviceCategory.create(
                "DC001", "摄像头", "Camera", "摄像头设备类别", 1,
                null, null, "admin"
        );
    }
}
