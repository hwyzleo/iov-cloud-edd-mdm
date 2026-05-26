package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 非权威数据源异常
 *
 * @author hwyz_leo
 */
public class NonAuthoritativeSourceException extends RuntimeException {

    public NonAuthoritativeSourceException(String message) {
        super(message);
    }

    public NonAuthoritativeSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
