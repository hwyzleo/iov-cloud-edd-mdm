package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 工厂不存在异常（Org 子域）
 * <p>
 * 错误码：813001 PLANT_NOT_EXIST
 *
 * @author hwyz_leo
 */
public class PlantNotExistException extends RuntimeException {

    public static final int ERROR_CODE = 813001;

    public PlantNotExistException(String message) {
        super(message);
    }

    public PlantNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
