package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 数据接入Schema异常
 *
 * @author hwyz_leo
 */
public class IngestionSchemaException extends RuntimeException {

    public IngestionSchemaException(String message) {
        super(message);
    }

    public IngestionSchemaException(String message, Throwable cause) {
        super(message, cause);
    }
}
