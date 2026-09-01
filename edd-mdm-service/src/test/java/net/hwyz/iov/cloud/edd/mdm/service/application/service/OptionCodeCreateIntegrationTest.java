package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFamilyPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionCodeHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionCodeMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionFamilyHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OptionFamilyMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodeHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选项码 MPT 创建路径编码治理集成测试（CR-040 §9.2/§9.3）
 * <p>
 * 验证：族不存在/非 ACTIVE 优先返回引用错误、格式非法 812127、族主干不一致 812128、
 * 重复 812101、合法创建同步写入主表/history/outbox、扩展族新建、code 不可变与 legacy 更新兼容。
 *
 * @author hwyz_leo
 */
@SpringBootTest
@Transactional
@DisplayName("OptionCode 创建编码治理集成测试")
class OptionCodeCreateIntegrationTest {

    @Autowired
    private OptionCodeAppService optionCodeAppService;
    @Autowired
    private OptionFamilyAppService optionFamilyAppService;
    @Autowired
    private OptionCodeMapper optionCodeMapper;
    @Autowired
    private OptionCodeHistoryMapper optionCodeHistoryMapper;
    @Autowired
    private OptionFamilyMapper optionFamilyMapper;
    @Autowired
    private OptionFamilyHistoryMapper optionFamilyHistoryMapper;
    @Autowired
    private OutboxMapper outboxMapper;

    @BeforeEach
    void setUpFamilies() {
        // 清理存量 OptionCode/OptionFamily（含 outbox/history），隔离名称防重与 code 唯一
        outboxMapper.delete(new LambdaQueryWrapper<OutboxPo>().in(OutboxPo::getAggregateType,
                "OPTION_CODE", "OPTION_FAMILY"));
        optionCodeHistoryMapper.delete(new LambdaQueryWrapper<>());
        optionCodeMapper.delete(new LambdaQueryWrapper<>());
        optionFamilyHistoryMapper.delete(new LambdaQueryWrapper<>());
        optionFamilyMapper.delete(new LambdaQueryWrapper<>());

        optionFamilyAppService.createOptionFamily(familyCmd(
                "OF_EXT_BODY_COLOR", "Body Color", "车身颜色", "EXTERIOR"));
        optionFamilyAppService.createOptionFamily(familyCmd(
                "OF_PWR_DRIVE_TYPE", "Drive Type", "驱动类型", "POWERTRAIN"));
        // 企业扩展族
        optionFamilyAppService.createOptionFamily(familyCmd(
                "OF_EXT_X_SPECIAL_PAINT", "Special Paint", "特殊漆面", "EXTERIOR"));
    }

    private OptionFamilyCreateCmd familyCmd(String code, String name, String nameLocal, String category) {
        return OptionFamilyCreateCmd.builder()
                .code(code).name(name).nameLocal(nameLocal)
                .category(category).createBy("test").build();
    }

    private OptionCodeCreateCmd createCmd(String code, String optionFamilyCode) {
        return OptionCodeCreateCmd.builder()
                .code(code).name("Black").nameLocal("黑色")
                .optionFamilyCode(optionFamilyCode)
                .createBy("test")
                .build();
    }

