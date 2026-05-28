package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka 事件发送网关实现
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventGatewayImpl implements KafkaEventGateway {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(String topic, String key, String payload) {
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
