package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线已冻结异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineFrozenException extends MdmBaseException {

    public TaBaselineFrozenException(String taBaselineCode) {
        super(MdmErrorCode.TA_BASELINE_FROZEN,
                String.format("TA基线 %s 已冻结，不可修改", taBaselineCode));
        log.warn("TA基线已冻结: {}", taBaselineCode);
    }
}
