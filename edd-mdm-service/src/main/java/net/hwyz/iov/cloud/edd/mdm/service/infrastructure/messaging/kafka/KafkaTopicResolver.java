package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MDM Kafka Topic 路由解析器（MDM-DSN-CR-041 §5）
 * <p>
 * 将 Outbox 记录中的聚合类型映射到 Kafka Topic 目录名称，映射为显式 SSOT：
 * - 19 条 aggregateType → 语义键 → Topic 名称（来自 {@link MdmKafkaTopicProperties}）
 * - 未登记聚合类型禁止回退到类名 / eventType 自动推导，抛出配置错误并阻止事件发送
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaTopicResolver {

    private final MdmKafkaTopicProperties topicProperties;

    /**
     * 聚合类型 → Topic 语义键（与 mdm_outbox.aggregate_type 落库值一致，19 条全覆盖）。
     */
    private static final Map<String, String> AGGREGATE_TYPE_TO_KEY = Map.ofEntries(
            Map.entry("BRAND", "brand"),
            Map.entry("CAR_LINE", "car-line"),
            Map.entry("PLATFORM", "platform"),
            Map.entry("MODEL", "model"),
            Map.entry("VARIANT", "variant"),
            Map.entry("CONFIGURATION", "configuration"),
            Map.entry("OPTION_FAMILY", "option-family"),
            Map.entry("OPTION_CODE", "option-code"),
            Map.entry("SUPPLIER", "supplier"),
            Map.entry("VEHICLE_NODE", "vehicle-node"),
            Map.entry("DEVICE_CATEGORY", "device-category"),
            Map.entry("SWIN_SCHEME", "swin-scheme"),
            Map.entry("SWIN_DEFINITION", "swin-definition"),
            Map.entry("RXSWIN_REGISTRY", "rxswin"),
            Map.entry("TYPE_APPROVAL_BASELINE", "type-approval-baseline"),
            Map.entry("PLANT", "plant"),
            Map.entry("MATERIAL_CATEGORY", "material-category"),
            Map.entry("PART", "part"),
            Map.entry("SOFTWARE_BASELINE", "software-baseline"));

    /**
     * 解析事件目标 Topic。
     *
     * @param aggregateType 聚合类型（mdm_outbox.aggregate_type）
     * @param eventType     事件类型（仅作日志上下文，不参与路由推导）
     * @return 目录 Topic 名称
     * @throws MdmKafkaTopicConfigException 聚合类型未登记映射或配置缺失
     */
    public String resolve(String aggregateType, String eventType) {
        String key = aggregateType == null ? null : AGGREGATE_TYPE_TO_KEY.get(aggregateType);
        if (key == null) {
            throw new MdmKafkaTopicConfigException(
                    "未知聚合类型，无法映射 Kafka topic（禁止回退推导）: aggregateType=" + aggregateType + ", eventType=" + eventType);
        }
        String topic = topicProperties.producerTopic(key);
        log.debug("解析 Kafka topic: aggregateType={}, eventType={}, key={}, topic={}", aggregateType, eventType, key, topic);
        return topic;
    }
}
