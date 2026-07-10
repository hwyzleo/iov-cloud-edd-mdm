package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件发件箱Relay定时任务
 * <p>
 * 每 5 秒扫描 mdm_outbox 表中未发送的事件，根据 aggregateType 路由到对应的 Kafka topic。
 * <p>
 * topic 路由规则：
 * - Product 子域（多 topic）：eventType 直接作为 topic（如 mdm.product.brand.created）
 * - Party 子域（多 topic）：eventType 直接作为 topic（如 mdm.party.supplier.created）
 * - EEAD 子域（单 topic）：VEHICLE_NODE → mdm.eead.vehicleNode.event，DEVICE_CATEGORY → mdm.eead.deviceCategory.event
 * - Org 子域（单 topic）：PLANT → mdm.org.plant.event
 * - Material 子域（单 topic）：MATERIAL_CATEGORY → mdm.material.category.event，PART → mdm.material.part.event
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaEventGateway kafkaEventGateway;

    /**
     * 单 topic 子域映射：aggregateType → 单一 topic（EEAD / Org / Material）
     */
    private static final Map<String, String> EEAD_TOPIC_MAPPING = new HashMap<>();

    static {
        // EEAD 子域 topic 映射
        EEAD_TOPIC_MAPPING.put("VEHICLE_NODE", "mdm.eead.vehicleNode.event");
        EEAD_TOPIC_MAPPING.put("DEVICE_CATEGORY", "mdm.eead.deviceCategory.event");
        EEAD_TOPIC_MAPPING.put("RXSWIN_REGISTRY", "mdm.eead.rxswin.event");
        // Org 子域 topic 映射
        EEAD_TOPIC_MAPPING.put("PLANT", "mdm.org.plant.event");
        // Material 子域 topic 映射
        EEAD_TOPIC_MAPPING.put("MATERIAL_CATEGORY", "mdm.material.category.event");
        EEAD_TOPIC_MAPPING.put("PART", "mdm.material.part.event");
        EEAD_TOPIC_MAPPING.put("SOFTWARE_BASELINE", "mdm.material.softwareBaseline.event");
    }

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 定时扫描Outbox并发送事件到Kafka
     */
    @Scheduled(fixedDelay = 5000)
    public void relayEvents() {
        try {
            List<Object> pendingEvents = outboxRepository.findPendingEvents(100);
            if (pendingEvents.isEmpty()) {
                return;
            }

            log.info("扫描到{}条待发送事件", pendingEvents.size());

            for (Object obj : pendingEvents) {
                OutboxPo event = (OutboxPo) obj;
                try {
                    String topic = resolveTopic(event.getAggregateType(), event.getEventType());
                    kafkaEventGateway.send(topic, event.getAggregateId(), event.getPayload());
                    outboxRepository.markEventAsSent(String.valueOf(event.getId()));
                    log.debug("事件发送成功: id={}, topic={}, aggregateId={}", event.getId(), topic, event.getAggregateId());
                } catch (Exception e) {
                    log.error("事件发送失败: id={}, aggregateType={}, eventType={}", event.getId(), event.getAggregateType(), event.getEventType(), e);
                    outboxRepository.incrementRetryCount(String.valueOf(event.getId()));

                    if (event.getRetryCount() != null && event.getRetryCount() >= MAX_RETRY_COUNT) {
                        log.error("事件重试次数超限，移至死信: id={}, retryCount={}", event.getId(), event.getRetryCount());
                    }
                }
            }
        } catch (Exception e) {
            log.error("扫描Outbox事件失败", e);
        }
    }

    /**
     * 根据聚合类型和事件类型解析目标 Kafka topic
     *
     * @param aggregateType 聚合类型
     * @param eventType     事件类型
     * @return Kafka topic 名称
     */
    private String resolveTopic(String aggregateType, String eventType) {
        String topic = EEAD_TOPIC_MAPPING.get(aggregateType);
        if (topic != null) {
            return topic;
        }
        return eventType;
    }
}
