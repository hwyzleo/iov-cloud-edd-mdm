package net.hwyz.iov.cloud.edd.mdm.service.domain.exception;

/**
 * Configuration 序号溢出异常（错误码 807014）
 * <p>
 * 当 mdm_configuration_seq.next_seq 超过 9,999,999（7 位上限）时抛出。
 *
 * @author hwyz_leo
 */
public class ConfigurationSeqOverflowException extends RuntimeException {

    public ConfigurationSeqOverflowException(String message) {
        super(message);
    }

    public ConfigurationSeqOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
