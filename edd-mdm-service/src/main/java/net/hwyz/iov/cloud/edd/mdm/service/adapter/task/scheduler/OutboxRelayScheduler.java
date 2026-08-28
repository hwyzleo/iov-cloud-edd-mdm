package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 事件发件箱Relay定时任务
 * <p>
 * 每 5 秒扫描 mdm_outbox 表中未发送的事件，根据 aggregateType/eventType
 * 经 {@link KafkaTopicResolver} 路由到对应的 Kafka topic。
 * <p>
 * 门禁规则（MDM-DSN-CR-034 / US-133）：
 * - KafkaTopicProvisioningStatus = NOT_READY：暂停本轮，不查询 mdm_outbox、
 *   不增加 retry_count、不投递 DLQ
 * - KafkaTopicProvisioningStatus = READY：恢复既有 Outbox 扫描与 Kafka 发送流程
 * - KafkaTopicProvisioningStatus = DISABLED：显式停用框架 Provisioning，
 *   走既有兼容路径（需确认 Topic 已由外部预建）
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaEventGateway kafkaEventGateway;
    private final KafkaTopicResolver kafkaTopicResolver;
    private final KafkaTopicProvisioningStatus provisioningStatus;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 定时扫描Outbox并发送事件到Kafka
     */
    @Scheduled(fixedDelay = 5000)
    public void relayEvents() {
        // 门禁：NOT_READY 时暂停本轮，READY / DISABLED 放行
        KafkaTopicProvisioningStatus.State state = provisioningStatus.state();
        if (state == KafkaTopicProvisioningStatus.State.NOT_READY) {
            log.info("Kafka Topic 未全部就绪，暂停 Outbox Relay: provisioningState={}, relaySkipped=true", state);
            return;
        }
        if (state == KafkaTopicProvisioningStatus.State.DISABLED) {
            log.debug("Kafka Topic Provisioning 已显式停用，走兼容路径: provisioningState=DISABLED");
        }

        try {
            List<Object> pendingEvents = outboxRepository.findPendingEvents(100);
            if (pendingEvents.isEmpty()) {
                return;
            }

            log.info("扫描到{}条待发送事件", pendingEvents.size());

            for (Object obj : pendingEvents) {
                OutboxPo event = (OutboxPo) obj;
                try {
                    Optional<String> topicOpt = kafkaTopicResolver.resolve(event.getAggregateType(), event.getEventType());
                    if (topicOpt.isEmpty()) {
                        log.warn("事件 topic 未登记，跳过发送: id={}, aggregateType={}, eventType={}", event.getId(), event.getAggregateType(), event.getEventType());
                        continue;
                    }
                    String topic = topicOpt.get();
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
}
