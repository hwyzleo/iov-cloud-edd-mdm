package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * SWIN编码方案不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SwinSchemeNotExistException extends MdmBaseException {

    public SwinSchemeNotExistException(String code) {
        super(MdmErrorCode.SWIN_SCHEME_NOT_EXIST, String.format("SWIN编码方案不存在: %s", code));
        log.warn("SWIN编码方案不存在: {}", code);
    }

    public SwinSchemeNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.SWIN_SCHEME_NOT_EXIST, message);
        log.warn("SWIN编码方案不存在: {}", message, cause);
    }
}
