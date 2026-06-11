package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件号系统发号冲突异常
 * CR-023 新增
 */
@Slf4j
@Getter
public class PartCodeGenConflictException extends MdmBaseException {

    private final String conflictCode;

    public PartCodeGenConflictException(String conflictCode) {
        super(MdmErrorCode.PART_CODE_GEN_CONFLICT, String.format("系统发号code撞UK，重试仍冲突: %s", conflictCode));
        this.conflictCode = conflictCode;
        log.warn("系统发号code撞UK，重试仍冲突: {}", conflictCode);
    }

    public PartCodeGenConflictException(String conflictCode, Throwable cause) {
        super(MdmErrorCode.PART_CODE_GEN_CONFLICT, String.format("系统发号code撞UK，重试仍冲突: %s", conflictCode));
        this.conflictCode = conflictCode;
        log.warn("系统发号code撞UK，重试仍冲突: {}", conflictCode, cause);
    }
}
