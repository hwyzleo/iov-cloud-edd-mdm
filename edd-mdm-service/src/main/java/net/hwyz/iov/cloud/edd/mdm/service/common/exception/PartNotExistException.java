package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 零件不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class PartNotExistException extends MdmBaseException {

    public PartNotExistException(String message) {
        super(MdmErrorCode.RECORD_NOT_EXIST, message);
        log.warn("零件不存在: {}", message);
    }

    public PartNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.RECORD_NOT_EXIST, message);
        log.warn("零件不存在: {}", message, cause);
    }
}
