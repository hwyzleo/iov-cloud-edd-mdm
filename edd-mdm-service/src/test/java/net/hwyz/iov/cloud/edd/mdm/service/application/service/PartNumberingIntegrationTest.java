package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartGenerationUpgradeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartMinorRevisionCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

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

    @Autowired
    private MaterialCategoryAppService materialCategoryAppService;

    @Autowired
    private OutboxMapper outboxMapper;
    @Autowired
    private MaterialCategoryMapper materialCategoryMapper;
    @Autowired
    private MaterialCategoryHistoryMapper materialCategoryHistoryMapper;

    /**
     * 预置标准 L1/L2/L3 叶子目录链（CR-039：Part.categoryCode 必须指向 ACTIVE 且无 ACTIVE 子节点的 L3 叶子）。
     * 先隔离存量 legacy 品类数据（如 M02/M0201/M0203），避免名称防重冲突；事务回滚后恢复。
     */
    @BeforeEach
    void setUpMaterialCategoryLeaf() {
        outboxMapper.delete(new LambdaQueryWrapper<OutboxPo>().eq(OutboxPo::getAggregateType, "MATERIAL_CATEGORY"));
        materialCategoryHistoryMapper.delete(new LambdaQueryWrapper<>());
        materialCategoryMapper.delete(new LambdaQueryWrapper<>());
        if (!materialCategoryAppService.existsMaterialCategory("MC_CMP")) {
            materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_CMP").name("Component / Part").nameLocal("零部件").createBy("test").build());
        }
        if (!materialCategoryAppService.existsMaterialCategory("MC_CMP_BODY")) {
            materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_CMP_BODY").name("Body Structure & Closures").nameLocal("车身结构与闭合")
                    .parentCode("MC_CMP").createBy("test").build());
        }
        if (!materialCategoryAppService.existsMaterialCategory("MC_CMP_BODY_BIW")) {
            materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_CMP_BODY_BIW").name("Body-in-White").nameLocal("白车身")
                    .parentCode("MC_CMP_BODY").createBy("test").build());
        }
    }

    @Test
    void testCreatePartWithSystemNumbering() {
        PartCreateCmd cmd = PartCreateCmd.builder()
                .name("测试零件")
                .categoryCode("MC_CMP_BODY_BIW")
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
    void testCreateSoftwarePartWithoutSPrefix() {
        PartCreateCmd cmd = PartCreateCmd.builder()
                .name("软件件")
                .categoryCode("MC_CMP_BODY_BIW")
                .partType("STANDARD_PART")
                .isSoftware(true)
                .fotaUpgradeable(true)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();

        PartDto dto = partAppService.createPart(cmd);

        assertNotNull(dto.getId());
        assertNotNull(dto.getCode());
        assertFalse(dto.getCode().startsWith("S"));
        assertTrue(dto.getCode().endsWith("AA"));
        assertTrue(dto.getBaseNo().matches("\\d{8}"));
    }

    @Test
    void testCreateSoftwareAssemblyPart() {
        PartCreateCmd cmd = PartCreateCmd.builder()
                .name("软件总成件")
                .categoryCode("MC_CMP_BODY_BIW")
                .partType("STANDARD_PART")
                .isSoftware(true)
                .isAssembly(true)
                .fotaUpgradeable(true)
                .lifecycleStage("PROTOTYPE")
                .createBy("test")
                .build();

        PartDto dto = partAppService.createPart(cmd);

        assertNotNull(dto.getId());
        assertNotNull(dto.getCode());
        assertFalse(dto.getCode().startsWith("S"));
        assertTrue(dto.getCode().endsWith("AA"));
        assertTrue(dto.getBaseNo().matches("\\d{8}"));
    }

    @Test
    void testUpgradeGeneration() {
        // 先创建一个零件
        PartCreateCmd createCmd = PartCreateCmd.builder()
                .name("测试零件")
                .categoryCode("MC_CMP_BODY_BIW")
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
                .categoryCode("MC_CMP_BODY_BIW")
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
