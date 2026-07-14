package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * TA基线状态流转非法异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class TaBaselineStatusInvalidException extends MdmBaseException {

    public TaBaselineStatusInvalidException(String taBaselineCode, String currentStatus, String targetStatus) {
        super(MdmErrorCode.TA_BASELINE_STATUS_INVALID,
                String.format("TA基线 %s 状态流转非法: %s -> %s", taBaselineCode, currentStatus, targetStatus));
        log.warn("TA基线状态流转非法: {} {} -> {}", taBaselineCode, currentStatus, targetStatus);
    }
}
