package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 生效期无效异常
 *
 * @author hwyz_leo
 */
public class InvalidEffectiveDateException extends RuntimeException {

    public InvalidEffectiveDateException(String message) {
        super(message);
    }

    public InvalidEffectiveDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
