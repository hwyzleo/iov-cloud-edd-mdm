package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryDepthExceededException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryNotLeafException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartCategoryInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PartMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartPo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Part 叶子归类校验集成测试（CR-039 §7）
 * <p>
 * 验证 Part 新建/更新时 categoryCode 必须指向 ACTIVE 且无 ACTIVE 子节点的 L3 叶子：
 * L1/L2 引用返回 812923、标准/扩展 L3 成功、四层创建返回 812926、
 * 不存在/非 ACTIVE 返回 812911、legacy 已引用非叶子的 Part 普通更新不被阻断。
 *
 * @author hwyz_leo
 */
@SpringBootTest
@Transactional
@DisplayName("Part 叶子归类校验集成测试")
class PartAppServiceLeafIntegrationTest {

    @Autowired
    private PartAppService partAppService;
    @Autowired
    private MaterialCategoryAppService materialCategoryAppService;
    @Autowired
    private PartMapper partMapper;
    @Autowired
    private OutboxMapper outboxMapper;
    @Autowired
    private MaterialCategoryMapper materialCategoryMapper;
    @Autowired
    private MaterialCategoryHistoryMapper materialCategoryHistoryMapper;

    @BeforeEach
    void setUpLeafCatalog() {
        // 隔离存量 legacy 品类数据（如 M02/M0201/M0203），避免名称防重冲突；事务回滚后恢复
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
        if (!materialCategoryAppService.existsMaterialCategory("MC_CMP_BODY_X_EXTRA")) {
            materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_CMP_BODY_X_EXTRA").name("Enterprise Extension").nameLocal("企业扩展")
                    .parentCode("MC_CMP_BODY").createBy("test").build());
        }
    }

    @Test
    @DisplayName("Part 引用 L1 返回 812923")
    void partReferencingL1Rejected() {
        assertThrows(MaterialCategoryNotLeafException.class,
                () -> partAppService.createPart(createCmd("MC_CMP")));
    }

    @Test
    @DisplayName("Part 引用 L2 返回 812923")
    void partReferencingL2Rejected() {
        assertThrows(MaterialCategoryNotLeafException.class,
                () -> partAppService.createPart(createCmd("MC_CMP_BODY")));
    }

    @Test
    @DisplayName("Part 引用标准 L3 叶子成功")
    void partReferencingStandardL3Success() {
        PartDto dto = partAppService.createPart(createCmd("MC_CMP_BODY_BIW"));
        assertNotNull(dto.getId());
        assertEquals("MC_CMP_BODY_BIW", dto.getCategoryCode());
    }

    @Test
    @DisplayName("Part 引用企业扩展 L3 叶子成功")
    void partReferencingExtensionL3Success() {
        PartDto dto = partAppService.createPart(createCmd("MC_CMP_BODY_X_EXTRA"));
        assertNotNull(dto.getId());
        assertEquals("MC_CMP_BODY_X_EXTRA", dto.getCategoryCode());
    }

    @Test
    @DisplayName("Part 引用不存在的品类返回 812911")
    void partReferencingUnknownCategoryRejected() {
        assertThrows(PartCategoryInvalidException.class,
                () -> partAppService.createPart(createCmd("MC_XXX")));
    }

    @Test
    @DisplayName("在 L3 下创建第四层返回 812926")
    void createFourthLevelRejected() {
        assertThrows(MaterialCategoryDepthExceededException.class,
                () -> materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                        .code("MC_CMP_BODY_BIW_CHILD").name("Child").nameLocal("子项")
                        .parentCode("MC_CMP_BODY_BIW").createBy("test").build()));
    }

    @Test
    @DisplayName("legacy 已引用非叶子的 Part 普通非分类更新不被阻断")
    void legacyPartOrdinaryUpdateNotBlocked() {
        PartDto created = partAppService.createPart(createCmd("MC_CMP_BODY_BIW"));
        // 模拟 legacy 数据：直接改库把 category_code 指向非叶子 L1
        partMapper.update(null, new LambdaUpdateWrapper<PartPo>()
                .eq(PartPo::getCode, created.getCode())
                .set(PartPo::getCategoryCode, "MC_CMP"));

        // 普通非分类更新（categoryCode 未变化）应成功
        PartDto updated = partAppService.updatePart(updateCmd(created.getCode(), "MC_CMP", "改名"));

        assertEquals("改名", updated.getName());
        assertEquals("MC_CMP", updated.getCategoryCode());
    }

    @Test
    @DisplayName("legacy Part 变更 categoryCode 到标准叶子成功，变到非叶子被拒")
    void legacyPartChangeCategoryEnforced() {
        PartDto created = partAppService.createPart(createCmd("MC_CMP_BODY_BIW"));
        partMapper.update(null, new LambdaUpdateWrapper<PartPo>()
                .eq(PartPo::getCode, created.getCode())
                .set(PartPo::getCategoryCode, "MC_CMP"));

        // 变到非叶子 L2 → 812923
        assertThrows(MaterialCategoryNotLeafException.class,
                () -> partAppService.updatePart(updateCmd(created.getCode(), "MC_CMP_BODY", "x")));

        // 变到标准 L3 叶子 → 成功
        PartDto updated = partAppService.updatePart(updateCmd(created.getCode(), "MC_CMP_BODY_BIW", "迁回"));
        assertEquals("MC_CMP_BODY_BIW", updated.getCategoryCode());
    }

    private PartCreateCmd createCmd(String categoryCode) {
        return PartCreateCmd.builder()
                .name("测试零件").categoryCode(categoryCode)
                .partType("STANDARD_PART").isSoftware(false).fotaUpgradeable(false)
                .lifecycleStage("PROTOTYPE").createBy("test").build();
    }

    private PartUpdateCmd updateCmd(String code, String categoryCode, String name) {
        return PartUpdateCmd.builder()
                .code(code).name(name).nameLocal(name).description(null).categoryCode(categoryCode)
                .partType("STANDARD_PART").vehicleNodeCode(null).supplierCode(null)
                .isSoftware(false).isAssembly(false).fotaUpgradeable(false).isSafetyCritical(false)
                .isKeyPart(null).isRegulatoryPart(false).isFramePart(false)
                .isAccuratelyTraced(false).ffaCode(null).ffaDesc(null).isDigitate(false)
                .initialModel(null).productionCode(null).firstProductionDate(null)
                .designer(null).designerDept(null).uom(null).drawingNo(null).drawingVersion(null)
                .weight(null).weightUom(null).lifecycleStage("PROTOTYPE").substitutePartCode(null)
                .effectiveFrom(null).effectiveTo(null).modifyBy("test").build();
    }
}
