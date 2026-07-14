package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义批量补发命中数量超过配置上限异常
 * <p>
 * 落地 MDM-DSN-CR-031（US-132）
 *
 * @author hwyz_leo
 */
@Slf4j
public class SwinDefinitionRepublishBatchLimitExceededException extends MdmBaseException {

    public SwinDefinitionRepublishBatchLimitExceededException(long hitCount, long maxBatchSize) {
        super(MdmErrorCode.SWIN_DEFINITION_REPUBLISH_BATCH_LIMIT_EXCEEDED,
                String.format("SWIN定义批量补发命中数量 %d 超过配置上限 %d", hitCount, maxBatchSize));
        log.warn("SWIN定义批量补发命中数量超过配置上限: hitCount={}, maxBatchSize={}", hitCount, maxBatchSize);
    }
}
