package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionCodeCodePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选项码存量治理审计单元测试（CR-040 §7）
 *
 * @author hwyz_leo
 */
@DisplayName("OptionCodeLegacyAudit 测试")
class OptionCodeLegacyAuditTest {

    private OptionCodeLegacyAudit audit;

    @BeforeEach
    void setUp() {
        audit = new OptionCodeLegacyAudit(new OptionCodeCodePolicy());
    }

    private OptionCode oc(String code, String optionFamilyCode) {
        return OptionCode.builder()
                .code(code).name(code).nameLocal(code)
                .optionFamilyCode(optionFamilyCode)
                .rowValid(true)
                .build();
    }

    @Test
    @DisplayName("识别 legacy、非法字符、空 VALUE、长度超限、族不一致与跨族疑似冲突")
    void detectsAllFindingTypes() {
        List<OptionCode> existing = List.of(
                // 规范：族一致
                oc("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"),
                // legacy code（非 OC_* 格式）
                oc("CLR_RED", "OF_EXT_BODY_COLOR"),
                // legacy code + 非法字符（小写、连字符）
                oc("OC_EXT_BODY_COLOR-black", "OF_EXT_BODY_COLOR"),
                // 非法字符（非 ASCII）
                oc("OC_EXT_BODY_COLOR_黑色", "OF_EXT_BODY_COLOR"),
                // 连续下划线
                oc("OC_EXT__BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"),
                // 空 VALUE（仅前缀）
                oc("OC_EXT_", "OF_EXT_BODY_COLOR"),
                // 长度超限（> 64）
                oc("OC_EXT_BODY_COLOR_" + "A".repeat(50), "OF_EXT_BODY_COLOR"),
                // 格式合法但族主干不一致（跨族写错归属：实际应属 OF_PWR_DRIVE_TYPE）
                oc("OC_PWR_DRIVE_TYPE_AWD", "OF_EXT_BODY_COLOR")
        );

        OptionCodeLegacyAuditResult result = audit.audit(existing);

        assertEquals(8, result.getTotalExisting());
        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_LEGACY_CODE) >= 5,
                "应识别 legacy/非法字符/空VALUE/长度超限 code");
        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_ILLEGAL_CHARS) >= 4);
        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_EMPTY_VALUE) >= 1);
        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_LENGTH_EXCEEDED) >= 1);
        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_FAMILY_PREFIX_MISMATCH) >= 1);
    }

    @Test
    @DisplayName("识别跨族疑似冲突并给出建议族")
    void detectsCrossFamilyConflict() {
        List<OptionCode> existing = List.of(
                // 数据集内存在 OF_EXT_BODY_COLOR 与 OF_PWR_DRIVE_TYPE 两个族
                oc("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"),
                oc("OC_PWR_DRIVE_TYPE_AWD", "OF_PWR_DRIVE_TYPE"),
                // 疑似跨族：主干与 OF_PWR_DRIVE_TYPE 一致，但当前归属 OF_EXT_BODY_COLOR
                oc("OC_PWR_DRIVE_TYPE_AWD", "OF_EXT_BODY_COLOR")
        );

        OptionCodeLegacyAuditResult result = audit.audit(existing);

        long crossFamily = result.countByType(OptionCodeLegacyAuditResult.TYPE_CROSS_FAMILY_CONFLICT);
        assertTrue(crossFamily >= 1, "应识别跨族疑似冲突");
        result.getFindings().stream()
                .filter(f -> OptionCodeLegacyAuditResult.TYPE_CROSS_FAMILY_CONFLICT.equals(f.getType()))
                .findFirst()
                .ifPresent(f -> assertEquals("OF_PWR_DRIVE_TYPE", f.getSuggestedOptionFamilyCode()));
    }

    @Test
    @DisplayName("legacy 族（非 OF_*）下规范格式 code 标记为族主干不一致")
    void legacyFamilyMismatch() {
        List<OptionCode> existing = List.of(
                oc("OC_EXT_BODY_COLOR_BLACK", "COLOR")
        );

        OptionCodeLegacyAuditResult result = audit.audit(existing);

        assertTrue(result.countByType(OptionCodeLegacyAuditResult.TYPE_FAMILY_PREFIX_MISMATCH) >= 1);
    }

    @Test
    @DisplayName("全部规范且族一致时无发现")
    void cleanStateNoFindings() {
        List<OptionCode> existing = List.of(
                oc("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"),
                oc("OC_EXT_BODY_COLOR_PEARL_WHITE", "OF_EXT_BODY_COLOR"),
                oc("OC_PWR_DRIVE_TYPE_AWD", "OF_PWR_DRIVE_TYPE")
        );

        OptionCodeLegacyAuditResult result = audit.audit(existing);

        assertEquals(3, result.getTotalExisting());
        assertEquals(0, result.getFindings().size());
    }

    @Test
    @DisplayName("空存量返回空审计")
    void emptyExisting() {
        OptionCodeLegacyAuditResult result = audit.audit(List.of());
        assertEquals(0, result.getTotalExisting());
        assertEquals(0, result.getFindings().size());
    }
}
