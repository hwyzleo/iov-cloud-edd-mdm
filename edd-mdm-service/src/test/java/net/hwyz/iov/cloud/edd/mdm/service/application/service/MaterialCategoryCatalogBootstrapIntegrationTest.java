package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryCatalogStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.MaterialCategoryHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.MaterialCategoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类标准目录 Bootstrap 集成测试（CR-039 §11.2）
 * <p>
 * 验证首次初始化 101 条 ACTIVE 记录（4 L1 + 19 L2 + 78 L3）并产生 101 组 history/outbox、
 * 二次执行全 skipped、同 code 冲突不覆盖且子树依赖失败隔离、以及 preview 统计。
 *
 * @author hwyz_leo
 */
@SpringBootTest
@Transactional
@DisplayName("MaterialCategoryCatalogBootstrap 集成测试")
class MaterialCategoryCatalogBootstrapIntegrationTest {

    @Autowired
    private MaterialCategoryCatalogBootstrap materialCategoryCatalogBootstrap;
    @Autowired
    private MaterialCategoryAppService materialCategoryAppService;
    @Autowired
    private MaterialCategoryRepository materialCategoryRepository;
    @Autowired
    private OutboxMapper outboxMapper;
    @Autowired
    private MaterialCategoryMapper materialCategoryMapper;
    @Autowired
    private MaterialCategoryHistoryMapper materialCategoryHistoryMapper;

    /**
     * 清理 MaterialCategory 主表/历史/outbox 数据，保证测试与存量数据隔离（事务回滚后自动恢复）。
     */
    @BeforeEach
    void cleanMaterialCategoryData() {
        LambdaQueryWrapper<OutboxPo> outboxWrapper = new LambdaQueryWrapper<>();
        outboxWrapper.eq(OutboxPo::getAggregateType, "MATERIAL_CATEGORY");
        outboxMapper.delete(outboxWrapper);
        materialCategoryHistoryMapper.delete(new LambdaQueryWrapper<>());
        materialCategoryMapper.delete(new LambdaQueryWrapper<>());
    }

    @Test
    @DisplayName("首次初始化创建 101 条 ACTIVE 记录（4/19/78）并产生 101 组 history/outbox")
    void firstBootstrapCreates101ActiveWithHistoryAndOutbox() {
        MaterialCategoryCatalogBootstrapResult result = materialCategoryCatalogBootstrap.bootstrap();

        assertEquals(MaterialCategoryCatalogStatus.VALID.name(), result.getCatalogStatus());
        assertEquals(101, result.getCreated());
        assertEquals(0, result.getFailed());
        assertEquals(0, result.getConflicted());
        assertEquals(0, result.getDependencyFailed());
        assertEquals(101, materialCategoryRepository.listAllActive().size());
        assertEquals(4, materialCategoryRepository.findAll().stream()
                .filter(c -> c.getParentCode() == null || c.getParentCode().isBlank()).count());
        assertEquals(19, materialCategoryRepository.findAll().stream()
                .filter(c -> c.getParentCode() != null && !c.getParentCode().isBlank()
                        && hasChildren(c.getCode())).count());
        // 每条记录产生 history 与 outbox 创建事件
        assertEquals(101, materialCategoryHistoryMapper.selectCount(new LambdaQueryWrapper<>()));
        assertEquals(101, countMaterialCategoryCreatedOutbox());
    }

    @Test
    @DisplayName("第二次执行全部 skipped，无重复记录和新增事件")
    void secondBootstrapAllSkipped() {
        materialCategoryCatalogBootstrap.bootstrap();

        MaterialCategoryCatalogBootstrapResult result = materialCategoryCatalogBootstrap.bootstrap();

        assertEquals(101, result.getSkipped());
        assertEquals(0, result.getCreated());
        assertEquals(101, materialCategoryRepository.listAllActive().size());
        assertEquals(101, countMaterialCategoryCreatedOutbox());
    }

    @Test
    @DisplayName("同 code 不同语义冲突不覆盖，且其子树依赖失败隔离")
    void conflictNotOverwrittenWithDependencyFailure() {
        // 预置与标准目录完全一致的 MC_CMP（含 description，保证 matches=true → skipped）与改名后的 MC_CMP_BODY
        materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                .code("MC_CMP").name("Component / Part").nameLocal("零部件")
                .description("零部件大类，覆盖车身、外饰、内饰、底盘、动力、能源、热管理、电气电子、智能驾驶与通用件")
                .createBy("test").build());
        materialCategoryAppService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                .code("MC_CMP_BODY").name("Changed Name").nameLocal("改名").description("被业务改过")
                .parentCode("MC_CMP").createBy("test").build());

        MaterialCategoryCatalogBootstrapResult result = materialCategoryCatalogBootstrap.bootstrap();

        // MC_CMP 已存在且一致 → skipped；MC_CMP_BODY 冲突 → conflicted；其 4 个 L3 子树 → dependencyFailed
        assertEquals(1, result.getSkipped());
        assertEquals(1, result.getConflicted());
        assertEquals(4, result.getDependencyFailed());
        assertEquals(0, result.getFailed());
        assertEquals(95, result.getCreated());
        // 不覆盖业务数据
        MaterialCategoryPo body = materialCategoryMapper.selectOne(new LambdaQueryWrapper<MaterialCategoryPo>()
                .eq(MaterialCategoryPo::getCode, "MC_CMP_BODY"));
        assertEquals("Changed Name", body.getName());
        assertEquals("改名", body.getNameLocal());
    }

    @Test
    @DisplayName("Preview 返回 VALID、total=101 与 4/19/78 分层统计")
    void previewCounts() {
        MaterialCategoryCatalogPreviewResult preview = materialCategoryCatalogBootstrap.preview();

        assertEquals(MaterialCategoryCatalogStatus.VALID.name(), preview.getCatalogStatus());
        assertEquals(101, preview.getTotal());
        assertEquals(4, preview.getLevel1Count());
        assertEquals(19, preview.getLevel2Count());
        assertEquals(78, preview.getLevel3Count());
        assertEquals(101, preview.getMissing());
        assertEquals(101, preview.getItems().size());
    }

    private boolean hasChildren(String code) {
        return materialCategoryRepository.countActiveChildren(code) > 0;
    }

    private long countMaterialCategoryCreatedOutbox() {
        LambdaQueryWrapper<OutboxPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboxPo::getAggregateType, "MATERIAL_CATEGORY");
        wrapper.eq(OutboxPo::getEventType, "MaterialCategoryCreated");
        return outboxMapper.selectCount(wrapper);
    }
}
