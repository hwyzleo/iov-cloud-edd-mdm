package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration 序号溢出异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class ConfigurationSeqOverflowException extends MdmBaseException {

    public ConfigurationSeqOverflowException(String message) {
        super(MdmErrorCode.CONFIGURATION_SEQ_OVERFLOW, message);
        log.warn("Configuration序号溢出: {}", message);
    }

    public ConfigurationSeqOverflowException(String message, Throwable cause) {
        super(MdmErrorCode.CONFIGURATION_SEQ_OVERFLOW, message);
        log.warn("Configuration序号溢出: {}", message, cause);
    }
}
