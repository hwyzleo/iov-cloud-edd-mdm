package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicCatalog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 事件发送网关实现
 * <p>
 * 发送前基于 FW-KAFKA KafkaTopicCatalog 校验目标 Topic（MDM-DSN-CR-034），
 * 未登记 topic 直接拒绝并告警，禁止未知 eventType 无约束地产生 Topic。
 * 框架 Provisioning 停用（DISABLED）时 Catalog 不存在，跳过校验走兼容路径。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventGatewayImpl implements KafkaEventGateway {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectProvider<KafkaTopicCatalog> catalogProvider;

    @Override
    public void send(String topic, String key, String payload) {
        KafkaTopicCatalog catalog = catalogProvider.getIfAvailable();
        if (catalog != null && !catalog.contains(topic)) {
            log.warn("未登记的 Kafka topic，拒绝发送: topic={}, key={}", topic, key);
            return;
        }
        try {
            kafkaTemplate.send(topic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("事件发送失败: topic={}, key={}", topic, key, ex);
                        } else {
                            log.debug("事件发送成功: topic={}, key={}", topic, key);
                        }
                    });
        } catch (Exception e) {
            log.error("事件发送异常: topic={}, key={}", topic, key, e);
            throw e;
        }
    }
}
