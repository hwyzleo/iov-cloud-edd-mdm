package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * 品牌未找到异常
 *
 * @author hwyz_leo
 */
public class BrandNotFoundException extends RuntimeException {

    public BrandNotFoundException(String message) {
        super(message);
    }

    public BrandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
