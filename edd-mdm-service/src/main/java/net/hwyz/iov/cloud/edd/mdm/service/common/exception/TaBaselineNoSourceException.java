package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线无源基线异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineNoSourceException extends MdmBaseException {

    public TaBaselineNoSourceException(String anchorType, String anchorCode) {
        super(MdmErrorCode.TA_BASELINE_NO_SOURCE,
                String.format("锚点 %s:%s 范围内无RELEASED SoftwareBaseline", anchorType, anchorCode));
        log.warn("锚点范围内无RELEASED SoftwareBaseline: {}:{}", anchorType, anchorCode);
    }
}
