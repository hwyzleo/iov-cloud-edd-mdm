package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicCatalog;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MDM Kafka Topic 路由解析器
 * <p>
 * 基于 FW-KAFKA KafkaTopicCatalog 解析或校验事件目标 Topic（MDM-DSN-CR-034）：
 * - 单 topic 子域（EEAD / Org / Material）：aggregateType → 固定 topic
 * - 多 topic 子域（Product / Party）：eventType 即 topic
 * - 未登记事件被拒绝并告警，禁止无约束地产生 Topic
 * <p>
 * 框架 Provisioning 停用（DISABLED）时 Catalog 不存在，走既有兼容解析路径，不校验。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class KafkaTopicResolver {

    private final ObjectProvider<KafkaTopicCatalog> catalogProvider;

    public KafkaTopicResolver(ObjectProvider<KafkaTopicCatalog> catalogProvider) {
        this.catalogProvider = catalogProvider;
    }

    /**
     * 单 topic 子域映射：aggregateType → 固定 topic
     */
    private static final Map<String, String> SINGLE_TOPIC_MAPPING = new HashMap<>();

    static {
        // EEAD 子域
        SINGLE_TOPIC_MAPPING.put("VEHICLE_NODE", "mdm.eead.vehicleNode.event");
        SINGLE_TOPIC_MAPPING.put("DEVICE_CATEGORY", "mdm.eead.deviceCategory.event");
        SINGLE_TOPIC_MAPPING.put("SWIN_DEFINITION", "mdm.eead.swin.event");
        SINGLE_TOPIC_MAPPING.put("SWIN_SCHEME", "mdm.eead.swinScheme.event");
        SINGLE_TOPIC_MAPPING.put("RXSWIN_REGISTRY", "mdm.eead.rxswin.event");
        SINGLE_TOPIC_MAPPING.put("TYPE_APPROVAL_BASELINE", "mdm.eead.typeApprovalBaseline.event");
        // Org 子域
        SINGLE_TOPIC_MAPPING.put("PLANT", "mdm.org.plant.event");
        // Material 子域
        SINGLE_TOPIC_MAPPING.put("MATERIAL_CATEGORY", "mdm.material.category.event");
        SINGLE_TOPIC_MAPPING.put("PART", "mdm.material.part.event");
        SINGLE_TOPIC_MAPPING.put("SOFTWARE_BASELINE", "mdm.material.softwareBaseline.event");
    }

    /**
     * 解析事件目标 Topic
     *
     * @param aggregateType 聚合类型
     * @param eventType     事件类型
     * @return 目标 topic；未登记或 Catalog 缺失时返回 empty
     */
    public Optional<String> resolve(String aggregateType, String eventType) {
        String fixed = SINGLE_TOPIC_MAPPING.get(aggregateType);
        if (fixed != null) {
            return resolveAndValidate(fixed, aggregateType, eventType);
        }
        return resolveAndValidate(eventType, aggregateType, eventType);
    }

    private Optional<String> resolveAndValidate(String topic, String aggregateType, String eventType) {
        KafkaTopicCatalog catalog = catalogProvider.getIfAvailable();
        if (catalog != null && !catalog.contains(topic)) {
            log.warn("未登记的 Kafka topic 被拒绝: topic={}, aggregateType={}, eventType={}", topic, aggregateType, eventType);
            return Optional.empty();
        }
        return Optional.of(topic);
    }
}
