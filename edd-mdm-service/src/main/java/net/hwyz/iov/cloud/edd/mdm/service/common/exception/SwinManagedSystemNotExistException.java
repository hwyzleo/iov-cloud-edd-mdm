package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * SWIN定义下的受管系统不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinManagedSystemNotExistException extends MdmBaseException {

    private final String swinCode;
    private final String vehicleNodeCode;

    public SwinManagedSystemNotExistException(String swinCode, String vehicleNodeCode) {
        super(MdmErrorCode.SWIN_MANAGED_SYSTEM_NOT_EXIST,
                String.format("SWIN定义 %s 下的受管系统不存在: %s", swinCode, vehicleNodeCode));
        this.swinCode = swinCode;
        this.vehicleNodeCode = vehicleNodeCode;
        log.warn("SWIN定义[{}]下的受管系统不存在: {}", swinCode, vehicleNodeCode);
    }
}
