package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TA基线批量补发结果（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-130）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineRepublishBatchResult {

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
