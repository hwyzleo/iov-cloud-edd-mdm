package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 数据接入认证异常
 *
 * @author hwyz_leo
 */
public class IngestionAuthException extends RuntimeException {

    public IngestionAuthException(String message) {
        super(message);
    }

    public IngestionAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
