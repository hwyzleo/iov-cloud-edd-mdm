package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义 swinCode 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinDefinitionDuplicateSwinCodeException extends MdmBaseException {

    private final String conflictSwinCode;
    private final String existingStatus;

    public SwinDefinitionDuplicateSwinCodeException(String conflictSwinCode, String existingStatus) {
        super(MdmErrorCode.SWIN_DEFINITION_SWIN_CODE_EXIST,
                String.format("SWIN定义 swinCode 已存在: %s（已占用记录状态: %s）", conflictSwinCode, existingStatus));
        this.conflictSwinCode = conflictSwinCode;
        this.existingStatus = existingStatus;
        log.warn("SWIN定义swinCode已存在: {}，已占用记录状态: {}", conflictSwinCode, existingStatus);
    }

    public SwinDefinitionDuplicateSwinCodeException(String conflictSwinCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.SWIN_DEFINITION_SWIN_CODE_EXIST,
                String.format("SWIN定义 swinCode 已存在: %s（已占用记录状态: %s）", conflictSwinCode, existingStatus));
        this.conflictSwinCode = conflictSwinCode;
        this.existingStatus = existingStatus;
        log.warn("SWIN定义swinCode已存在: {}，已占用记录状态: {}", conflictSwinCode, existingStatus, cause);
    }
}
