package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 软件基线批量补发结果
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineRepublishBatchResult {

    /**
     * 命中数量
     */
    private Long hitCount;

    /**
     * 已发布数量
     */
    private Long publishedCount;

    /**
     * 失败数量
     */
    private Long failedCount;

    /**
     * 批次ID
     */
    private String batchId;
}
