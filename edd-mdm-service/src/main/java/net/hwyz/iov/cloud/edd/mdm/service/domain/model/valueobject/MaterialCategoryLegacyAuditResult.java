package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料品类存量治理审计报告（CR-039 §9）
 * <p>
 * 按 code、双语名称、父子前缀、parentCode 链、层级深度与 Part 引用识别存量候选，
 * 供治理人员人工确认 legacy_category_code → standard_l3_code 映射后执行收敛，不自动改写、不自动合并、不自动失效。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryLegacyAuditResult {

    /**
     * 发现类型：LEGACY_CODE / NAME_DUPLICATE / PARENT_PREFIX_INCONSISTENT / ORPHAN / DEPTH_EXCEEDED / CROSS_SCOPE / PART_NON_LEAF_REF
     */
    public static final String TYPE_LEGACY_CODE = "LEGACY_CODE";
    public static final String TYPE_NAME_DUPLICATE = "NAME_DUPLICATE";
    public static final String TYPE_PARENT_PREFIX_INCONSISTENT = "PARENT_PREFIX_INCONSISTENT";
    public static final String TYPE_ORPHAN = "ORPHAN";
    public static final String TYPE_DEPTH_EXCEEDED = "DEPTH_EXCEEDED";
    public static final String TYPE_CROSS_SCOPE = "CROSS_SCOPE";
    public static final String TYPE_PART_NON_LEAF_REF = "PART_NON_LEAF_REF";

    /**
     * 审计的现存品类总数
     */
    private int totalExisting;

    /**
     * 审计的现存 Part 引用数（按 categoryCode 去重）
     */
    private int totalPartCategoryRefs;

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
         * 现存品类 code
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
         * 建议归一到标准 L3 code（若有；无则 null）
         */
        private String suggestedStandardCode;
    }
}
