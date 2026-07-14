package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线批量补发数量超限异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineRepublishBatchLimitExceededException extends MdmBaseException {

    public SoftwareBaselineRepublishBatchLimitExceededException(long hitCount, long maxSize) {
        super(MdmErrorCode.SW_BASELINE_REPUBLISH_BATCH_LIMIT_EXCEEDED,
                String.format("单次批量补发命中数量(%d)超过配置上限(%d)", hitCount, maxSize));
        log.warn("批量补发数量超限: hitCount={}, maxSize={}", hitCount, maxSize);
    }
}
