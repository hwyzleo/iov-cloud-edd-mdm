package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeLegacyAuditResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionCodeCodePolicy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选项码存量治理审计（CR-040 §7）
 * <p>
 * 识别存量 OptionCode 中不符合 OC_* 统一编码格式的 legacy code、含非法字符 / 连续下划线 /
 * 空 VALUE / 长度超限的 code，以及 code 与所属 OptionFamily 派生主干不一致 / 疑似跨族归属的记录，
 * 输出审计报告供治理人员人工确认后按「新建规范码 → 迁移绑定 → 校验 → 失效旧码」收敛；
 * 只生成报告，不修改主表、history、绑定关系或下游数据。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OptionCodeLegacyAudit {

    private final OptionCodeCodePolicy codePolicy;

    /**
     * 对现存选项码执行存量治理审计
     *
     * @param existing 现存选项码（row_valid=1）
     * @return 审计报告
     */
    public OptionCodeLegacyAuditResult audit(List<OptionCode> existing) {
        OptionCodeLegacyAuditResult result = OptionCodeLegacyAuditResult.builder()
                .totalExisting(existing == null ? 0 : existing.size())
                .build();

        if (existing == null || existing.isEmpty()) {
            log.info("Option Code 存量治理审计完成: 无现存选项码");
            return result;
        }

        // 族 code → 派生主干映射（用于跨族疑似冲突识别）
        Map<String, String> familyStemByCode = new HashMap<>();
        for (OptionCode oc : existing) {
            String stem = codePolicy.deriveExpectedStem(oc.getOptionFamilyCode());
            if (stem != null) {
                familyStemByCode.putIfAbsent(oc.getOptionFamilyCode(), stem);
            }
        }

        for (OptionCode oc : existing) {
            String code = oc.getCode();
            String familyCode = oc.getOptionFamilyCode();

            if (code == null || code.isBlank()) {
                result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_LEGACY_CODE,
                        "code 为空，无法识别，建议人工确认", null));
                continue;
            }

            // 1) 长度超限
            if (code.length() > OptionCodeCodePolicy.CODE_MAX_LENGTH) {
                result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_LENGTH_EXCEEDED,
                        "code 长度超过 " + OptionCodeCodePolicy.CODE_MAX_LENGTH + " 字符上限（当前 " + code.length() + "）", null));
            }

            if (!codePolicy.isValidFormat(code)) {
                // 2) 非法字符 / 连续下划线 / 首尾下划线
                if (codePolicy.containsInvalidCharset(code) || codePolicy.hasConsecutiveOrEdgeUnderscores(code)) {
                    result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_ILLEGAL_CHARS,
                            "包含非法字符（仅允许大写字母、数字与单下划线，禁止连续/首尾下划线、空格、连字符、非 ASCII）", null));
                }
                // 3) 空 VALUE
                if (codePolicy.isPrefixOnly(code)) {
                    result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_EMPTY_VALUE,
                            "缺少 VALUE 段（OC_<CATEGORY_PREFIX>_<FAMILY_SEMANTIC>_<VALUE> 中 VALUE 为空）", null));
                }
                // 4) legacy code
                result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_LEGACY_CODE,
                        "不符合 OC_* 统一编码格式，建议人工确认 VALUE 语义后按规范新建并迁移绑定", null));
            } else {
                // 5) 与所属族派生主干不一致
                String expectedStem = codePolicy.deriveExpectedStem(familyCode);
                boolean familyMatched = expectedStem != null
                        && code.startsWith(expectedStem)
                        && code.length() > expectedStem.length();
                if (!familyMatched) {
                    result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_FAMILY_PREFIX_MISMATCH,
                            "code 派生主干与所属选项族不一致: optionFamilyCode=" + familyCode
                                    + ", expectedStem=" + (expectedStem == null ? "(legacy 族无法派生)" : expectedStem), null));
                }
                // 6) 跨族疑似冲突：code 主干匹配到数据集内其他族
                for (Map.Entry<String, String> e : familyStemByCode.entrySet()) {
                    if (!e.getKey().equals(familyCode)
                            && code.startsWith(e.getValue())
                            && code.length() > e.getValue().length()) {
                        result.addFinding(finding(code, OptionCodeLegacyAuditResult.TYPE_CROSS_FAMILY_CONFLICT,
                                "code 主干与族 " + e.getKey() + " 派生主干一致，疑似跨族归属错误（当前归属 " + familyCode + "）",
                                e.getKey()));
                        break;
                    }
                }
            }
        }

        log.info("Option Code 存量治理审计完成: 现存={}, legacy={}, 非法字符={}, 空VALUE={}, 长度超限={}, "
                        + "族主干不一致={}, 跨族疑似冲突={}",
                result.getTotalExisting(),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_LEGACY_CODE),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_ILLEGAL_CHARS),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_EMPTY_VALUE),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_LENGTH_EXCEEDED),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_FAMILY_PREFIX_MISMATCH),
                result.countByType(OptionCodeLegacyAuditResult.TYPE_CROSS_FAMILY_CONFLICT));
        return result;
    }

    private OptionCodeLegacyAuditResult.Finding finding(String code, String type, String detail,
                                                        String suggestedOptionFamilyCode) {
        return OptionCodeLegacyAuditResult.Finding.builder()
                .code(code).type(type).detail(detail).suggestedOptionFamilyCode(suggestedOptionFamilyCode)
                .build();
    }
}
