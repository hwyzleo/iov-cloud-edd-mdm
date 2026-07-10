package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 软件基线状态流转非法异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class SoftwareBaselineStatusInvalidException extends MdmBaseException {

    public SoftwareBaselineStatusInvalidException(String currentStatus, String targetStatus) {
        super(MdmErrorCode.SW_BASELINE_STATUS_INVALID,
                String.format("基线状态流转非法: %s -> %s", currentStatus, targetStatus));
        log.warn("软件基线状态流转非法: {} -> {}", currentStatus, targetStatus);
    }
}
