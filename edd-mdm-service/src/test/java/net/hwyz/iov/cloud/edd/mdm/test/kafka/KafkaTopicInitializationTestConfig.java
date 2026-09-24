package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import io.micrometer.core.instrument.MeterRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicInitializationProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.mq.KafkaEventGatewayImpl;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicHealth;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicInitializer;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicMetrics;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * MDM Kafka Topic 预检/初始化集成测试装配（MDM-DSN-CR-041）
 * <p>
 * 仅装配 Kafka 相关 Bean（Admin / Producer / 预检初始化 / 路由 / Relay 门禁），
 * 不加载 Nacos / MyBatis / Web 等业务自动配置，聚焦 Kafka 行为。
 *
 * @author hwyz_leo
 */
@SpringBootConfiguration
@EnableConfigurationProperties({
        KafkaProperties.class,
        MdmKafkaTopicProperties.class,
        MdmKafkaTopicInitializationProperties.class
})
public class KafkaTopicInitializationTestConfig {

    @Bean(destroyMethod = "close")
    Admin kafkaAdminClient(KafkaProperties properties) {
        return AdminClient.create(properties.buildAdminProperties());
    }

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
    MdmKafkaTopicReadiness mdmKafkaTopicReadiness() {
        return new MdmKafkaTopicReadiness();
    }

    @Bean
    MdmKafkaTopicMetrics mdmKafkaTopicMetrics(ObjectProvider<MeterRegistry> registryProvider) {
        return new MdmKafkaTopicMetrics(registryProvider);
    }

    @Bean
    MdmKafkaTopicInitializer mdmKafkaTopicInitializer(MdmKafkaTopicProperties topicProperties,
                                                      MdmKafkaTopicInitializationProperties initialization,
                                                      Admin admin,
                                                      MdmKafkaTopicReadiness readiness,
                                                      MdmKafkaTopicMetrics metrics) {
        return new MdmKafkaTopicInitializer(topicProperties, initialization, admin, readiness, metrics);
    }

    @Bean
    KafkaTopicResolver kafkaTopicResolver(MdmKafkaTopicProperties topicProperties) {
        return new KafkaTopicResolver(topicProperties);
    }

    @Bean
    KafkaEventGateway kafkaEventGateway(KafkaTemplate<String, String> kafkaTemplate,
                                        MdmKafkaTopicProperties topicProperties) {
        return new KafkaEventGatewayImpl(kafkaTemplate, topicProperties);
    }

    @Bean
    OutboxRelayScheduler outboxRelayScheduler(OutboxRepository outboxRepository,
                                              KafkaEventGateway kafkaEventGateway,
                                              KafkaTopicResolver kafkaTopicResolver,
                                              MdmKafkaTopicReadiness readiness) {
        return new OutboxRelayScheduler(outboxRepository, kafkaEventGateway, kafkaTopicResolver, readiness);
    }

    @Bean
    MdmKafkaTopicHealth mdmKafkaTopicHealth(MdmKafkaTopicReadiness readiness,
                                            MdmKafkaTopicInitializationProperties initialization) {
        return new MdmKafkaTopicHealth(readiness, initialization);
    }
}
