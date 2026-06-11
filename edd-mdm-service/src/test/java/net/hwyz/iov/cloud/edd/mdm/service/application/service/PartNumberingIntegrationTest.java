package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartGenerationUpgradeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartMinorRevisionCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Part零件号编码增强集成测试
 * CR-023
 */
@SpringBootTest
@Transactional
class PartNumberingIntegrationTest {

    @Autowired
    private PartAppService partAppService;

    @Test
    void testCreatePartWithSystemNumbering() {
        PartCreateCmd cmd = PartCreateCmd.builder()
                .name("测试零件")
                .categoryCode("CAT001")
                .partType("STANDARD_PART")
                .isSoftware(false)
                .fotaUpgradeable(false)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();

        PartDto dto = partAppService.createPart(cmd);

        assertNotNull(dto.getId());
        assertNotNull(dto.getCode());
        assertTrue(dto.getCode().endsWith("AA"));
        assertNotNull(dto.getBaseNo());
        assertEquals("MDM_GEN", dto.getNumberingSource());
        assertTrue(dto.getBaseNo().matches("\\d{8}"));
    }

    @Test
    void testCreateSoftwarePartWithSPrefix() {
        PartCreateCmd cmd = PartCreateCmd.builder()
                .name("软件件")
                .categoryCode("CAT001")
                .partType("SOFTWARE")
                .isSoftware(true)
                .fotaUpgradeable(true)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();

        PartDto dto = partAppService.createPart(cmd);

        assertNotNull(dto.getId());
        assertNotNull(dto.getCode());
        assertTrue(dto.getCode().startsWith("S"));
        assertTrue(dto.getCode().endsWith("AA"));
        assertTrue(dto.getBaseNo().startsWith("S"));
    }

    @Test
    void testUpgradeGeneration() {
        // 先创建一个零件
        PartCreateCmd createCmd = PartCreateCmd.builder()
                .name("测试零件")
                .categoryCode("CAT001")
                .partType("STANDARD_PART")
                .isSoftware(false)
                .fotaUpgradeable(false)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();
        PartDto created = partAppService.createPart(createCmd);

        // 升级代次
        PartGenerationUpgradeCmd upgradeCmd = PartGenerationUpgradeCmd.builder()
                .code(created.getCode())
                .operator("test")
                .build();
        PartDto upgraded = partAppService.upgradeGeneration(upgradeCmd);

        // 验证
        assertNotEquals(created.getId(), upgraded.getId());
        assertNotEquals(created.getCode(), upgraded.getCode());
        assertEquals(created.getBaseNo(), upgraded.getBaseNo());
        assertTrue(upgraded.getCode().endsWith("AB"));
    }

    @Test
    void testMinorRevision() {
        // 先创建一个零件
        PartCreateCmd createCmd = PartCreateCmd.builder()
                .name("测试零件")
                .categoryCode("CAT001")
                .partType("STANDARD_PART")
                .isSoftware(false)
                .fotaUpgradeable(false)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();
        PartDto created = partAppService.createPart(createCmd);

        // 小修订
        PartMinorRevisionCmd revisionCmd = PartMinorRevisionCmd.builder()
                .code(created.getCode())
                .drawingVersion("V2.0")
                .operator("test")
                .build();
        PartDto revised = partAppService.minorRevision(revisionCmd);

        // 验证
        assertEquals(created.getId(), revised.getId());
        assertEquals(created.getCode(), revised.getCode());
        assertEquals("V2.0", revised.getDrawingVersion());
        assertEquals(created.getVersion() + 1, revised.getVersion());
    }
}
