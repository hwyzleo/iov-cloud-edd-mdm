package net.hwyz.iov.cloud.edd.mdm.service.common.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 零件 vehicleNodeCode 指向不存在的 VehicleNode 异常
 *
 * @author hwyz_leo
 */
@Slf4j
@Getter
public class PartVehicleNodeInvalidException extends MdmBaseException {

    private final String vehicleNodeCode;

    public PartVehicleNodeInvalidException(String vehicleNodeCode) {
        super(MdmErrorCode.PART_VEHICLE_NODE_INVALID, String.format("车辆节点无效: %s", vehicleNodeCode));
        this.vehicleNodeCode = vehicleNodeCode;
        log.warn("车辆节点无效: {}", vehicleNodeCode);
    }
}
