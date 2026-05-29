package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

public class PartNotExistException extends RuntimeException {
    public static final int ERROR_CODE = 814010;

    public PartNotExistException(String message) {
        super(message);
    }

    public PartNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getErrorCode() {
        return ERROR_CODE;
    }
}
