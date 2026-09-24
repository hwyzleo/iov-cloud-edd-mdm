package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicInitializer;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * MDM Kafka Topic 预检/初始化集成测试（MDM-DSN-CR-041 §9.2）
 * <p>
 * 使用禁用 auto.create.topics.enable 的 Kafka Testcontainers 装配 MdmKafkaTopicInitializer：
 * - 空集群启动后创建全部 19 个生产 Topic
 * - 二次执行幂等，不重复创建
 * - Topic 就绪后 Outbox Relay 恢复并发送积压事件到目录 Topic
 *
 * @author hwyz_leo
 */
@SpringBootTest(
        classes = KafkaTopicInitializationTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "edd.mdm.kafka.topic-initialization.enabled=true",
                "edd.mdm.kafka.topic-initialization.create-missing-producer-topics=true",
                "edd.mdm.kafka.topic-initialization.fail-fast=true",
                "edd.mdm.kafka.topic-initialization.partitions=1",
                "edd.mdm.kafka.topic-initialization.replicas=1"
        })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@DisplayName("MDM Kafka Topic 预检/初始化集成测试")
class MdmKafkaTopicInitializerIntegrationTest {

    @Container
    static final MdmKafkaContainer KAFKA = KafkaTestSupport.newKafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private MdmKafkaTopicReadiness readiness;

    @Autowired
    private MdmKafkaTopicInitializer initializer;

    @Autowired
    private MdmKafkaTopicProperties topicProperties;

    @Autowired
    private OutboxRelayScheduler outboxRelayScheduler;

    @MockBean
    private OutboxRepository outboxRepository;

    @Test
    @Order(1)
    @DisplayName("空集群启动后创建全部 19 个生产 Topic 并置 READY")
    void emptyCluster_initializesAllProducerTopics() throws Exception {
        KafkaTestSupport.awaitReady(readiness, Duration.ofSeconds(60));

        assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
        Set<String> expected = Set.copyOf(topicProperties.producerTopics());
        assertEquals(expected, KafkaTestSupport.listNonInternalTopics(KAFKA.getBootstrapServers()),
                "空集群应恰好创建 19 个目录生产 Topic");
    }

    @Test
    @Order(2)
    @DisplayName("重复执行幂等：再次预检不报错、Topic 集合不变")
    void idempotentReRun() throws Exception {
        KafkaTestSupport.awaitReady(readiness, Duration.ofSeconds(60));

        initializer.run(null);

        assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
        Set<String> expected = Set.copyOf(topicProperties.producerTopics());
        assertEquals(expected, KafkaTestSupport.listNonInternalTopics(KAFKA.getBootstrapServers()),
                "重复初始化不应创建额外 Topic");
    }

    @Test
    @Order(3)
    @DisplayName("Topic 就绪后 Relay 恢复：积压 Outbox 经真实 Kafka 发送到目录 Topic")
    void backlogFlushedAfterReady() throws Exception {
        KafkaTestSupport.awaitReady(readiness, Duration.ofSeconds(60));

        OutboxPo brand = buildOutboxPo(1L, "BRAND", "BrandCreated", "BRAND_001", "{\"code\":\"BRAND_001\"}");
        OutboxPo part = buildOutboxPo(2L, "PART", "PartCreated", "00000001AA", "{\"code\":\"00000001AA\"}");
        when(outboxRepository.findPendingEvents(anyInt())).thenReturn(Arrays.asList(brand, part));

        outboxRelayScheduler.relayEvents();

        verify(outboxRepository).markEventAsSent("1");
        verify(outboxRepository).markEventAsSent("2");
        assertFalse(KafkaTestSupport.readRecords(KAFKA.getBootstrapServers(), "mdm.brand", Duration.ofSeconds(20)).isEmpty(),
                "品牌创建事件应投递到 mdm.brand");
        assertFalse(KafkaTestSupport.readRecords(KAFKA.getBootstrapServers(), "mdm.part", Duration.ofSeconds(20)).isEmpty(),
                "零件创建事件应投递到 mdm.part");
    }

    private OutboxPo buildOutboxPo(Long id, String aggregateType, String eventType, String aggregateId, String payload) {
        return OutboxPo.builder()
                .id(id)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .occurredAt(new Date())
                .sent(false)
                .retryCount(0)
                .build();
    }
}
