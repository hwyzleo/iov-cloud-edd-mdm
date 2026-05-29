package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 零件聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("Part 聚合根测试")
class PartTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 所有字段正确填充")
        void create_success() {
            Part part = Part.create(
                    "P001", "前保险杠总成", "Front Bumper Assembly", "前保险杠总成描述",
                    "MC001", PartType.ASSEMBLY, "NODE001", "SUP001",
                    false, false, true,
                    KeyPartLevel.KEY, true, false,
                    true, "FFA001", "FFA描述",
                    true, "M001", "PROD001",
                    new Date(), "张三", "设计部",
                    "EA", "DWG001", "V1.0",
                    new BigDecimal("5.5"), "KG",
                    LifecycleStage.MASS_PRODUCTION, null,
                    null, null, "admin"
            );

            assertEquals("P001", part.getCode());
            assertEquals("前保险杠总成", part.getName());
            assertEquals("Front Bumper Assembly", part.getNameLocal());
            assertEquals("前保险杠总成描述", part.getDescription());
            assertEquals("MC001", part.getCategoryCode());
            assertEquals(PartType.ASSEMBLY, part.getPartType());
            assertEquals("NODE001", part.getVehicleNodeCode());
            assertEquals("SUP001", part.getSupplierCode());
            assertFalse(part.getIsSoftware());
            assertFalse(part.getFotaUpgradeable());
            assertTrue(part.getIsSafetyCritical());
            assertEquals(KeyPartLevel.KEY, part.getIsKeyPart());
            assertTrue(part.getIsRegulatoryPart());
            assertFalse(part.getIsFramePart());
            assertTrue(part.getIsAccuratelyTraced());
            assertEquals("FFA001", part.getFfaCode());
            assertEquals("FFA描述", part.getFfaDesc());
            assertTrue(part.getIsDigitate());
            assertEquals("M001", part.getInitialModel());
            assertEquals("PROD001", part.getProductionCode());
            assertNotNull(part.getFirstProductionDate());
            assertEquals("张三", part.getDesigner());
            assertEquals("设计部", part.getDesignerDept());
            assertEquals("EA", part.getUom());
            assertEquals("DWG001", part.getDrawingNo());
            assertEquals("V1.0", part.getDrawingVersion());
            assertEquals(new BigDecimal("5.5"), part.getWeight());
            assertEquals("KG", part.getWeightUom());
            assertEquals(LifecycleStage.MASS_PRODUCTION, part.getLifecycleStage());
            assertNull(part.getSubstitutePartCode());
            assertEquals("LOCAL", part.getSourceSystem());
            assertEquals("P001", part.getSourceId());
            assertEquals("LOCAL", part.getIngestionChannel());
            assertEquals(1, part.getVersion());
            assertEquals(PartStatus.ACTIVE, part.getStatus());
            assertEquals("admin", part.getCreateBy());
            assertEquals("admin", part.getModifyBy());
            assertEquals(0, part.getRowVersion());
            assertTrue(part.getRowValid());
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
                    Part.create(
                            "P001", "前保险杠总成", null, null,
                            "MC001", PartType.ASSEMBLY, null, null,
                            false, false, false,
                            null, false, false,
                            false, null, null,
                            false, null, null,
                            null, null, null,
                            null, null, null,
                            null, null,
                            LifecycleStage.PROTOTYPE, null,
                            from, to, "admin"
                    )
            );
        }

        @Test
        @DisplayName("创建成功 - effectiveFrom 和 effectiveTo 都为 null")
        void create_bothDatesNull_success() {
            Part part = Part.create(
                    "P001", "前保险杠总成", null, null,
                    "MC001", PartType.ASSEMBLY, null, null,
                    false, false, false,
                    null, false, false,
                    false, null, null,
                    false, null, null,
                    null, null, null,
                    null, null, null,
                    null, null,
                    LifecycleStage.PROTOTYPE, null,
                    null, null, "admin"
            );

            assertNotNull(part);
            assertNull(part.getEffectiveFrom());
            assertNull(part.getEffectiveTo());
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            Part part = createTestPart();
            int originalVersion = part.getVersion();

            part.update("新名称", null, null,
                    "MC002", PartType.STANDARD_PART, null, null,
                    true, true, false,
                    KeyPartLevel.MAJOR, false, true,
                    false, null, null,
                    false, null, null,
                    null, null, null,
                    null, null, null,
                    null, null,
                    LifecycleStage.PRE_PRODUCTION, null,
                    null, null, "modifier");

            assertEquals(originalVersion + 1, part.getVersion());
            assertEquals("新名称", part.getName());
            assertEquals("MC002", part.getCategoryCode());
            assertEquals(PartType.STANDARD_PART, part.getPartType());
            assertEquals(KeyPartLevel.MAJOR, part.getIsKeyPart());
            assertTrue(part.getIsFramePart());
            assertEquals(LifecycleStage.PRE_PRODUCTION, part.getLifecycleStage());
            assertEquals("modifier", part.getModifyBy());
        }

        @Test
        @DisplayName("更新成功 - code 不可变")
        void update_codeUnchanged() {
            Part part = createTestPart();
            String originalCode = part.getCode();

            part.update("新名称", null, null,
                    "MC002", PartType.STANDARD_PART, null, null,
                    false, false, false,
                    null, false, false,
                    false, null, null,
                    false, null, null,
                    null, null, null,
                    null, null, null,
                    null, null,
                    LifecycleStage.PROTOTYPE, null,
                    null, null, "modifier");

            assertEquals(originalCode, part.getCode());
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            Part part = createTestPart();

            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    part.update("新名称", null, null,
                            "MC001", PartType.ASSEMBLY, null, null,
                            false, false, false,
                            null, false, false,
                            false, null, null,
                            false, null, null,
                            null, null, null,
                            null, null, null,
                            null, null,
                            LifecycleStage.PROTOTYPE, null,
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
            Part part = createTestPart();
            assertEquals(PartStatus.ACTIVE, part.getStatus());

            part.deactivate("admin");

            assertEquals(PartStatus.INACTIVE, part.getStatus());
            assertNotNull(part.getEffectiveTo());
            assertEquals("admin", part.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            Part part = createTestPart();
            part.deactivate("admin");

            assertThrows(IllegalStateException.class, () ->
                    part.deactivate("admin")
            );
        }

        @Test
        @DisplayName("失效失败 - DRAFT 状态")
        void deactivate_draftStatus_throwsException() {
            Part part = createTestPart();
            part.setStatus(PartStatus.DRAFT);

            assertThrows(IllegalStateException.class, () ->
                    part.deactivate("admin")
            );
        }
    }

    @Nested
    @DisplayName("advanceLifecycleStage 生命周期推进方法")
    class AdvanceLifecycleStageTests {

        @Test
        @DisplayName("推进成功 - PROTOTYPE → PRE_PRODUCTION")
        void advance_prototypeToPreProduction_success() {
            Part part = createTestPart();
            assertEquals(LifecycleStage.PROTOTYPE, part.getLifecycleStage());

            part.advanceLifecycleStage(LifecycleStage.PRE_PRODUCTION, "admin");

            assertEquals(LifecycleStage.PRE_PRODUCTION, part.getLifecycleStage());
        }

        @Test
        @DisplayName("推进成功 - 完整生命周期")
        void advance_fullLifecycle_success() {
            Part part = createTestPart();

            part.advanceLifecycleStage(LifecycleStage.PRE_PRODUCTION, "admin");
            assertEquals(LifecycleStage.PRE_PRODUCTION, part.getLifecycleStage());

            part.advanceLifecycleStage(LifecycleStage.MASS_PRODUCTION, "admin");
            assertEquals(LifecycleStage.MASS_PRODUCTION, part.getLifecycleStage());

            part.advanceLifecycleStage(LifecycleStage.PHASE_OUT, "admin");
            assertEquals(LifecycleStage.PHASE_OUT, part.getLifecycleStage());

            part.advanceLifecycleStage(LifecycleStage.OBSOLETE, "admin");
            assertEquals(LifecycleStage.OBSOLETE, part.getLifecycleStage());
        }

        @Test
        @DisplayName("推进失败 - 逆向跳转")
        void advance_reverseTransition_throwsException() {
            Part part = createTestPart();
            part.setLifecycleStage(LifecycleStage.MASS_PRODUCTION);

            assertThrows(IllegalStateException.class, () ->
                    part.advanceLifecycleStage(LifecycleStage.PROTOTYPE, "admin")
            );
        }

        @Test
        @DisplayName("推进失败 - OBSOLETE 为终态")
        void advance_fromObsolete_throwsException() {
            Part part = createTestPart();
            part.setLifecycleStage(LifecycleStage.OBSOLETE);

            assertThrows(IllegalStateException.class, () ->
                    part.advanceLifecycleStage(LifecycleStage.PROTOTYPE, "admin")
            );
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            Part part = createTestPart();
            Date before = new Date();

            part.markAsDeleting("deleter");

            assertEquals("deleter", part.getModifyBy());
            assertNotNull(part.getModifyTime());
            assertTrue(part.getModifyTime().getTime() >= before.getTime());
        }
    }

    private Part createTestPart() {
        return Part.create(
                "P001", "前保险杠总成", "Front Bumper Assembly", "前保险杠总成描述",
                "MC001", PartType.ASSEMBLY, "NODE001", "SUP001",
                false, false, true,
                KeyPartLevel.KEY, true, false,
                true, "FFA001", "FFA描述",
                true, "M001", "PROD001",
                new Date(), "张三", "设计部",
                "EA", "DWG001", "V1.0",
                new BigDecimal("5.5"), "KG",
                LifecycleStage.PROTOTYPE, null,
                null, null, "admin"
        );
    }
}
