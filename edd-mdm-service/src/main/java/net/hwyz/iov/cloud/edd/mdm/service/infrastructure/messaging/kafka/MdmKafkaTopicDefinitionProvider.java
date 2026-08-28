package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProvisioningProperties;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicDefinition;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicDefinitionProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * MDM 下游 Kafka Topic 定义提供者
 * <p>
 * 向 FW-KAFKA 声明全部 mdm.* Topic（Product / Party / EEAD / Org / Material），
 * 由框架统一完成 Catalog 合并、存在性检查、幂等创建、后台重试与状态传播。
 * <p>
 * MDM Provider 不声明 upstream.*、DLQ 或清单外 Topic；Topic 分区数、副本数
 * 通过 {@link MdmKafkaTopicProvisioningProperties} 环境参数注入。
 *
 * @author hwyz_leo
 */
@Component
@RequiredArgsConstructor
public class MdmKafkaTopicDefinitionProvider implements KafkaTopicDefinitionProvider {

    private final MdmKafkaTopicProvisioningProperties properties;

    /**
     * Product 子域多 topic 实体（eventType 即 topic）
     */
    private static final List<String> PRODUCT_ENTITIES = List.of(
            "brand", "carLine", "platform", "model", "variant",
            "configuration", "optionFamily", "optionCode");

    /**
     * Product / Party 多 topic 事件维度
     */
    private static final List<String> MULTI_TOPIC_EVENTS = List.of("created", "updated", "deactivated");

    /**
     * Party 子域多 topic 实体
     */
    private static final List<String> PARTY_ENTITIES = List.of("supplier");

    /**
     * 固定 topic（EEAD / Org / Material 单 topic 子域）
     */
    private static final List<String> FIXED_TOPICS = List.of(
            "mdm.eead.vehicleNode.event",
            "mdm.eead.deviceCategory.event",
            "mdm.eead.swinScheme.event",
            "mdm.eead.swin.event",
            "mdm.eead.typeApprovalBaseline.event",
            "mdm.eead.rxswin.event",
            "mdm.org.plant.event",
            "mdm.material.part.event",
            "mdm.material.category.event",
            "mdm.material.softwareBaseline.event");

    @Override
    public Collection<KafkaTopicDefinition> topicDefinitions() {
        Set<String> names = new LinkedHashSet<>();

        // Product 子域多 topic
        for (String entity : PRODUCT_ENTITIES) {
            for (String event : MULTI_TOPIC_EVENTS) {
                names.add("mdm.product." + entity + "." + event);
            }
        }
        // Party 子域多 topic
        for (String entity : PARTY_ENTITIES) {
            for (String event : MULTI_TOPIC_EVENTS) {
                names.add("mdm.party." + entity + "." + event);
            }
        }
        // 固定 topic
        names.addAll(FIXED_TOPICS);

        List<KafkaTopicDefinition> definitions = new ArrayList<>();
        for (String name : names) {
            definitions.add(new KafkaTopicDefinition(
                    name, properties.getPartitions(), properties.getReplicationFactor()));
        }
        return definitions;
    }
}
