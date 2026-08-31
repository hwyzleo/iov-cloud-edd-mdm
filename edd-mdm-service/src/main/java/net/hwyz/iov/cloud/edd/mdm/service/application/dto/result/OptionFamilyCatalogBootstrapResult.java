package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项族标准目录 Bootstrap 导入结果（CR-035 §5.3）
 * <p>
 * 统计 created/skipped/conflicted/failed，并记录冲突/失败明细。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionFamilyCatalogBootstrapResult {

    /**
     * 成功创建（含 history 与 outbox）
     */
    private int created;

    /**
     * 已存在且完全一致，跳过
     */
    private int skipped;

    /**
     * 已存在但名称/category 不一致，记录冲突并跳过（不覆盖业务数据）
     */
    private int conflicted;

    /**
     * 单条创建失败
     */
    private int failed;

    /**
     * 冲突/失败明细（code: 原因）
     */
    @Builder.Default
    private List<String> details = new ArrayList<>();

    public void incrementCreated() {
        this.created++;
    }

    public void incrementSkipped() {
        this.skipped++;
    }

    public void incrementConflicted() {
        this.conflicted++;
    }

    public void incrementFailed() {
        this.failed++;
    }

    public void addDetail(String detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }
}
