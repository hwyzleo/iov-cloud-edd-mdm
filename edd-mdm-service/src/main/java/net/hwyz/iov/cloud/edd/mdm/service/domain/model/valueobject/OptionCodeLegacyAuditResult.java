package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项码存量治理审计报告（CR-040 §7）
 * <p>
 * 按 code 格式、字符集、VALUE、长度与所属族派生主干一致性识别存量候选，
 * 供治理人员人工确认后按「新建规范码 → 迁移绑定 → 失效旧码」收敛，
 * 不自动改写、不自动迁移、不自动失效。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionCodeLegacyAuditResult {

    /**
     * 发现类型：LEGACY_CODE / ILLEGAL_CHARS / EMPTY_VALUE / LENGTH_EXCEEDED /
     * FAMILY_PREFIX_MISMATCH / CROSS_FAMILY_CONFLICT
     */
    public static final String TYPE_LEGACY_CODE = "LEGACY_CODE";
    public static final String TYPE_ILLEGAL_CHARS = "ILLEGAL_CHARS";
    public static final String TYPE_EMPTY_VALUE = "EMPTY_VALUE";
    public static final String TYPE_LENGTH_EXCEEDED = "LENGTH_EXCEEDED";
    public static final String TYPE_FAMILY_PREFIX_MISMATCH = "FAMILY_PREFIX_MISMATCH";
    public static final String TYPE_CROSS_FAMILY_CONFLICT = "CROSS_FAMILY_CONFLICT";

    /**
     * 审计的现存选项码总数
     */
    private int totalExisting;

    /**
     * 逐条发现
     */
    @Builder.Default
    private List<Finding> findings = new ArrayList<>();

    public void addFinding(Finding finding) {
        if (this.findings == null) {
            this.findings = new ArrayList<>();
        }
        this.findings.add(finding);
    }

    public long countByType(String type) {
        return findings.stream().filter(f -> type.equals(f.getType())).count();
    }

    /**
     * 单条发现
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Finding {

        /**
         * 现存选项码 code
         */
        private String code;

        /**
         * 发现类型（TYPE_*）
         */
        private String type;

        /**
         * 说明
         */
        private String detail;

        /**
         * 建议的所属选项族 code（如有；无则 null）
         */
        private String suggestedOptionFamilyCode;
    }
}
