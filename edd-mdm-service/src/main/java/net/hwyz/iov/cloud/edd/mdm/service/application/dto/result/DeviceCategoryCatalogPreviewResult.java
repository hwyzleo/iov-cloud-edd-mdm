package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备类别标准目录预检结果（CR-037 §6.1）
 * <p>
 * 返回目录版本、标准设备族数量、已初始化数量及冲突明细，供 MDM-Admin 执行初始化前预览。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryCatalogPreviewResult {

    /**
     * 标准目录版本
     */
    private Integer catalogVersion;

    /**
     * 标准目录设备族数量（固定 24）
     */
    private int standardFamilyCount;

    /**
     * 已存在且与目录完全一致（无需处理）
     */
    private int initialized;

    /**
     * 尚未初始化（将创建）
     */
    private int missing;

    /**
     * 已存在但名称/语义冲突（不会覆盖）
     */
    private int conflicted;

    /**
     * 冲突明细（code: 原因）
     */
    @Builder.Default
    private List<String> conflicts = new ArrayList<>();

    public void addConflict(String conflict) {
        if (this.conflicts == null) {
            this.conflicts = new ArrayList<>();
        }
        this.conflicts.add(conflict);
    }
}
