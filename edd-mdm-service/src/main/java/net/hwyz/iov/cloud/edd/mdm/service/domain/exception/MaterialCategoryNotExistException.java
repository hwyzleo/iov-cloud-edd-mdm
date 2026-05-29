package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

public class MaterialCategoryNotExistException extends RuntimeException {
    public static final int ERROR_CODE = 814001;

    public MaterialCategoryNotExistException(String message) {
        super(message);
    }

    public MaterialCategoryNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
