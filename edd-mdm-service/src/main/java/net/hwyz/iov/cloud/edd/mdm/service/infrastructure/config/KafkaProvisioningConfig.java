package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MDM Kafka 基础设施配置
 * <p>
 * 提供 Kafka Admin Bean（MDM-DSN-CR-041）：
 * - 供 {@code MdmKafkaTopicInitializer} 启动预检 / 初始化使用（describeTopics / createTopics）
 * - Admin 连接参数复用 {@link KafkaProperties}，运行 Principal 需具备 DescribeTopics
 *   权限；生产环境如未授予 CreateTopics 权限，由部署前置任务使用受控平台账号创建
 *
 * @author hwyz_leo
 */
@Configuration
public class KafkaProvisioningConfig {

    /**
     * Kafka Admin 客户端，应用关闭时释放资源
     */
    @Bean(destroyMethod = "close")
    public Admin kafkaAdminClient(KafkaProperties properties) {
        return AdminClient.create(properties.buildAdminProperties());
    }
}
