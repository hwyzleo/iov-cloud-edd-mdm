package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MDM Kafka Topic 预检/初始化可观测性指标（MDM-DSN-CR-041 §6）
 * <ul>
 *   <li>mdm_kafka_topic_check_total{role,status}：预检次数（role=producer|consumer，status=ok|missing）</li>
 *   <li>mdm_kafka_topic_create_total{status}：创建次数（status=created|failed|skipped）</li>
 *   <li>mdm_kafka_topic_missing{topic,role}：当前仍缺失的 Topic（Gauge，1=缺失）</li>
 * </ul>
 * Micrometer 注册表不存在时全部为 no-op，不影响预检主流程。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class MdmKafkaTopicMetrics {

    private final ObjectProvider<MeterRegistry> registryProvider;

    /**
     * role|status → Counter
     */
    private final Map<String, Counter> checkCounters = new ConcurrentHashMap<>();

    private final Map<String, Counter> createCounters = new ConcurrentHashMap<>();

    /**
     * topic|role → Gauge
     */
    private final Map<String, Gauge> missingGauges = new ConcurrentHashMap<>();

    public MdmKafkaTopicMetrics(ObjectProvider<MeterRegistry> registryProvider) {
        this.registryProvider = registryProvider;
    }

    private MeterRegistry registry() {
        return registryProvider.getIfAvailable();
    }

    /**
     * 记录一次预检结果。
     *
     * @param role   producer / consumer
     * @param status ok / missing
     */
    public void recordCheck(String role, String status) {
        MeterRegistry registry = registry();
        if (registry == null) {
            return;
        }
        String key = role + "|" + status;
        checkCounters.computeIfAbsent(key, k -> Counter.builder("mdm_kafka_topic_check_total")
                .description("MDM Kafka Topic 预检次数")
                .tag("role", role)
                .tag("status", status)
                .register(registry))
                .increment();
    }

    /**
     * 记录一次创建结果。
     *
     * @param status created / failed / skipped
     * @param count  数量
     */
    public void recordCreate(String status, int count) {
        MeterRegistry registry = registry();
        if (registry == null) {
            return;
        }
        String key = status;
        createCounters.computeIfAbsent(key, k -> Counter.builder("mdm_kafka_topic_create_total")
                .description("MDM Kafka Topic 创建次数")
                .tag("status", status)
                .register(registry))
                .increment(count);
    }

    /**
     * 更新缺失 Topic Gauge（1=缺失，0=已存在）。
     */
    public void recordMissing(String topic, String role, boolean missing) {
        MeterRegistry registry = registry();
        if (registry == null) {
            return;
        }
        String key = topic + "|" + role;
        missingGauges.computeIfAbsent(key, k -> Gauge.builder("mdm_kafka_topic_missing",
                        () -> isMissing(topic, role) ? 1.0 : 0.0)
                .description("MDM Kafka Topic 缺失标记")
                .tag("topic", topic)
                .tag("role", role)
                .register(registry));
        this.missingFlags.put(key, missing);
    }

    private final Map<String, Boolean> missingFlags = new ConcurrentHashMap<>();

    private boolean isMissing(String topic, String role) {
        return Boolean.TRUE.equals(missingFlags.get(topic + "|" + role));
    }
}
