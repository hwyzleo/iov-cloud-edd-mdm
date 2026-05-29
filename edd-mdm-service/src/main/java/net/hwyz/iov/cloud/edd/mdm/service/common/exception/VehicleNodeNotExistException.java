package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 车载节点不存在异常
 *
 * @author hwyz_leo
 */
@Slf4j
public class VehicleNodeNotExistException extends MdmBaseException {

    public VehicleNodeNotExistException(String message) {
        super(MdmErrorCode.VEHICLE_NODE_NOT_EXIST, message);
        log.warn("车载节点不存在: {}", message);
    }

    public VehicleNodeNotExistException(String message, Throwable cause) {
        super(MdmErrorCode.VEHICLE_NODE_NOT_EXIST, message);
        log.warn("车载节点不存在: {}", message, cause);
    }
}
