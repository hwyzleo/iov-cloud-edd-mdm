package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线序列号溢出异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineSeqOverflowException extends MdmBaseException {

    public TaBaselineSeqOverflowException(String seqName) {
        super(MdmErrorCode.TA_BASELINE_SEQ_OVERFLOW,
                String.format("TA基线序列号溢出: %s", seqName));
        log.warn("TA基线序列号溢出: {}", seqName);
    }
}
