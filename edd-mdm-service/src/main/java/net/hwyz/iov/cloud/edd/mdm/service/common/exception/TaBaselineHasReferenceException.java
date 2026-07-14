package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线被引用异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineHasReferenceException extends MdmBaseException {

    public TaBaselineHasReferenceException(String taBaselineCode, String refBy) {
        super(MdmErrorCode.TA_BASELINE_HAS_REFERENCE,
                String.format("TA基线 %s 被 %s 引用，禁止删除", taBaselineCode, refBy));
        log.warn("TA基线被引用: code={}, refBy={}", taBaselineCode, refBy);
    }
}
