package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.DeviceCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.DeviceCategoryNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备类别存量治理审计单元测试（CR-037 §8）
 *
 * @author hwyz_leo
 */
@DisplayName("DeviceCategoryLegacyAudit 测试")
class DeviceCategoryLegacyAuditTest {

    private DeviceCategoryLegacyAudit audit;

    @BeforeEach
    void setUp() {
        audit = new DeviceCategoryLegacyAudit(new DeviceCategoryCodePolicy(), new DeviceCategoryNamePolicy());
    }

    private List<DeviceCategoryCatalogEntry> catalog() {
        return List.of(
                entry("TBOX", "Telematics Control Unit", "车载通信终端", List.of("TCU", "DC_TBOX")),
                entry("CAM", "Camera", "摄像头", List.of()),
                entry("LIDAR", "LiDAR", "激光雷达", List.of())
        );
    }

    private DeviceCategoryCatalogEntry entry(String code, String name, String nameLocal, List<String> aliases) {
        return DeviceCategoryCatalogEntry.builder()
                .code(code).name(name).nameLocal(nameLocal).aliases(aliases).sortOrder(1)
                .build();
    }

    private DeviceCategory category(String code, String name, String nameLocal) {
        return DeviceCategory.create(code, name, nameLocal, null, 0, null, null, "admin");
    }

    @Nested
    @DisplayName("识别候选")
    class IdentificationTests {

        @Test
        @DisplayName("近义 legacy code（DC_TBOX 命中 aliases）识别并建议归一到 TBOX")
        void legacyNearSynonymIdentified() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(category("DC_TBOX", "车载通信终端", "Telematics Box")), catalog());

            assertTrue(result.getFindings().stream().anyMatch(f ->
                    DeviceCategoryLegacyAuditResult.TYPE_NEAR_SYNONYM.equals(f.getType())
                            && "DC_TBOX".equals(f.getCode())
                            && "TBOX".equals(f.getSuggestedStandardCode())));
        }

        @Test
        @DisplayName("未知 legacy code（非标准目录、非别名、非规格化）识别为 LEGACY_CODE")
        void unknownLegacyCodeIdentified() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(category("MY_CUSTOM", "自定义设备", "Custom Device")), catalog());

            assertTrue(result.getFindings().stream().anyMatch(f ->
                    DeviceCategoryLegacyAuditResult.TYPE_LEGACY_CODE.equals(f.getType())
                            && "MY_CUSTOM".equals(f.getCode())));
        }

        @Test
        @DisplayName("规格化 code（携带节点规格语义）被识别")
        void specCodeIdentified() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(category("TBOX_4G", "车载通信终端", "Telematics Box")), catalog());

            assertTrue(result.getFindings().stream().anyMatch(f ->
                    DeviceCategoryLegacyAuditResult.TYPE_SPEC_CODE.equals(f.getType())
                            && "TBOX_4G".equals(f.getCode())));
        }

        @Test
        @DisplayName("名称标准化后与标准目录重复被识别并建议归一到标准类别")
        void nameDuplicateIdentified() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(category("CAM_X", "Camera", "摄像头")), catalog());

            assertTrue(result.getFindings().stream().anyMatch(f ->
                    DeviceCategoryLegacyAuditResult.TYPE_NAME_DUPLICATE.equals(f.getType())
                            && "CAM".equals(f.getSuggestedStandardCode())));
        }

        @Test
        @DisplayName("标准类别本身不产生任何发现")
        void standardCategoryNoFinding() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(category("TBOX", "Telematics Control Unit", "车载通信终端"),
                            category("CAM", "Camera", "摄像头")), catalog());

            assertEquals(0, result.getFindings().size());
        }

        @Test
        @DisplayName("统计与计数正确")
        void countsCorrect() {
            DeviceCategoryLegacyAuditResult result = audit.audit(
                    List.of(
                            category("DC_TBOX", "车载通信终端", "Telematics Box"),
                            category("TBOX_4G", "车载通信终端", "Telematics Box"),
                            category("MY_CUSTOM", "自定义设备", "Custom Device"),
                            category("TBOX", "Telematics Control Unit", "车载通信终端")
                    ), catalog());

            assertEquals(4, result.getTotalExisting());
            assertEquals(1, result.countByType(DeviceCategoryLegacyAuditResult.TYPE_NEAR_SYNONYM));
            assertEquals(1, result.countByType(DeviceCategoryLegacyAuditResult.TYPE_SPEC_CODE));
            assertEquals(1, result.countByType(DeviceCategoryLegacyAuditResult.TYPE_LEGACY_CODE));
        }
    }
}
