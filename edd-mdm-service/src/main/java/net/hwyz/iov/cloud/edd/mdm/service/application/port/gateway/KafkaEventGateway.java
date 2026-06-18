package net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway;

/**
 * Kafka 事件发送网关接口
 * <p>
 * 负责将 Outbox 中的事件发送到对应的 Kafka topic。
 * topic 路由规则：
 * - Product MDM（多 topic）：eventType 作为 topic 名称（如 mdm.product.brand.created）
 * - Party MDM（多 topic）：eventType 作为 topic 名称（如 mdm.party.supplier.created）
 * - EEAD（单 topic）：mdm.eead.vehicleNode.event / mdm.eead.deviceCategory.event
 * - Org（单 topic）：mdm.org.plant.event
 * - Material（单 topic）：mdm.material.part.event / mdm.material.category.event
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
