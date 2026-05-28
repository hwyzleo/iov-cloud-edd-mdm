package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 工厂生效期非法异常（Org 子域）
 * <p>
 * 错误码：813004 PLANT_EFFECTIVE_PERIOD_INVALID
 *
 * @author hwyz_leo
 */
public class PlantEffectivePeriodInvalidException extends RuntimeException {

    public static final int ERROR_CODE = 813004;

    public PlantEffectivePeriodInvalidException(String message) {
        super(message);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
