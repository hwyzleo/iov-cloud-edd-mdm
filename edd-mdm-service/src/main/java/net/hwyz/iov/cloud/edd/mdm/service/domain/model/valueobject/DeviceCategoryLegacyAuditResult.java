package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备类别存量治理审计报告（CR-037 §8）
 * <p>
 * 按 code、双语名称、目录 aliases 和规格化后缀识别存量候选，
 * 供治理人员人工确认 legacy_code → standard_code 映射后执行收敛，不自动改写。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryLegacyAuditResult {

    /**
     * 审计的现存类别总数
     */
    private int totalExisting;

    /**
     * 发现类型：LEGACY_CODE / NAME_DUPLICATE / SPEC_CODE / NEAR_SYNONYM
     */
    public static final String TYPE_LEGACY_CODE = "LEGACY_CODE";
    public static final String TYPE_NAME_DUPLICATE = "NAME_DUPLICATE";
    public static final String TYPE_SPEC_CODE = "SPEC_CODE";
    public static final String TYPE_NEAR_SYNONYM = "NEAR_SYNONYM";

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
         * 现存类别 code
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
         * 建议归一到标准类别 code（若有；无则 null）
         */
        private String suggestedStandardCode;
    }
}
