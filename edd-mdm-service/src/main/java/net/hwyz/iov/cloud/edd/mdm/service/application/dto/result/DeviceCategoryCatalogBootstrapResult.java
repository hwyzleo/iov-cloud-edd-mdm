package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 设备类别标准目录 Bootstrap 导入结果（CR-037 §6）
 * <p>
 * 统计 created/skipped/conflicted/failed，记录目录版本、操作者、开始/结束时间与冲突/失败明细。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryCatalogBootstrapResult {

    /**
     * 标准目录版本
     */
    private Integer catalogVersion;

    /**
     * 初始化执行人（系统身份）
     */
    private String operator;

    /**
     * 开始时间
     */
    private Date startedAt;

    /**
     * 结束时间
     */
    private Date finishedAt;

    /**
     * 成功创建（含 history 与 outbox）
     */
    private int created;

    /**
     * 已存在且完全一致，跳过
     */
    private int skipped;

    /**
     * 已存在但名称/语义不一致，记录冲突并跳过（不覆盖业务数据）
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
