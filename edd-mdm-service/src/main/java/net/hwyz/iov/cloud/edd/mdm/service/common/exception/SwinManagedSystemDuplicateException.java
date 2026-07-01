package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 同一SWIN定义下已存在相同的车载节点异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class SwinManagedSystemDuplicateException extends MdmBaseException {

    private final String swinCode;
    private final String vehicleNodeCode;

    public SwinManagedSystemDuplicateException(String swinCode, String vehicleNodeCode) {
        super(MdmErrorCode.SWIN_MANAGED_SYSTEM_DUPLICATE,
                String.format("SWIN定义 %s 下已存在相同的车载节点: %s", swinCode, vehicleNodeCode));
        this.swinCode = swinCode;
        this.vehicleNodeCode = vehicleNodeCode;
        log.warn("SWIN定义[{}]下已存在相同的车载节点: {}", swinCode, vehicleNodeCode);
    }
}
