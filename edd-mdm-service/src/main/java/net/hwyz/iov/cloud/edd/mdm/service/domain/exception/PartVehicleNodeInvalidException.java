package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

import lombok.Getter;

@Getter
public class PartVehicleNodeInvalidException extends RuntimeException {
    public static final int ERROR_CODE = 814012;
    private final String vehicleNodeCode;

    public PartVehicleNodeInvalidException(String vehicleNodeCode) {
        super(String.format("车辆节点无效: %s", vehicleNodeCode));
        this.vehicleNodeCode = vehicleNodeCode;
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
