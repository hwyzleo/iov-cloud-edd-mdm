package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicConfigException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * MDM Kafka Topic 集中配置（MDM-DSN-CR-041）
 * <p>
 * 以 Kafka Topic 目录为名称唯一基线，集中维护 19 个 EDD-MDM 生产 Topic；
 * 业务代码只按语义键引用（如 {@code brand} → {@code mdm.brand}），禁止硬编码或拼接 Topic 名称。
 * <p>
 * 配置前缀 {@code edd.mdm.kafka.topics}；Topic 名称默认值即目录基线，
 * 如确需覆盖必须通过受控发布配置并保留审计，不允许在业务代码中二次拼接。
 * <p>
 * Topic 清单拆分为 {@link #producerTopics()} 与 {@link #consumerTopics()}；
 * 当前 EDD-MDM 无消费 Topic，消费者集合为空。
 *
 * @author hwyz_leo
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "edd.mdm.kafka.topics")
public class MdmKafkaTopicProperties {

    /**
     * 品牌
     */
    private String brand = "mdm.brand";

    /**
     * 车系
     */
    private String carLine = "mdm.car-line";

    /**
     * 配置
     */
    private String configuration = "mdm.configuration";

    /**
     * 设备类别
     */
    private String deviceCategory = "mdm.device-category";

    /**
     * 物料分类
     */
    private String materialCategory = "mdm.material-category";

    /**
     * 车型
     */
    private String model = "mdm.model";

    /**
     * 选项码
     */
    private String optionCode = "mdm.option-code";

    /**
     * 选项族
     */
    private String optionFamily = "mdm.option-family";

    /**
     * 零件
     */
    private String part = "mdm.part";

    /**
     * 工厂
     */
    private String plant = "mdm.plant";

    /**
     * 平台
     */
    private String platform = "mdm.platform";

    /**
     * RXSWIN 登记
     */
    private String rxswin = "mdm.rxswin";

    /**
     * 软件基线
     */
    private String softwareBaseline = "mdm.software-baseline";

    /**
     * 供应商
     */
    private String supplier = "mdm.supplier";

    /**
     * SWIN 定义
     */
    private String swinDefinition = "mdm.swin-definition";

    /**
     * SWIN 编码方案
     */
    private String swinScheme = "mdm.swin-scheme";

    /**
     * 型式批准基线
     */
    private String typeApprovalBaseline = "mdm.type-approval-baseline";

    /**
     * 版本
     */
    private String variant = "mdm.variant";

    /**
     * 车载节点
     */
    private String vehicleNode = "mdm.vehicle-node";

    /**
     * 内置语义键集合（与 Kafka Topic 目录一致），用于配置校验。
     */
    private static final List<String> BASELINE_KEYS = List.of(
            "brand", "car-line", "configuration", "device-category", "material-category",
            "model", "option-code", "option-family", "part", "plant", "platform", "rxswin",
            "software-baseline", "supplier", "swin-definition", "swin-scheme",
            "type-approval-baseline", "variant", "vehicle-node");

    /**
     * 配置绑定后校验：名称非空、无重复，并与内置基线键集合一致。
     */
    @PostConstruct
    public void validate() {
        Map<String, String> byKey = producerTopicsByKey();
        if (byKey.size() != BASELINE_KEYS.size()) {
            throw new MdmKafkaTopicConfigException(
                    "Kafka Topic 配置键集合与目录基线不一致: expectedKeys=" + BASELINE_KEYS + ", actualKeys=" + byKey.keySet());
        }
        Set<String> names = new LinkedHashSet<>();
        for (Map.Entry<String, String> entry : byKey.entrySet()) {
            String name = entry.getValue();
            if (name == null || name.isBlank()) {
                throw new MdmKafkaTopicConfigException("Kafka Topic 名称不能为空: key=" + entry.getKey());
            }
            if (!names.add(name)) {
                throw new MdmKafkaTopicConfigException("Kafka Topic 名称重复: name=" + name);
            }
            if (!name.matches("^[a-zA-Z0-9._-]+$")) {
                throw new MdmKafkaTopicConfigException("Kafka Topic 名称格式非法: key=" + entry.getKey() + ", name=" + name);
            }
        }
        log.info("MDM Kafka Topic 配置校验通过: producerTopics={}", byKey.values());
    }

    /**
     * 生产 Topic 语义键 → Topic 名称（有序）。
     */
    public Map<String, String> producerTopicsByKey() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("brand", brand);
        map.put("car-line", carLine);
        map.put("configuration", configuration);
        map.put("device-category", deviceCategory);
        map.put("material-category", materialCategory);
        map.put("model", model);
        map.put("option-code", optionCode);
        map.put("option-family", optionFamily);
        map.put("part", part);
        map.put("plant", plant);
        map.put("platform", platform);
        map.put("rxswin", rxswin);
        map.put("software-baseline", softwareBaseline);
        map.put("supplier", supplier);
        map.put("swin-definition", swinDefinition);
        map.put("swin-scheme", swinScheme);
        map.put("type-approval-baseline", typeApprovalBaseline);
        map.put("variant", variant);
        map.put("vehicle-node", vehicleNode);
        return map;
    }

    /**
     * 生产 Topic 清单（19 个目录 Topic）。
     */
    public List<String> producerTopics() {
        return List.copyOf(producerTopicsByKey().values());
    }

    /**
     * 消费 Topic 清单；当前 EDD-MDM 无消费 Topic。
     */
    public Set<String> consumerTopics() {
        return Set.of();
    }

    /**
     * 按语义键解析生产 Topic 名称；键不存在时抛出配置错误（禁止回退推导）。
     *
     * @param key 语义键（如 brand / car-line）
     * @return Topic 名称
     */
    public String producerTopic(String key) {
        String name = producerTopicsByKey().get(key);
        if (name == null || name.isBlank()) {
            throw new MdmKafkaTopicConfigException("未知 Kafka Topic 语义键: key=" + key);
        }
        return name;
    }
}
