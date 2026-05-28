package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 车载节点不存在异常（EEAD 子域）
 * <p>
 * 错误码：812001 VEHICLE_NODE_NOT_EXIST
 *
 * @author hwyz_leo
 */
public class VehicleNodeNotExistException extends RuntimeException {

    /**
     * 错误码常量
     */
    public static final int ERROR_CODE = 812001;

    public VehicleNodeNotExistException(String message) {
        super(message);
    }

    public VehicleNodeNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
