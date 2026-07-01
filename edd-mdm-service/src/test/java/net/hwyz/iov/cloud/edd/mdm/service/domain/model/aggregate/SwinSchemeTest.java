package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SwinScheme聚合根测试
 *
 * @author hwyz_leo
 */
@DisplayName("SwinScheme 聚合根测试")
class SwinSchemeTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 验证默认值")
        void create_success() {
            String code = "TEST_SCHEME";
            String name = "Test Scheme";
            String nameLocal = "测试方案";
            String description = "Test Description";
            SwinRoute route = SwinRoute.SINGLE_SWIN;
            Integer sortOrder = 1;
            Date effectiveFrom = new Date();
            Date effectiveTo = new Date(System.currentTimeMillis() + 86400000L);
            String createBy = "testUser";

            SwinScheme swinScheme = SwinScheme.create(code, name, nameLocal, description, route, sortOrder, effectiveFrom, effectiveTo, createBy);

            assertNotNull(swinScheme);
            assertEquals(code, swinScheme.getCode());
            assertEquals(name, swinScheme.getName());
            assertEquals(nameLocal, swinScheme.getNameLocal());
            assertEquals(description, swinScheme.getDescription());
            assertEquals(route, swinScheme.getRoute());
            assertEquals(sortOrder, swinScheme.getSortOrder());
            assertEquals("MANUAL", swinScheme.getSource());
            assertNull(swinScheme.getExternalRefId());
            assertNull(swinScheme.getExternalVersion());
            assertNotNull(swinScheme.getLastSyncTime());
            assertEquals(1, swinScheme.getVersion());
            assertEquals(effectiveFrom, swinScheme.getEffectiveFrom());
            assertEquals(effectiveTo, swinScheme.getEffectiveTo());
            assertEquals(SwinSchemeStatus.ACTIVE, swinScheme.getStatus());
            assertEquals(createBy, swinScheme.getCreateBy());
            assertNotNull(swinScheme.getCreateTime());
            assertEquals(createBy, swinScheme.getModifyBy());
            assertNotNull(swinScheme.getModifyTime());
            assertEquals(0, swinScheme.getRowVersion());
            assertTrue(swinScheme.getRowValid());
        }

        @Test
        @DisplayName("创建成功 - sortOrder 为 null 时默认为 0")
        void create_nullSortOrder_defaultsToZero() {
            SwinScheme swinScheme = SwinScheme.create("TEST", "Test", null, null, SwinRoute.SINGLE_SWIN, null, null, null, "testUser");

            assertEquals(0, swinScheme.getSortOrder());
        }

        @Test
        @DisplayName("创建失败 - route 为 null")
        void create_nullRoute_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinScheme.create("TEST", "Test", null, null, null, 0, null, null, "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - effectiveFrom 晚于 effectiveTo")
        void create_invalidEffectiveDate_throwsException() {
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () -> {
                SwinScheme.create("TEST", "Test", null, null, SwinRoute.SINGLE_SWIN, 0, from, to, "testUser");
            });
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            SwinScheme swinScheme = createTestScheme();
            int originalVersion = swinScheme.getVersion();

            String newName = "Updated Name";
            String newNameLocal = "更新名称";
            String newDescription = "Updated Description";
            SwinRoute newRoute = SwinRoute.MULTI_SWIN;
            Integer newSortOrder = 2;
            Date newEffectiveFrom = new Date();
            Date newEffectiveTo = new Date(System.currentTimeMillis() + 86400000L);
            String modifyBy = "modifier";

            swinScheme.update(newName, newNameLocal, newDescription, newRoute, newSortOrder, newEffectiveFrom, newEffectiveTo, modifyBy);

            assertEquals(newName, swinScheme.getName());
            assertEquals(newNameLocal, swinScheme.getNameLocal());
            assertEquals(newDescription, swinScheme.getDescription());
            assertEquals(newRoute, swinScheme.getRoute());
            assertEquals(newSortOrder, swinScheme.getSortOrder());
            assertEquals(newEffectiveFrom, swinScheme.getEffectiveFrom());
            assertEquals(newEffectiveTo, swinScheme.getEffectiveTo());
            assertEquals(originalVersion + 1, swinScheme.getVersion());
            assertEquals(modifyBy, swinScheme.getModifyBy());
        }

        @Test
        @DisplayName("更新失败 - route 为 null")
        void update_nullRoute_throwsException() {
            SwinScheme swinScheme = createTestScheme();

            assertThrows(IllegalArgumentException.class, () -> {
                swinScheme.update("Name", null, null, null, 0, null, null, "modifier");
            });
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            SwinScheme swinScheme = createTestScheme();

            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () -> {
                swinScheme.update("Name", null, null, SwinRoute.SINGLE_SWIN, 0, from, to, "modifier");
            });
        }
    }

    @Nested
    @DisplayName("deactivate 失效方法")
    class DeactivateTests {

        @Test
        @DisplayName("失效成功 - ACTIVE 状态")
        void deactivate_activeStatus_success() {
            SwinScheme swinScheme = createTestScheme();
            assertEquals(SwinSchemeStatus.ACTIVE, swinScheme.getStatus());
            String modifyBy = "modifier";

            swinScheme.deactivate(modifyBy);

            assertEquals(SwinSchemeStatus.INACTIVE, swinScheme.getStatus());
            assertNotNull(swinScheme.getEffectiveTo());
            assertEquals(2, swinScheme.getVersion());
            assertEquals(modifyBy, swinScheme.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            SwinScheme swinScheme = createTestScheme();
            swinScheme.deactivate("modifier");

            assertThrows(IllegalStateException.class, () -> {
                swinScheme.deactivate("modifier");
            });
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            SwinScheme swinScheme = createTestScheme();
            Date before = new Date();

            swinScheme.markAsDeleting("deleter");

            assertEquals("deleter", swinScheme.getModifyBy());
            assertNotNull(swinScheme.getModifyTime());
            assertTrue(swinScheme.getModifyTime().getTime() >= before.getTime());
        }
    }

    private SwinScheme createTestScheme() {
        return SwinScheme.create("TEST", "Test", null, null, SwinRoute.SINGLE_SWIN, 0, null, null, "testUser");
    }
}
