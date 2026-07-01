package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SwinDefinition聚合根测试
 *
 * @author hwyz_leo
 */
@DisplayName("SwinDefinition 聚合根测试")
class SwinDefinitionTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 验证默认值")
        void create_success() {
            String swinCode = "SWIN001";
            String schemeCode = "SCHEME001";
            String typeRefType = "VARIANT";
            String typeRefCode = "VAR001";
            String name = "Test Definition";
            String nameLocal = "测试定义";
            String description = "Test Description";
            String createBy = "testUser";

            SwinDefinition swinDefinition = SwinDefinition.create(swinCode, schemeCode, typeRefType, typeRefCode, name, nameLocal, description, createBy);

            assertNotNull(swinDefinition);
            assertEquals(swinCode, swinDefinition.getSwinCode());
            assertEquals(schemeCode, swinDefinition.getSchemeCode());
            assertEquals(typeRefType, swinDefinition.getTypeRefType());
            assertEquals(typeRefCode, swinDefinition.getTypeRefCode());
            assertEquals(name, swinDefinition.getName());
            assertEquals(nameLocal, swinDefinition.getNameLocal());
            assertEquals(description, swinDefinition.getDescription());
            assertEquals(1, swinDefinition.getVersion());
            assertEquals(SwinDefinitionStatus.ACTIVE, swinDefinition.getStatus());
            assertEquals(createBy, swinDefinition.getCreateBy());
            assertNotNull(swinDefinition.getCreateTime());
            assertEquals(createBy, swinDefinition.getModifyBy());
            assertNotNull(swinDefinition.getModifyTime());
            assertEquals(0, swinDefinition.getRowVersion());
            assertTrue(swinDefinition.getRowValid());
            assertNotNull(swinDefinition.getManagedSystems());
            assertTrue(swinDefinition.getManagedSystems().isEmpty());
        }

        @Test
        @DisplayName("创建成功 - MODEL 类型")
        void create_modelType_success() {
            SwinDefinition swinDefinition = SwinDefinition.create("SWIN001", "SCHEME001", "MODEL", "MDL001", "Test", null, null, "testUser");

            assertEquals("MODEL", swinDefinition.getTypeRefType());
        }

        @Test
        @DisplayName("创建失败 - swinCode 为空")
        void create_blankSwinCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinDefinition.create("", "SCHEME001", "VARIANT", "VAR001", "Test", null, null, "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - schemeCode 为空")
        void create_blankSchemeCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinDefinition.create("SWIN001", "", "VARIANT", "VAR001", "Test", null, null, "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - typeRefType 为空")
        void create_blankTypeRefType_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinDefinition.create("SWIN001", "SCHEME001", "", "VAR001", "Test", null, null, "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - typeRefType 无效")
        void create_invalidTypeRefType_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinDefinition.create("SWIN001", "SCHEME001", "INVALID", "VAR001", "Test", null, null, "testUser");
            });
        }

        @Test
        @DisplayName("创建失败 - typeRefCode 为空")
        void create_blankTypeRefCode_throwsException() {
            assertThrows(IllegalArgumentException.class, () -> {
                SwinDefinition.create("SWIN001", "SCHEME001", "VARIANT", "", "Test", null, null, "testUser");
            });
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            SwinDefinition swinDefinition = createTestDefinition();
            int originalVersion = swinDefinition.getVersion();

            String newName = "Updated Name";
            String newNameLocal = "更新名称";
            String newDescription = "Updated Description";
            String modifyBy = "modifier";

            swinDefinition.update(newName, newNameLocal, newDescription, modifyBy);

            assertEquals(newName, swinDefinition.getName());
            assertEquals(newNameLocal, swinDefinition.getNameLocal());
            assertEquals(newDescription, swinDefinition.getDescription());
            assertEquals(originalVersion + 1, swinDefinition.getVersion());
            assertEquals(modifyBy, swinDefinition.getModifyBy());
        }
    }

    @Nested
    @DisplayName("deactivate 失效方法")
    class DeactivateTests {

        @Test
        @DisplayName("失效成功 - ACTIVE 状态")
        void deactivate_activeStatus_success() {
            SwinDefinition swinDefinition = createTestDefinition();
            assertEquals(SwinDefinitionStatus.ACTIVE, swinDefinition.getStatus());
            String modifyBy = "modifier";

            swinDefinition.deactivate(modifyBy);

            assertEquals(SwinDefinitionStatus.INACTIVE, swinDefinition.getStatus());
            assertEquals(2, swinDefinition.getVersion());
            assertEquals(modifyBy, swinDefinition.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            SwinDefinition swinDefinition = createTestDefinition();
            swinDefinition.deactivate("modifier");

            assertThrows(IllegalStateException.class, () -> {
                swinDefinition.deactivate("modifier");
            });
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            SwinDefinition swinDefinition = createTestDefinition();
            Date before = new Date();

            swinDefinition.markAsDeleting("deleter");

            assertEquals("deleter", swinDefinition.getModifyBy());
            assertNotNull(swinDefinition.getModifyTime());
            assertTrue(swinDefinition.getModifyTime().getTime() >= before.getTime());
        }
    }

    @Nested
    @DisplayName("managedSystems 管理软件系统方法")
    class ManagedSystemsTests {

        @Test
        @DisplayName("添加管理软件系统成功")
        void addManagedSystem_success() {
            SwinDefinition swinDefinition = createTestDefinition();
            int originalVersion = swinDefinition.getVersion();

            SwinManagedSystem managedSystem = SwinManagedSystem.create("SWIN001", "VN001", "testUser");
            swinDefinition.addManagedSystem(managedSystem);

            assertEquals(1, swinDefinition.getManagedSystems().size());
            assertEquals(managedSystem, swinDefinition.getManagedSystems().get(0));
            assertEquals(originalVersion + 1, swinDefinition.getVersion());
        }

        @Test
        @DisplayName("移除管理软件系统成功")
        void removeManagedSystem_success() {
            SwinDefinition swinDefinition = createTestDefinition();
            SwinManagedSystem managedSystem = SwinManagedSystem.create("SWIN001", "VN001", "testUser");
            swinDefinition.addManagedSystem(managedSystem);
            int versionAfterAdd = swinDefinition.getVersion();

            swinDefinition.removeManagedSystem("VN001");

            assertTrue(swinDefinition.getManagedSystems().isEmpty());
            assertEquals(versionAfterAdd + 1, swinDefinition.getVersion());
        }
    }

    private SwinDefinition createTestDefinition() {
        return SwinDefinition.create("SWIN001", "SCHEME001", "VARIANT", "VAR001", "Test", null, null, "testUser");
    }
}
