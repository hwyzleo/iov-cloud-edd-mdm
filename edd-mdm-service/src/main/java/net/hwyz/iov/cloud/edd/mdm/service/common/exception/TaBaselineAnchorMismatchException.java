package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线锚点不匹配异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineAnchorMismatchException extends MdmBaseException {

    public TaBaselineAnchorMismatchException(String swinCode, String expectedAnchorType, String actualAnchorType) {
        super(MdmErrorCode.TA_BASELINE_ANCHOR_MISMATCH,
                String.format("锚点层级与SWIN型式引用不一致: swinCode=%s, expected=%s, actual=%s",
                        swinCode, expectedAnchorType, actualAnchorType));
        log.warn("TA基线锚点不匹配: swinCode={}, expected={}, actual={}", swinCode, expectedAnchorType, actualAnchorType);
    }
}
