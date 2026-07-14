package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线批量补发命中数量超过配置上限异常
 * <p>
 * 落地 MDM-DSN-CR-031（US-130）
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineRepublishBatchLimitExceededException extends MdmBaseException {

    public TaBaselineRepublishBatchLimitExceededException(long hitCount, long maxBatchSize) {
        super(MdmErrorCode.TA_BASELINE_REPUBLISH_BATCH_LIMIT_EXCEEDED,
                String.format("TA基线批量补发命中数量 %d 超过配置上限 %d", hitCount, maxBatchSize));
        log.warn("TA基线批量补发命中数量超过配置上限: hitCount={}, maxBatchSize={}", hitCount, maxBatchSize);
    }
}
