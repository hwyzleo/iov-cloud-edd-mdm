package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线投影冲突异常（同一型式判定失败）
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineProjectionConflictException extends MdmBaseException {

    public TaBaselineProjectionConflictException(String swinCode, String diff) {
        super(MdmErrorCode.TA_BASELINE_PROJECTION_CONFLICT,
                String.format("SWIN %s 型批相关投影不唯一（同一型式判定失败）: %s", swinCode, diff));
        log.warn("TA基线投影冲突: swinCode={}, diff={}", swinCode, diff);
    }
}
