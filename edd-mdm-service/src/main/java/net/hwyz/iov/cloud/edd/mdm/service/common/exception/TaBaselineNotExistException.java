package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineNotExistException extends MdmBaseException {

    public TaBaselineNotExistException(String taBaselineCode) {
        super(MdmErrorCode.TA_BASELINE_NOT_EXIST,
                String.format("TA基线 %s 不存在", taBaselineCode));
        log.warn("TA基线不存在: {}", taBaselineCode);
    }
}
