package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

/**
 * MDM Kafka Topic 配置错误（MDM-DSN-CR-041）
 * <p>
 * 配置缺失 / 重复 / 与目录基线不一致，或事件聚合类型未登记映射时抛出，
 * 用于阻止事件发送（禁止回退到类名或 eventType 自动推导）。
 *
 * @author hwyz_leo
 */
public class MdmKafkaTopicConfigException extends RuntimeException {

    public MdmKafkaTopicConfigException(String message) {
        super(message);
    }

    public MdmKafkaTopicConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
