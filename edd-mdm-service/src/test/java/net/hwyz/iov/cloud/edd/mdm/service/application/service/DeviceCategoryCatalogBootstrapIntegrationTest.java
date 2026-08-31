package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.DeviceCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.DeviceCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.DeviceCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 设备类别标准目录 Bootstrap 集成测试（CR-037 §11.2）
 * <p>
 * 验证首次初始化 24 条 ACTIVE 记录并产生 24 组 history/outbox、二次执行全 skipped、
 * 同 code 冲突不覆盖、单条失败不回滚、以及多 VehicleNode 共用同一设备类别。
 *
 * @author hwyz_leo
 */
@SpringBootTest
@Transactional
@DisplayName("DeviceCategoryCatalogBootstrap 集成测试")
class DeviceCategoryCatalogBootstrapIntegrationTest {

    @Autowired
    private DeviceCategoryCatalogBootstrap deviceCategoryCatalogBootstrap;
    @Autowired
    private DeviceCategoryAppService deviceCategoryAppService;
    @Autowired
    private DeviceCategoryRepository deviceCategoryRepository;
    @Autowired
    private VehicleNodeAppService vehicleNodeAppService;
    @Autowired
    private OutboxMapper outboxMapper;
    @Autowired
    private DeviceCategoryMapper deviceCategoryMapper;
    @Autowired
    private DeviceCategoryHistoryMapper deviceCategoryHistoryMapper;

    private static final List<String> CATALOG_CODES = List.of(
            "TBOX", "CCU", "DCU", "ZCU", "CGW", "BCM", "VCU", "BMS", "MCU",
            "BRAKE_ECU", "EPS", "AIRBAG_ECU", "IHU", "CLUSTER", "HUD",
            "CAM", "RADAR", "LIDAR", "USS", "OBC", "DCDC", "EVCC",
            "ETH_SW", "V2X_OBU");

