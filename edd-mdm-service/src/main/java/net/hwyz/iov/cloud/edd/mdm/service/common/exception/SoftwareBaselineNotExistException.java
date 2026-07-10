package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineNotExistException extends MdmBaseException {

    public SoftwareBaselineNotExistException(String code) {
        super(MdmErrorCode.SW_BASELINE_NOT_EXIST, String.format("软件基线不存在: %s", code));
        log.warn("软件基线不存在: {}", code);
    }
}
