package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

public class MaterialCategoryEffectivePeriodInvalidException extends RuntimeException {
    public static final int ERROR_CODE = 814004;

    public MaterialCategoryEffectivePeriodInvalidException(String message) {
        super(message);
    }

    public MaterialCategoryEffectivePeriodInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
