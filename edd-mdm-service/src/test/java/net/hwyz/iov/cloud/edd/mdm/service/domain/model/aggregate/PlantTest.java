package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 工厂聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("Plant 聚合根测试")
class PlantTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 所有字段正确填充")
        void create_success() {
            Plant plant = Plant.create(
                    "PLT_CN_CD_01", "成都工厂", "Chengdu Plant", "成都", "整车总装工厂",
                    PlantType.VEHICLE_ASSEMBLY, "LE001", "CC001",
                    "中国", "四川省", "成都市", "高新区天府大道",
                    new BigDecimal("104.065735"), new BigDecimal("30.659462"), "Asia/Shanghai",
                    200000L, 4, new Date(), "MES_CD_01",
                    null, null, "admin"
            );

            assertEquals("PLT_CN_CD_01", plant.getCode());
            assertEquals("成都工厂", plant.getName());
            assertEquals("Chengdu Plant", plant.getNameEn());
            assertEquals("成都", plant.getShortName());
            assertEquals("整车总装工厂", plant.getDescription());
            assertEquals(PlantType.VEHICLE_ASSEMBLY, plant.getPlantType());
            assertEquals("LE001", plant.getLegalEntityCode());
            assertEquals("CC001", plant.getCostCenterCode());
            assertEquals("中国", plant.getCountry());
            assertEquals("四川省", plant.getProvince());
            assertEquals("成都市", plant.getCity());
            assertEquals("高新区天府大道", plant.getAddress());
            assertNotNull(plant.getLongitude());
            assertNotNull(plant.getLatitude());
            assertEquals("Asia/Shanghai", plant.getTimezone());
            assertEquals(200000L, plant.getAnnualCapacity());
            assertEquals(4, plant.getProductionLines());
            assertNotNull(plant.getOperationalStartDate());
            assertEquals("MES_CD_01", plant.getMesInstance());
            assertEquals("LOCAL", plant.getSourceSystem());
            assertEquals("PLT_CN_CD_01", plant.getSourceId());
            assertEquals("LOCAL", plant.getIngestionChannel());
            assertEquals(1, plant.getVersion());
            assertEquals(PlantStatus.ACTIVE, plant.getStatus());
            assertEquals("admin", plant.getCreateBy());
            assertEquals("admin", plant.getModifyBy());
            assertEquals(0, plant.getRowVersion());
            assertTrue(plant.getRowValid());
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
                    Plant.create(
                            "PLT_CN_CD_01", "成都工厂", null, null, null,
                            PlantType.VEHICLE_ASSEMBLY, null, null,
                            null, null, null, null,
                            null, null, null,
                            null, null, null, null,
                            from, to, "admin"
                    )
            );
        }

        @Test
        @DisplayName("创建成功 - effectiveFrom 和 effectiveTo 都为 null")
        void create_bothDatesNull_success() {
            Plant plant = Plant.create(
                    "PLT_CN_CD_01", "成都工厂", null, null, null,
                    PlantType.VEHICLE_ASSEMBLY, null, null,
                    null, null, null, null,
                    null, null, null,
                    null, null, null, null,
                    null, null, "admin"
            );

            assertNotNull(plant);
            assertNull(plant.getEffectiveFrom());
            assertNull(plant.getEffectiveTo());
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("更新成功 - version 自增")
        void update_success_versionIncremented() {
            Plant plant = createTestPlant();
            int originalVersion = plant.getVersion();

            plant.update("新名称", null, null, null,
                    PlantType.POWERTRAIN, null, null,
                    null, null, null, null,
                    null, null, null,
                    null, null, null, null,
                    null, null, "modifier");

            assertEquals(originalVersion + 1, plant.getVersion());
            assertEquals("新名称", plant.getName());
            assertEquals(PlantType.POWERTRAIN, plant.getPlantType());
            assertEquals("modifier", plant.getModifyBy());
        }

        @Test
        @DisplayName("更新成功 - code 不可变")
        void update_codeUnchanged() {
            Plant plant = createTestPlant();
            String originalCode = plant.getCode();

            plant.update("新名称", null, null, null,
                    PlantType.POWERTRAIN, null, null,
                    null, null, null, null,
                    null, null, null,
                    null, null, null, null,
                    null, null, "modifier");

            assertEquals(originalCode, plant.getCode());
        }

        @Test
        @DisplayName("更新失败 - effectiveFrom 晚于 effectiveTo")
        void update_invalidEffectiveDate_throwsException() {
            Plant plant = createTestPlant();

            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    plant.update("新名称", null, null, null,
                            PlantType.POWERTRAIN, null, null,
                            null, null, null, null,
                            null, null, null,
                            null, null, null, null,
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
            Plant plant = createTestPlant();
            assertEquals(PlantStatus.ACTIVE, plant.getStatus());

            plant.deactivate("admin");

            assertEquals(PlantStatus.INACTIVE, plant.getStatus());
            assertNotNull(plant.getEffectiveTo());
            assertEquals("admin", plant.getModifyBy());
        }

        @Test
        @DisplayName("失效失败 - 非 ACTIVE 状态")
        void deactivate_nonActiveStatus_throwsException() {
            Plant plant = createTestPlant();
            plant.deactivate("admin");

            assertThrows(IllegalStateException.class, () ->
                    plant.deactivate("admin")
            );
        }

        @Test
        @DisplayName("失效失败 - DRAFT 状态")
        void deactivate_draftStatus_throwsException() {
            Plant plant = createTestPlant();
            plant.setStatus(PlantStatus.DRAFT);

            assertThrows(IllegalStateException.class, () ->
                    plant.deactivate("admin")
            );
        }
    }

    @Nested
    @DisplayName("markAsDeleting 标记删除方法")
    class MarkAsDeletingTests {

        @Test
        @DisplayName("标记删除成功")
        void markAsDeleting_success() {
            Plant plant = createTestPlant();
            Date before = new Date();

            plant.markAsDeleting("deleter");

            assertEquals("deleter", plant.getModifyBy());
            assertNotNull(plant.getModifyTime());
            assertTrue(plant.getModifyTime().getTime() >= before.getTime());
        }
    }

    private Plant createTestPlant() {
        return Plant.create(
                "PLT_CN_CD_01", "成都工厂", "Chengdu Plant", "成都", "整车总装工厂",
                PlantType.VEHICLE_ASSEMBLY, "LE001", "CC001",
                "中国", "四川省", "成都市", "高新区天府大道",
                new BigDecimal("104.065735"), new BigDecimal("30.659462"), "Asia/Shanghai",
                200000L, 4, new Date(), "MES_CD_01",
                null, null, "admin"
        );
    }
}