    /**
     * 清理 DeviceCategory 主表/历史/outbox 数据，保证测试与存量数据隔离（事务回滚后自动恢复）。
     */
    @BeforeEach
    void cleanDeviceCategoryData() {
        LambdaQueryWrapper<OutboxPo> outboxWrapper = new LambdaQueryWrapper<>();
        outboxWrapper.eq(OutboxPo::getAggregateType, "DEVICE_CATEGORY");
        outboxMapper.delete(outboxWrapper);
        deviceCategoryHistoryMapper.delete(new LambdaQueryWrapper<>());
        deviceCategoryMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    @DisplayName("首次初始化创建 24 条 ACTIVE 记录并产生 24 组 history/outbox")
    void firstBootstrapCreates24ActiveWithHistoryAndOutbox() {
        DeviceCategoryCatalogBootstrapResult result = deviceCategoryCatalogBootstrap.bootstrap();

        assertEquals(24, result.getCreated());
        assertEquals(0, result.getFailed());
        assertEquals(0, result.getConflicted());
        assertEquals(24, deviceCategoryRepository.listAllActive().size());
        // 每条记录产生 CREATE history
        for (String code : CATALOG_CODES) {
            assertFalse(deviceCategoryRepository.findHistoryByCode(code).isEmpty(),
                    "设备类别应产生 history: " + code);
        }
        // 24 组 outbox 创建事件
        assertEquals(24, countDeviceCategoryCreatedOutbox());
    }

    @Test
    @DisplayName("第二次执行全部 skipped，无重复记录和新增事件")
    void secondBootstrapAllSkipped() {
        deviceCategoryCatalogBootstrap.bootstrap();

        DeviceCategoryCatalogBootstrapResult result = deviceCategoryCatalogBootstrap.bootstrap();

        assertEquals(24, result.getSkipped());
        assertEquals(0, result.getCreated());
        assertEquals(24, deviceCategoryRepository.listAllActive().size());
        assertEquals(24, countDeviceCategoryCreatedOutbox());
    }

    @Test
    @DisplayName("同 code 不同语义冲突且不覆盖")
    void conflictNotOverwritten() {
        createCategory("TBOX", "Changed Name", "改名", "被业务改过", 1);

        DeviceCategoryCatalogBootstrapResult result = deviceCategoryCatalogBootstrap.bootstrap();

        assertTrue(result.getConflicted() >= 1);
        assertEquals(23, result.getCreated());
        // 不覆盖业务数据
        DeviceCategory tbox = deviceCategoryRepository.findByCode("TBOX").orElseThrow();
        assertEquals("Changed Name", tbox.getName());
        assertEquals("改名", tbox.getNameLocal());
    }

    @Test
    @DisplayName("Bootstrap 单条失败不回滚其他成功项，统计准确")
    void singleFailureDoesNotRollbackOthers() {
        // 预置与目录 TBOX 同名不同 code 的类别，使目录 TBOX 创建时名称防重失败
        createCategory("LEGACY_TBOX", "Telematics Control Unit", "车载通信终端", "legacy", 99);

        DeviceCategoryCatalogBootstrapResult result = deviceCategoryCatalogBootstrap.bootstrap();

        assertEquals(1, result.getFailed());
        assertEquals(23, result.getCreated());
        assertTrue(result.getDetails().stream().anyMatch(d -> d.startsWith("TBOX")));
        // 其余 23 个标准类别均已创建
        assertEquals(24, deviceCategoryRepository.listAllActive().size());
        assertTrue(deviceCategoryRepository.findByCode("LEGACY_TBOX").isPresent());
    }

    @Test
    @DisplayName("TBOX_4G/TBOX_5G 同时引用 TBOX；CAM_FRONT_8M/CAM_DMS 同时引用 CAM")
    void multipleVehicleNodesShareSameCategory() {
        createCategory("TBOX", "Telematics Control Unit", "车载通信终端", null, 1);
        createCategory("CAM", "Camera", "摄像头", null, 2);

        VehicleNodeDto tbox4g = createVehicleNode("TBOX_4G", "TBOX", "TELEMATICS", "CONNECTIVITY");
        VehicleNodeDto tbox5g = createVehicleNode("TBOX_5G", "TBOX", "TELEMATICS", "CONNECTIVITY");
        VehicleNodeDto camFront = createVehicleNode("CAM_FRONT_8M", "CAM", "SENSOR", "ADAS");
        VehicleNodeDto camDms = createVehicleNode("CAM_DMS", "CAM", "SENSOR", "ADAS");

        assertEquals("TBOX", tbox4g.getDeviceCategory());
        assertEquals("TBOX", tbox5g.getDeviceCategory());
        assertEquals("CAM", camFront.getDeviceCategory());
        assertEquals("CAM", camDms.getDeviceCategory());
    }

    private void createCategory(String code, String name, String nameLocal, String description, int sortOrder) {
        deviceCategoryAppService.createDeviceCategory(DeviceCategoryCreateCmd.builder()
                .code(code).name(name).nameLocal(nameLocal)
                .description(description).sortOrder(sortOrder)
                .createBy("test").build());
    }

    private VehicleNodeDto createVehicleNode(String nodeCode, String deviceCategory, String nodeType,
                                             String functionalDomain) {
        return vehicleNodeAppService.create(VehicleNodeCreateCmd.builder()
                .nodeCode(nodeCode)
                .name(nodeCode)
                .nameLocal(nodeCode)
                .nodeType(nodeType)
                .functionalDomain(functionalDomain)
                .deviceCategory(deviceCategory)
                .isCoreNode(false)
                .otaSupportType("BOTH")
                .hsmCapability("HSM_LIGHT")
                .securityLevel("CAL2")
                .createBy("test")
                .build());
    }

    private long countDeviceCategoryCreatedOutbox() {
        LambdaQueryWrapper<OutboxPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboxPo::getAggregateType, "DEVICE_CATEGORY");
        wrapper.eq(OutboxPo::getEventType, "DeviceCategoryCreated");
        return outboxMapper.selectCount(wrapper);
    }
}
