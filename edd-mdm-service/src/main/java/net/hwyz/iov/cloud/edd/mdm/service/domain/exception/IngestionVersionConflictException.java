package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 数据接入版本冲突异常
 *
 * @author hwyz_leo
 */
public class IngestionVersionConflictException extends RuntimeException {

    public IngestionVersionConflictException(String message) {
        super(message);
    }

    public IngestionVersionConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
