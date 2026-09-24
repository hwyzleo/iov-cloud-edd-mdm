package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MDM Kafka Topic 就绪状态（MDM-DSN-CR-041）
 * <p>
 * 由 {@link MdmKafkaTopicInitializer} 更新，供 Outbox Relay 门禁、健康检查与指标共享。
 * 初始状态为 UNKNOWN，此时 Relay 暂停，避免在预检完成前消费业务重试次数。
 *
 * @author hwyz_leo
 */
@Component
public class MdmKafkaTopicReadiness {

    /**
     * 就绪状态
     */
    public enum State {
        /**
         * 初始状态：预检尚未执行
         */
        UNKNOWN,
        /**
         * 全部生产 Topic 就绪，允许发布
         */
        READY,
        /**
         * 存在缺失生产 Topic 且未创建成功（fail-fast 关闭），禁止发布
         */
        NOT_READY,
        /**
         * 预检/初始化被显式禁用，走兼容路径（需确认 Topic 已外部预建）
         */
        DISABLED
    }

    /**
     * 状态快照（不可变）
     *
     * @param state                 就绪状态
     * @param missingProducerTopics 缺失生产 Topic（完整清单）
     * @param missingConsumerTopics 缺失消费 Topic（仅报告，不创建）
     * @param expectedProducerCount 期望生产 Topic 数
     * @param expectedConsumerCount 期望消费 Topic 数
     * @param autoCreateEnabled     自动创建开关
     * @param lastCheckAt           最近一次检查时间
     * @param lastFailure           最近一次失败原因（如有）
     */
    public record Snapshot(State state,
                           Set<String> missingProducerTopics,
                           Set<String> missingConsumerTopics,
                           int expectedProducerCount,
                           int expectedConsumerCount,
                           boolean autoCreateEnabled,
                           Instant lastCheckAt,
                           Optional<Throwable> lastFailure) {

        public Snapshot {
            missingProducerTopics = Set.copyOf(missingProducerTopics);
            missingConsumerTopics = Set.copyOf(missingConsumerTopics);
            lastFailure = lastFailure == null ? Optional.empty() : lastFailure;
        }

        public boolean ready() {
            return state == State.READY;
        }

        static Snapshot initial() {
            return new Snapshot(State.UNKNOWN, Set.of(), Set.of(), 0, 0, false, null, Optional.empty());
        }
    }

    private final AtomicReference<Snapshot> snapshot = new AtomicReference<>(Snapshot.initial());

    /**
     * 当前状态快照。
     */
    public Snapshot snapshot() {
        return snapshot.get();
    }

    /**
     * 当前状态。
     */
    public State state() {
        return snapshot.get().state();
    }

    /**
     * 原子更新状态快照。
     */
    public void update(Snapshot newSnapshot) {
        snapshot.set(newSnapshot);
    }
}
