package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线源基线非RELEASED异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineSourceNotReleasedException extends MdmBaseException {

    public TaBaselineSourceNotReleasedException(String baselineCode) {
        super(MdmErrorCode.TA_BASELINE_SOURCE_NOT_RELEASED,
                String.format("参与卷积的SoftwareBaseline %s 非RELEASED", baselineCode));
        log.warn("TA基线源基线非RELEASED: {}", baselineCode);
    }
}
