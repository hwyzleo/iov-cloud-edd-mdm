package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线项零件非软件件异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineItemNotSoftwareException extends MdmBaseException {

    public SoftwareBaselineItemNotSoftwareException(String partCode) {
        super(MdmErrorCode.SW_BASELINE_ITEM_NOT_SOFTWARE,
                String.format("零件 %s 非软件件（is_software=false），不可纳入软件基线", partCode));
        log.warn("软件基线项零件非软件件: {}", partCode);
    }
}
