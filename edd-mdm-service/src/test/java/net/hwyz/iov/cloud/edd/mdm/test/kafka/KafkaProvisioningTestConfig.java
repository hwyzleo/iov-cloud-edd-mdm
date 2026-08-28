package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProvisioningProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.mq.KafkaEventGatewayImpl;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicDefinitionProvider;
import net.hwyz.iov.cloud.framework.kafka.autoconfigure.KafkaAutoConfiguration;
import net.hwyz.iov.cloud.framework.kafka.autoconfigure.KafkaTopicProvisioningAutoConfiguration;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicCatalog;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * MDM Kafka Topic Provisioning 集成测试装配
 * <p>
 * 仅装配 FW-KAFKA Topic Provisioning 自动配置 + MDM 相关 Bean，
 * 不加载 Nacos / MyBatis / Web 等业务自动配置，聚焦 Kafka 行为。
 *
 * @author hwyz_leo
 */
@SpringBootConfiguration
@EnableConfigurationProperties({KafkaProperties.class, MdmKafkaTopicProvisioningProperties.class})
@ImportAutoConfiguration({
        KafkaAutoConfiguration.class,
        KafkaTopicProvisioningAutoConfiguration.class
})
public class KafkaProvisioningTestConfig {

    @Bean
    ProducerFactory<String, String> producerFactory(KafkaProperties properties) {
        Map<String, Object> producerProps = new HashMap<>(properties.buildProducerProperties());
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    MdmKafkaTopicDefinitionProvider mdmKafkaTopicDefinitionProvider(MdmKafkaTopicProvisioningProperties properties) {
        return new MdmKafkaTopicDefinitionProvider(properties);
    }

    @Bean
    KafkaTopicResolver kafkaTopicResolver(ObjectProvider<KafkaTopicCatalog> catalogProvider) {
        return new KafkaTopicResolver(catalogProvider);
    }

    @Bean
    KafkaEventGateway kafkaEventGateway(KafkaTemplate<String, String> kafkaTemplate,
                                        ObjectProvider<KafkaTopicCatalog> catalogProvider) {
        return new KafkaEventGatewayImpl(kafkaTemplate, catalogProvider);
    }

    @Bean
    OutboxRelayScheduler outboxRelayScheduler(OutboxRepository outboxRepository,
                                              KafkaEventGateway kafkaEventGateway,
                                              KafkaTopicResolver kafkaTopicResolver,
                                              KafkaTopicProvisioningStatus provisioningStatus) {
        return new OutboxRelayScheduler(outboxRepository, kafkaEventGateway, kafkaTopicResolver, provisioningStatus);
    }
}
