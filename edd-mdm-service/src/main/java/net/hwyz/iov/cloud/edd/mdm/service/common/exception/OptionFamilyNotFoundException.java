package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 选项族不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class OptionFamilyNotFoundException extends MdmBaseException {

    public OptionFamilyNotFoundException(String message) {
        super(MdmErrorCode.RECORD_NOT_EXIST, message);
        log.warn("选项族不存在: {}", message);
    }

    public OptionFamilyNotFoundException(String message, Throwable cause) {
        super(MdmErrorCode.RECORD_NOT_EXIST, message);
        log.warn("选项族不存在: {}", message, cause);
    }
}
