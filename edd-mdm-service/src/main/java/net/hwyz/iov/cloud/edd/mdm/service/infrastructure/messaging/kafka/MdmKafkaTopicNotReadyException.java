package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

/**
 * MDM Kafka Topic 未就绪异常（MDM-DSN-CR-041 §4.1 / §7）
 * <p>
 * 生产 Topic 缺失且未创建成功、或 Kafka 不可达 / Describe 失败时，
 * 在 fail-fast 开启（生产默认）下抛出以终止应用启动。
 *
 * @author hwyz_leo
 */
public class MdmKafkaTopicNotReadyException extends RuntimeException {

    public MdmKafkaTopicNotReadyException(String message) {
        super(message);
    }

    public MdmKafkaTopicNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }
}
