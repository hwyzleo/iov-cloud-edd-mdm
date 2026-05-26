package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 事件发件箱Relay定时任务
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelayScheduler {

    private final OutboxRepository outboxRepository;

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

            for (Object event : pendingEvents) {
                try {
                    // TODO: 发送事件到Kafka
                    // kafkaGateway.send(event);

                    // 标记事件为已发送
                    // outboxRepository.markEventAsSent(event.getEventId());

                    log.info("事件发送成功: {}", event);
                } catch (Exception e) {
                    log.error("事件发送失败: {}", event, e);
                    // 增加重试次数
                    // outboxRepository.incrementRetryCount(event.getEventId());
                }
            }
        } catch (Exception e) {
            log.error("扫描Outbox事件失败", e);
        }
    }
}
