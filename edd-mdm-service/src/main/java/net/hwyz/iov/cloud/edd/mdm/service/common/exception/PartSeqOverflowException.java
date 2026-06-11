package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件号流水序号溢出异常
 * CR-023 新增
 */
@Slf4j
@Getter
public class PartSeqOverflowException extends MdmBaseException {

    private final long currentSeq;

    public PartSeqOverflowException(long currentSeq) {
        super(MdmErrorCode.PART_SEQ_OVERFLOW, String.format("零件号流水序号溢出: %d", currentSeq));
        this.currentSeq = currentSeq;
        log.warn("零件号流水序号溢出: {}", currentSeq);
    }

    public PartSeqOverflowException(long currentSeq, Throwable cause) {
        super(MdmErrorCode.PART_SEQ_OVERFLOW, String.format("零件号流水序号溢出: %d", currentSeq));
        this.currentSeq = currentSeq;
        log.warn("零件号流水序号溢出: {}", currentSeq, cause);
    }
}