    @Test
    @DisplayName("族不存在 -> 引用错误优先（不校验编码）")
    void familyMissingReferenceErrorFirst() {
        assertThrows(IllegalArgumentException.class, () -> optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_XXX_NOT_EXIST")));
    }

    @Test
    @DisplayName("族非 ACTIVE -> 引用错误优先")
    void familyInactiveReferenceErrorFirst() {
        optionFamilyAppService.deactivateOptionFamily("OF_PWR_DRIVE_TYPE", "test");

        assertThrows(IllegalArgumentException.class, () -> optionCodeAppService.createOptionCode(
                createCmd("OC_PWR_DRIVE_TYPE_AWD", "OF_PWR_DRIVE_TYPE")));
    }

    @Test
    @DisplayName("code 格式非法 -> 812127")
    void invalidFormatRejected() {
        assertThrows(OptionCodeFormatInvalidException.class, () -> optionCodeAppService.createOptionCode(
                createCmd("CLR_BLACK", "OF_EXT_BODY_COLOR")));
    }

    @Test
    @DisplayName("格式合法但族主干不一致 -> 812128")
    void familyMismatchRejected() {
        assertThrows(OptionCodeFamilyPrefixMismatchException.class, () -> optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_WHEEL_BLACK", "OF_EXT_BODY_COLOR")));
    }

    @Test
    @DisplayName("code 全局重复 -> 沿用 812101 重复语义")
    void duplicateRejected() {
        optionCodeAppService.createOptionCode(createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"));

        assertThrows(DuplicateCodeException.class, () -> optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR")));
    }

    @Test
    @DisplayName("合法创建 -> 主表、history、outbox 三写一致，快照保留完整 code")
    void validCreateWritesMainHistoryOutbox() {
        OptionCodeDto dto = optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"));

        assertNotNull(dto.getId());
        assertEquals("OC_EXT_BODY_COLOR_BLACK", dto.getCode());
        assertEquals("OF_EXT_BODY_COLOR", dto.getOptionFamilyCode());

        // 主表
        OptionCodePo po = optionCodeMapper.selectOne(new LambdaQueryWrapper<OptionCodePo>()
                .eq(OptionCodePo::getCode, "OC_EXT_BODY_COLOR_BLACK"));
        assertNotNull(po, "主表应写入");
        assertEquals("OF_EXT_BODY_COLOR", po.getOptionFamilyCode());

        // history
        Long historyCount = optionCodeHistoryMapper.selectCount(
                new LambdaQueryWrapper<OptionCodeHistoryPo>()
                        .eq(OptionCodeHistoryPo::getCode, "OC_EXT_BODY_COLOR_BLACK"));
        assertTrue(historyCount > 0, "history 应写入 CREATE 快照");

        // outbox：created 事件，payload 保留完整 code
        OutboxPo outbox = outboxMapper.selectOne(new LambdaQueryWrapper<OutboxPo>()
                .eq(OutboxPo::getAggregateType, "OPTION_CODE")
                .eq(OutboxPo::getEventType, "mdm.product.optionCode.created")
                .eq(OutboxPo::getAggregateId, "OC_EXT_BODY_COLOR_BLACK"));
        assertNotNull(outbox, "outbox 应写入 created 事件");
        assertTrue(outbox.getPayload().contains("OC_EXT_BODY_COLOR_BLACK"), "事件 payload 应保留完整 code");
    }

    @Test
    @DisplayName("企业扩展族下新建扩展值成功")
    void extensionFamilyCreateSucceeds() {
        OptionCodeDto dto = optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_X_SPECIAL_PAINT_MATTE_GRAY", "OF_EXT_X_SPECIAL_PAINT"));

        assertEquals("OC_EXT_X_SPECIAL_PAINT_MATTE_GRAY", dto.getCode());
    }

    @Test
    @DisplayName("更新不改变 code（code 不可变），legacy 非 code 更新不被追溯阻断")
    void legacyOrdinaryUpdateNotBlockedAndCodeImmutable() {
        OptionCodeDto created = optionCodeAppService.createOptionCode(
                createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"));

        // 模拟 legacy：直接改库把 code 改成非 OC_* 格式（绕过创建校验，模拟存量）
        optionCodeMapper.update(null, new LambdaUpdateWrapper<OptionCodePo>()
                .eq(OptionCodePo::getCode, created.getCode())
                .set(OptionCodePo::getCode, "CLR_RED"));

        // legacy code 普通更新（仅改名称）不被追溯阻断，code 保持不变
        OptionCodeDto updated = optionCodeAppService.updateOptionCode(OptionCodeUpdateCmd.builder()
                .code("CLR_RED").name("Red").nameLocal("红色")
                .modifyBy("test").build());

        assertEquals("Red", updated.getName());
        assertEquals("CLR_RED", updated.getCode());
        assertEquals("OF_EXT_BODY_COLOR", updated.getOptionFamilyCode());
    }

    @Test
    @DisplayName("标准族合法更新保持 code 不变并发布 updated 事件")
    void standardUpdateKeepsCodeAndPublishesEvent() {
        optionCodeAppService.createOptionCode(createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"));

        OptionCodeDto updated = optionCodeAppService.updateOptionCode(OptionCodeUpdateCmd.builder()
                .code("OC_EXT_BODY_COLOR_BLACK").name("Black Pearl").nameLocal("珍珠黑")
                .modifyBy("test").build());

        assertEquals("OC_EXT_BODY_COLOR_BLACK", updated.getCode());
        OutboxPo outbox = outboxMapper.selectOne(new LambdaQueryWrapper<OutboxPo>()
                .eq(OutboxPo::getAggregateType, "OPTION_CODE")
                .eq(OutboxPo::getEventType, "mdm.product.optionCode.updated")
                .eq(OutboxPo::getAggregateId, "OC_EXT_BODY_COLOR_BLACK"));
        assertNotNull(outbox, "更新应写入 updated 事件");
    }
}
