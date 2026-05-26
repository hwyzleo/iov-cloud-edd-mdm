package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 业务主键重复异常
 *
 * @author hwyz_leo
 */
public class DuplicateCodeException extends RuntimeException {

    public DuplicateCodeException(String message) {
        super(message);
    }

    public DuplicateCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
