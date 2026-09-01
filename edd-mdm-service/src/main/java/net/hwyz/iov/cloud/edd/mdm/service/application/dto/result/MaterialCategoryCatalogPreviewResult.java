package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料品类标准目录预检结果（CR-039 §6）
 * <p>
 * 返回目录版本、total=101、level1Count=4、level2Count=19、level3Count=78、catalogStatus，
 * 以及每项 Initialized/Missing/Conflict 与冲突明细，供 MDM-Admin 执行初始化前预览。
 * 目录非法时 catalogStatus=INVALID 并携带 error，禁止 bootstrap。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryCatalogPreviewResult {

    /**
     * 目录状态：VALID / INVALID
     */
    private String catalogStatus;

    /**
     * 标准目录版本
     */
    private Integer catalogVersion;

    /**
     * 标准目录总条目数（固定 101）
     */
    private int total;

    /**
     * L1 数量（固定 4）
     */
    private int level1Count;

    /**
     * L2 数量（固定 19）
     */
    private int level2Count;

    /**
     * L3 数量（固定 78）
     */
    private int level3Count;

    /**
     * 已存在且与目录完全一致（无需处理）
     */
    private int initialized;

    /**
     * 尚未初始化（将创建）
     */
    private int missing;

    /**
     * 已存在但名称/父级/语义冲突（不会覆盖）
     */
    private int conflicted;

    /**
     * 每项初始化状态明细（Initialized / Missing / Conflict）
     */
    @Builder.Default
    private List<ItemStatus> items = new ArrayList<>();

    /**
     * 冲突明细（code: 原因）
     */
    @Builder.Default
    private List<String> conflicts = new ArrayList<>();

    /**
     * 目录非法原因（catalogStatus=INVALID 时）
     */
    private String error;

    public void addConflict(String conflict) {
        if (this.conflicts == null) {
            this.conflicts = new ArrayList<>();
        }
        this.conflicts.add(conflict);
    }

    public void addItem(ItemStatus item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    /**
     * 单项目录条目初始化状态
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemStatus {

        public static final String STATUS_INITIALIZED = "Initialized";
        public static final String STATUS_MISSING = "Missing";
        public static final String STATUS_CONFLICT = "Conflict";

        /**
         * 目录条目 code
         */
        private String code;

        /**
         * 层级（1/2/3）
         */
        private Integer level;

        /**
         * 初始化状态：Initialized / Missing / Conflict
         */
        private String status;
    }
}
