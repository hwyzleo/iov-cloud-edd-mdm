package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * Variant code 长度超限异常（错误码 807015）
 * <p>
 * 为给下层 Configuration code 自动拼接 7 位序号预留空间，
 * Variant code 长度上限为 57 字符（57 + 7 = 64），超限时抛出本异常。
 *
 * @author hwyz_leo
 */
public class VariantCodeTooLongException extends RuntimeException {

    public VariantCodeTooLongException(String message) {
        super(message);
    }

    public VariantCodeTooLongException(String message, Throwable cause) {
        super(message, cause);
    }
}
