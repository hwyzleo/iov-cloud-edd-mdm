package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线锚点 Configuration/Variant 引用无效异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineAnchorInvalidException extends MdmBaseException {

    public SoftwareBaselineAnchorInvalidException(String anchorType, String anchorCode) {
        super(MdmErrorCode.SW_BASELINE_ANCHOR_INVALID,
                String.format("锚点引用无效: anchorType=%s, anchorCode=%s（不存在或非 ACTIVE）", anchorType, anchorCode));
        log.warn("软件基线锚点引用无效: anchorType={}, anchorCode={}", anchorType, anchorCode);
    }
}
