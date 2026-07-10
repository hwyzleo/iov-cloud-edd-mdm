package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线项零件引用无效异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineItemPartInvalidException extends MdmBaseException {

    public SoftwareBaselineItemPartInvalidException(String partCode) {
        super(MdmErrorCode.SW_BASELINE_ITEM_PART_INVALID,
                String.format("基线项零件引用无效: %s（不存在或非 ACTIVE）", partCode));
        log.warn("软件基线项零件引用无效: {}", partCode);
    }
}
