package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Variant code 长度超限异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class VariantCodeTooLongException extends MdmBaseException {

    public VariantCodeTooLongException(String message) {
        super(MdmErrorCode.VARIANT_CODE_TOO_LONG, message);
        log.warn("Variant code长度超限: {}", message);
    }

    public VariantCodeTooLongException(String message, Throwable cause) {
        super(MdmErrorCode.VARIANT_CODE_TOO_LONG, message);
        log.warn("Variant code长度超限: {}", message, cause);
    }
}
