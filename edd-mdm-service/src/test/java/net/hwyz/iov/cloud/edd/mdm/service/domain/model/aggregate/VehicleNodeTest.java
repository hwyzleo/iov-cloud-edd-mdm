package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SecurityLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehicleNodeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 车载节点聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("VehicleNode 聚合根测试")
class VehicleNodeTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 所有字段正确填充")
        void create_success() {
            VehicleNode node = VehicleNode.create(
                    "TBOX", "远程信息处理器", "Telematics Box", "车载通讯终端",
                    NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, "T-BOX",
                    true, OtaSupportType.BOTH,
                    HsmCapability.HSM_LIGHT, SecurityLevel.CAL2,
                    null, null, "admin"
            );

            assertEquals("TBOX", node.getNodeCode());
            assertEquals("远程信息处理器", node.getName());
            assertEquals("Telematics Box", node.getNameLocal());
            assertEquals("车载通讯终端", node.getDescription());
            assertEquals(NodeType.TELEMATICS, node.getNodeType());
            assertEquals(FunctionalDomain.CONNECTIVITY, node.getFunctionalDomain());
            assertEquals("T-BOX", node.getDeviceCategory());
            assertTrue(node.getIsCoreNode());
            assertEquals(OtaSupportType.BOTH, node.getOtaSupportType());
            assertEquals(HsmCapability.HSM_LIGHT, node.getHsmCapability());
            assertEquals(SecurityLevel.CAL2, node.getSecurityLevel());
            assertEquals("MANUAL", node.getSource());
            assertNull(node.getExternalRefId());
            assertEquals(1, node.getVersion());
            assertEquals(VehicleNodeStatus.ACTIVE, node.getStatus());
            assertEquals("admin", node.getCreateBy());
            assertEquals("admin", node.getModifyBy());
            assertEquals(0, node.getRowVersion());
            assertTrue(node.getRowValid());
        }

        @Test
        @DisplayName("创建成功 - isCoreNode 为 null 时默认 false")
        void create_isCoreNodeNull_defaultsToFalse() {
            VehicleNode node = VehicleNode.create(
                    "ECU01", "电控单元", null, null,
                    NodeType.ECU, FunctionalDomain.POWERTRAIN, null,
                    null, OtaSupportType.FOTA,
                    null, null,
                    null, null, "admin"
            );

            assertFalse(node.getIsCoreNode());
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
                    VehicleNode.create(
                            "TBOX", "远程信息处理器", null, null,
                            NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, null,
                            true, OtaSupportType.BOTH,
                            null, null,
                            from, to, "admin"
                    )
            );
        }

        @Test
        @DisplayName("创建成功 - effectiveFrom 等于 effectiveTo")
        void create_effectiveFromEqualsTo_success() {
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.JUNE, 1);
            Date from = cal.getTime();
            Date to = cal.getTime();

            VehicleNode node = VehicleNode.create(
                    "TBOX", "远程信息处理器", null, null,
                    NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, null,
                    true, OtaSupportType.BOTH,
                    null, null,
                    from, to, "admin"
            );

            assertNotNull(node);
            assertEquals(from, node.getEffectiveFrom());
            assertEquals(to, node.getEffectiveTo());
        }

        @Test
        @DisplayName("创建成功 - effectiveFrom 和 effectiveTo 都为 null")
        void create_bothDatesNull_success() {
            VehicleNode node = VehicleNode.create(
                    "TBOX", "远程信息处理器", null, null,
                    NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, null,
                    true, OtaSupportType.BOTH,
                    null, null,
                    null, null, "admin"
            );

            assertNotNull(node);
            assertNull(node.getEffectiveFrom());
            assertNull(node.getEffectiveTo());
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            VehicleNode node = createTestNode();
            int originalVersion = node.getVersion();

            node.update("新名称", null, null,
                    NodeType.ECU, FunctionalDomain.POWERTRAIN, null,
                    false, OtaSupportType.FOTA,
                    null, null,
                    null, null, "modifier");

            assertEquals(originalVersion + 1, node.getVersion());
            assertEquals("新名称", node.getName());
            assertEquals(NodeType.ECU, node.getNodeType());
            assertEquals(FunctionalDomain.POWERTRAIN, node.getFunctionalDomain());
            assertEquals("modifier", node.getModifyBy());
        }

        @Test
        @DisplayName("更新成功 - nodeCode 不可变")
        void update_nodeCodeUnchanged() {
            VehicleNode node = createTestNode();
            String originalCode = node.getNodeCode();

            node.update("新名称", null, null,
                    NodeType.ECU, FunctionalDomain.POWERTRAIN, null,
                    false, OtaSupportType.FOTA,
                    null, null,
                    null, null, "modifier");

            assertEquals(originalCode, node.getNodeCode());
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            VehicleNode node = createTestNode();

            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    node.update("新名称", null, null,
                            NodeType.ECU, FunctionalDomain.POWERTRAIN, null,
                            false, OtaSupportType.FOTA,
                            null, null,
                            from, to, "modifier")
            );
        }

        @Test
        @DisplayName("更新成功 - isCoreNode 为 null 时保持原值")
        void update_isCoreNodeNull_keepsOriginal() {
            VehicleNode node = createTestNode();
            assertTrue(node.getIsCoreNode());

            node.update("新名称", null, null,
                    NodeType.ECU, FunctionalDomain.POWERTRAIN, null,
                    null, OtaSupportType.FOTA,
                    null, null,
                    null, null, "modifier");

            assertTrue(node.getIsCoreNode());
        }
    }

    @Nested
    @DisplayName("deactivate 失效方法")
    class DeactivateTests {

        @Test
        @DisplayName("失效成功 - ACTIVE 状态")
        void deactivate_activeStatus_success() {
            VehicleNode node = createTestNode();
            assertEquals(VehicleNodeStatus.ACTIVE, node.getStatus());

            node.deactivate("admin");

            assertEquals(VehicleNodeStatus.INACTIVE, node.getStatus());
            assertNotNull(node.getEffectiveTo());
            assertEquals("admin", node.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            VehicleNode node = createTestNode();
            node.deactivate("admin");

            assertThrows(IllegalStateException.class, () ->
                    node.deactivate("admin")
            );
        }

        @Test
        @DisplayName("失效失败 - DRAFT 状态")
        void deactivate_draftStatus_throwsException() {
            VehicleNode node = createTestNode();
            node.setStatus(VehicleNodeStatus.DRAFT);

            assertThrows(IllegalStateException.class, () ->
                    node.deactivate("admin")
            );
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            VehicleNode node = createTestNode();
            Date before = new Date();

            node.markAsDeleting("deleter");

            assertEquals("deleter", node.getModifyBy());
            assertNotNull(node.getModifyTime());
            assertTrue(node.getModifyTime().getTime() >= before.getTime());
        }
    }

    private VehicleNode createTestNode() {
        return VehicleNode.create(
                "TBOX", "远程信息处理器", "Telematics Box", "车载通讯终端",
                NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, "T-BOX",
                true, OtaSupportType.BOTH,
                HsmCapability.HSM_LIGHT, SecurityLevel.CAL2,
                null, null, "admin"
        );
    }
}
