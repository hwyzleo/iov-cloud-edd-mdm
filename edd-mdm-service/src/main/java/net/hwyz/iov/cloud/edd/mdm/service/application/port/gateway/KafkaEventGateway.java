package net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway;

/**
 * Kafka 事件发送网关接口
 * <p>
 * 负责将 Outbox 中的事件发送到对应的 Kafka topic。
 * topic 路由规则：
 * - EEAD 子域: mdm.eead.vehicleNode.event（单一 topic + eventType discriminator）
 * - Product MDM: eventType 作为 topic 名称（如 mdm.product.brand.created）
 * - Party MDM: eventType 作为 topic 名称（如 mdm.party.supplier.created）
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
