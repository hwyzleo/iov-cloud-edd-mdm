package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SwinDefinitionNotExistException extends MdmBaseException {

    public SwinDefinitionNotExistException(String swinCode) {
        super(MdmErrorCode.SWIN_DEFINITION_NOT_EXIST, String.format("SWIN定义不存在: %s", swinCode));
        log.warn("SWIN定义不存在: {}", swinCode);
    }

    public SwinDefinitionNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.SWIN_DEFINITION_NOT_EXIST, message);
        log.warn("SWIN定义不存在: {}", message, cause);
    }
}
