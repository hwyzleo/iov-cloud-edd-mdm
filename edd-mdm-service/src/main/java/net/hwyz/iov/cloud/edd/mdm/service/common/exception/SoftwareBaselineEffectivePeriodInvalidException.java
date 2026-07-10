package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线生效期非法异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineEffectivePeriodInvalidException extends MdmBaseException {

    public SoftwareBaselineEffectivePeriodInvalidException() {
        super(MdmErrorCode.SW_BASELINE_EFFECTIVE_PERIOD_INVALID, "生效期非法（effectiveFrom > effectiveTo）");
        log.warn("软件基线生效期非法");
    }
}
