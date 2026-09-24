package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicConfigException;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件发件箱Relay定时任务
 * <p>
 * 每 5 秒扫描 mdm_outbox 表中未发送的事件，根据 aggregateType 经
 * {@link KafkaTopicResolver} 路由到 Kafka Topic 目录对应的 topic。
 * <p>
 * 门禁规则（MDM-DSN-CR-041 F29）：
 * - MdmKafkaTopicReadiness 为 UNKNOWN / NOT_READY：暂停本轮，不查询 mdm_outbox、
 *   不增加 retry_count、不投递 DLQ
 * - READY：恢复既有 Outbox 扫描与 Kafka 发送流程
 * - DISABLED：显式停用预检/初始化，走兼容路径（需确认 Topic 已由外部预建）
 * <p>
 * 事件路由配置错误（未登记聚合类型）时跳过该事件且不增加 retry_count；
 * 运行期发送失败沿用既有重试机制。
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
    private final MdmKafkaTopicReadiness readiness;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    /**
     * 定时扫描Outbox并发送事件到Kafka
     */
    @Scheduled(fixedDelay = 5000)
    public void relayEvents() {
        // 门禁：UNKNOWN / NOT_READY 时暂停本轮，READY / DISABLED 放行
        MdmKafkaTopicReadiness.State state = readiness.state();
        if (state == MdmKafkaTopicReadiness.State.UNKNOWN
                || state == MdmKafkaTopicReadiness.State.NOT_READY) {
            log.info("Kafka Topic 未就绪，暂停 Outbox Relay: state={}, missingProducerTopics={}",
                    state, readiness.snapshot().missingProducerTopics());
            return;
        }
        if (state == MdmKafkaTopicReadiness.State.DISABLED) {
            log.debug("MDM Kafka Topic 预检/初始化已显式停用，走兼容路径: state=DISABLED");
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
                    String topic = kafkaTopicResolver.resolve(event.getAggregateType(), event.getEventType());
                    kafkaEventGateway.send(topic, event.getAggregateId(), event.getPayload());
                    outboxRepository.markEventAsSent(String.valueOf(event.getId()));
                    log.debug("事件发送成功: id={}, topic={}, aggregateId={}", event.getId(), topic, event.getAggregateId());
                } catch (MdmKafkaTopicConfigException e) {
                    log.error("事件 topic 路由配置错误，跳过发送（不增加重试）: id={}, aggregateType={}, eventType={}",
                            event.getId(), event.getAggregateType(), event.getEventType(), e);
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
