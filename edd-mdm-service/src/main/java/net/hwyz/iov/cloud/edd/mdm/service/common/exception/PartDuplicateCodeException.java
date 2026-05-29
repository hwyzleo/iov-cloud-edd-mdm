package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 code 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartDuplicateCodeException extends MdmBaseException {

    private final String conflictCode;
    private final String existingStatus;

    public PartDuplicateCodeException(String conflictCode, String existingStatus) {
        super(MdmErrorCode.PART_CODE_EXIST, String.format("零件 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("零件code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus);
    }

    public PartDuplicateCodeException(String conflictCode, String existingStatus, Throwable cause) {
        super(MdmErrorCode.PART_CODE_EXIST, String.format("零件 code 已存在: %s（已占用记录状态: %s）", conflictCode, existingStatus));
        this.conflictCode = conflictCode;
        this.existingStatus = existingStatus;
        log.warn("零件code已存在: {}，已占用记录状态: {}", conflictCode, existingStatus, cause);
    }
}
