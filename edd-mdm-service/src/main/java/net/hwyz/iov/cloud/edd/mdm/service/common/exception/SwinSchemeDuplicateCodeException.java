package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN编码方案 code 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinSchemeDuplicateCodeException extends MdmBaseException {

    private final String conflictCode;
    private final String existingStatus;

    public SwinSchemeDuplicateCodeException(String conflictCode, String existingStatus) {
        super(MdmErrorCode.SWIN_SCHEME_CODE_EXIST,
                String.format("SWIN编码方案 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("SWIN编码方案code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus);
    }

    public SwinSchemeDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.SWIN_SCHEME_CODE_EXIST,
                String.format("SWIN编码方案 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("SWIN编码方案code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus, cause);
    }
}
