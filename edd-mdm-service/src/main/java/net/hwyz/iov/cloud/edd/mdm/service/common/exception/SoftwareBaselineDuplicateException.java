package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线 code 或 (锚点+版本) 重复异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineDuplicateException extends MdmBaseException {

    public SoftwareBaselineDuplicateException(String message) {
        super(MdmErrorCode.SW_BASELINE_DUPLICATE, message);
        log.warn("软件基线重复: {}", message);
    }
}
