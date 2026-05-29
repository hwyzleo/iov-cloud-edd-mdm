package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 物料品类聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategory 聚合根测试")
class MaterialCategoryTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 所有字段正确填充")
        void create_success() {
            MaterialCategory category = MaterialCategory.create(
                    "MC001", "原材料", "Raw Material", "原材料品类描述", null,
                    null, null, "admin"
            );

            assertEquals("MC001", category.getCode());
            assertEquals("原材料", category.getName());
            assertEquals("Raw Material", category.getNameLocal());
            assertEquals("原材料品类描述", category.getDescription());
            assertNull(category.getParentCode());
            assertEquals("LOCAL", category.getSourceSystem());
            assertEquals("MC001", category.getSourceId());
            assertEquals("LOCAL", category.getIngestionChannel());
            assertEquals(1, category.getVersion());
            assertEquals(MaterialCategoryStatus.ACTIVE, category.getStatus());
            assertEquals("admin", category.getCreateBy());
            assertEquals("admin", category.getModifyBy());
            assertEquals(0, category.getRowVersion());
            assertTrue(category.getRowValid());
        }

        @Test
        @DisplayName("创建成功 - 带父品类")
        void create_withParentCode_success() {
            MaterialCategory category = MaterialCategory.create(
                    "MC00101", "钢材", "Steel", "钢材品类", "MC001",
                    null, null, "admin"
            );

            assertEquals("MC00101", category.getCode());
            assertEquals("MC001", category.getParentCode());
        }

        @Test
        @DisplayName("创建失败 - effectiveFrom 晚于 effectiveTo")
        void create_effectiveFromAfterTo_throwsException() {
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    MaterialCategory.create(
                            "MC001", "原材料", null, null, null,
                            from, to, "admin"
                    )
            );
        }

        @Test
        @DisplayName("创建成功 - effectiveFrom 和 effectiveTo 都为 null")
        void create_bothDatesNull_success() {
            MaterialCategory category = MaterialCategory.create(
                    "MC001", "原材料", null, null, null,
                    null, null, "admin"
            );

            assertNotNull(category);
            assertNull(category.getEffectiveFrom());
            assertNull(category.getEffectiveTo());
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            MaterialCategory category = createTestCategory();
            int originalVersion = category.getVersion();

            category.update("新名称", "New Name", "新描述", "MC002",
                    null, null, "modifier");

            assertEquals(originalVersion + 1, category.getVersion());
            assertEquals("新名称", category.getName());
            assertEquals("New Name", category.getNameLocal());
            assertEquals("新描述", category.getDescription());
            assertEquals("MC002", category.getParentCode());
            assertEquals("modifier", category.getModifyBy());
        }

        @Test
        @DisplayName("更新成功 - code 不可变")
        void update_codeUnchanged() {
            MaterialCategory category = createTestCategory();
            String originalCode = category.getCode();

            category.update("新名称", null, null, null,
                    null, null, "modifier");

            assertEquals(originalCode, category.getCode());
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            MaterialCategory category = createTestCategory();

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
            MaterialCategory category = createTestCategory();
            assertEquals(MaterialCategoryStatus.ACTIVE, category.getStatus());

            category.deactivate("admin");

            assertEquals(MaterialCategoryStatus.INACTIVE, category.getStatus());
            assertNotNull(category.getEffectiveTo());
            assertEquals("admin", category.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            MaterialCategory category = createTestCategory();
            category.deactivate("admin");

            assertThrows(IllegalStateException.class, () ->
                    category.deactivate("admin")
            );
        }

        @Test
        @DisplayName("失效失败 - DRAFT 状态")
        void deactivate_draftStatus_throwsException() {
            MaterialCategory category = createTestCategory();
            category.setStatus(MaterialCategoryStatus.DRAFT);

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
            MaterialCategory category = createTestCategory();
            Date before = new Date();

            category.markAsDeleting("deleter");

            assertEquals("deleter", category.getModifyBy());
            assertNotNull(category.getModifyTime());
            assertTrue(category.getModifyTime().getTime() >= before.getTime());
        }
    }

    private MaterialCategory createTestCategory() {
        return MaterialCategory.create(
                "MC001", "原材料", "Raw Material", "原材料品类描述", null,
                null, null, "admin"
        );
    }
}
