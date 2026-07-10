package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * RXSWIN序列溢出异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class RxswinSeqOverflowException extends MdmBaseException {

    private final long currentValue;

    public RxswinSeqOverflowException(long currentValue) {
        super(MdmErrorCode.RXSWIN_SEQ_OVERFLOW,
                String.format("RXSWIN 16位十进制序列溢出: next_seq=%d 超过上限 9999999999999999", currentValue));
        this.currentValue = currentValue;
        log.warn("RXSWIN序列溢出: next_seq={}", currentValue);
    }
}
