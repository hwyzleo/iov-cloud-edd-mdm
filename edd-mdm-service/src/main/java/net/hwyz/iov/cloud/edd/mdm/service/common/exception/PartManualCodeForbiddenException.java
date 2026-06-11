package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 无权限手动指定code异常
 * CR-023 新增
 */
@Slf4j
@Getter
public class PartManualCodeForbiddenException extends MdmBaseException {

    public PartManualCodeForbiddenException() {
        super(MdmErrorCode.PART_MANUAL_CODE_FORBIDDEN, "无mdm:material:part:code:manual权限尝试手动指定code");
        log.warn("无权限手动指定code");
    }

    public PartManualCodeForbiddenException(Throwable cause) {
        super(MdmErrorCode.PART_MANUAL_CODE_FORBIDDEN, "无mdm:material:part:code:manual权限尝试手动指定code");
        log.warn("无权限手动指定code", cause);
    }
}
