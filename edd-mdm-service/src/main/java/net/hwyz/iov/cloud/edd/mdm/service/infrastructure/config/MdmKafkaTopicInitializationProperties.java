package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * MDM Kafka Topic 初始化参数（MDM-DSN-CR-041）
 * <p>
 * 控制 {@code MdmKafkaTopicInitializer} 启动预检与缺失初始化行为：
 * - 本地/测试环境可开启自动创建；生产环境默认关闭自动创建（由部署前置任务或平台创建），
 *   应用保留 Describe 预检，缺失即按 fail-fast 阻止启动或置健康为 DOWN。
 * - 分区数 / 副本数仅用于新建 Topic，已存在 Topic 不执行 alterConfigs / createPartitions。
 *
 * @author hwyz_leo
 */
@Data
@Component
@ConfigurationProperties(prefix = "edd.mdm.kafka.topic-initialization")
public class MdmKafkaTopicInitializationProperties {

    /**
     * 是否启用启动预检与初始化，默认 true
     */
    private boolean enabled = true;

    /**
     * 缺失生产 Topic 是否自动创建（幂等），默认 true；生产默认关闭
     */
    private boolean createMissingProducerTopics = true;

    /**
     * 预检失败（Kafka 不可达 / 缺失且未创建）时是否终止启动，默认 true；生产默认 true
     */
    private boolean failFast = true;

    /**
     * 新建 Topic 分区数，默认 3（环境变量 EDD_MDM_KAFKA_TOPIC_PARTITIONS 覆盖）
     */
    private int partitions = 3;

    /**
     * 新建 Topic 副本数，默认 1（环境变量 EDD_MDM_KAFKA_TOPIC_REPLICAS 覆盖）
     */
    private short replicas = 1;

    /**
     * 单次 Admin 操作总超时，默认 30s
     */
    private Duration timeout = Duration.ofSeconds(30);
}
