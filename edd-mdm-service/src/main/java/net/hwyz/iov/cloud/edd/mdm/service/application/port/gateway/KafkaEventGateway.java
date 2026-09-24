package net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway;

/**
 * Kafka 事件发送网关接口
 * <p>
 * 负责将 Outbox 中的事件发送到对应的 Kafka topic（MDM-DSN-CR-041）。
 * topic 名称统一来自 Kafka Topic 目录（19 个生产 Topic，如 mdm.brand / mdm.vehicle-node），
 * 由 KafkaTopicResolver 按 aggregateType 显式映射，事件操作由 payload 的 eventType 区分。
 *
 * @author hwyz_leo
 */
public interface KafkaEventGateway {

    /**
     * 发送事件到 Kafka
     *
     * @param topic     Kafka topic
     * @param key       消息 key（通常为 aggregateId）
     * @param payload   消息体（JSON 字符串）
     */
    void send(String topic, String key, String payload);
}
