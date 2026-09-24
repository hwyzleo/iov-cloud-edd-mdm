package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicInitializationProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

/**
 * MDM Kafka Topic 健康检查快照（MDM-DSN-CR-041 §6）
 * <p>
 * 输出生产 Topic 期望数 / 存在数 / 缺失清单、消费 Topic 期望数 / 缺失清单、
 * 自动创建开关与最近一次检查时间；不输出 Kafka 凭据或敏感连接参数。
 *
 * @author hwyz_leo
 */
@Component
public class MdmKafkaTopicHealth {

    private final MdmKafkaTopicReadiness readiness;
    private final MdmKafkaTopicInitializationProperties initialization;

    public MdmKafkaTopicHealth(MdmKafkaTopicReadiness readiness,
                               MdmKafkaTopicInitializationProperties initialization) {
        this.readiness = readiness;
        this.initialization = initialization;
    }

    /**
     * 健康状态
     */
    public record Status(MdmKafkaTopicReadiness.State state,
                         int expectedProducerTopics,
                         int existingProducerTopics,
                         Set<String> missingProducerTopics,
                         int expectedConsumerTopics,
                         Set<String> missingConsumerTopics,
                         boolean autoCreateEnabled,
                         boolean failFast,
                         Instant lastCheckAt) {

        /**
         * 是否健康：READY 为 UP；DISABLED 视为 UP（外部预建由人工负责）；其余为 DOWN。
         */
        public boolean up() {
            return state == MdmKafkaTopicReadiness.State.READY
                    || state == MdmKafkaTopicReadiness.State.DISABLED;
        }
    }

    /**
     * 生成当前健康快照。
     */
    public Status status() {
        MdmKafkaTopicReadiness.Snapshot snapshot = readiness.snapshot();
        int expectedProducer = snapshot.expectedProducerCount();
        int existingProducer = expectedProducer - snapshot.missingProducerTopics().size();
        return new Status(
                snapshot.state(),
                expectedProducer,
                existingProducer,
                snapshot.missingProducerTopics(),
                snapshot.expectedConsumerCount(),
                snapshot.missingConsumerTopics(),
                snapshot.autoCreateEnabled(),
                initialization.isFailFast(),
                snapshot.lastCheckAt());
    }
}
