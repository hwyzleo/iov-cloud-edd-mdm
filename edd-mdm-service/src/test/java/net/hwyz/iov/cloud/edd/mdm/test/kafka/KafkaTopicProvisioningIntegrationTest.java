package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicDefinitionProvider;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
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
 * MDM Kafka Topic Provisioning 集成测试（MDM-DSN-CR-034）
 * <p>
 * 使用禁用 auto.create.topics.enable 的 Kafka Testcontainers 装配 FW-KAFKA：
 * - 首次启动后全部 MDM Topic 存在
 * - 重复启动幂等
 * - 不创建 upstream.* 或 Catalog 外 Topic
 * - Topic 就绪后 Relay 恢复并发送积压 Outbox
 *
 * @author hwyz_leo
 */
@SpringBootTest(
        classes = KafkaProvisioningTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "iov.kafka.topic-provisioning.enabled=true",
                "iov.kafka.topic-provisioning.initial-delay=0s",
                "iov.kafka.topic-provisioning.retry.initial-interval=1s",
                "iov.kafka.topic-provisioning.retry.max-interval=2s",
                "iov.kafka.topic-provisioning.retry.multiplier=2.0",
                "mdm.kafka.topic-provisioning.partitions=1",
                "mdm.kafka.topic-provisioning.replication-factor=1"
        })
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@DisplayName("MDM Kafka Topic Provisioning 集成测试")
class KafkaTopicProvisioningIntegrationTest {

    @Container
    static final MdmKafkaContainer KAFKA = KafkaTestSupport.newKafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private KafkaTopicProvisioningStatus provisioningStatus;

    @Autowired
    private OutboxRelayScheduler outboxRelayScheduler;

    @Autowired
    private MdmKafkaTopicDefinitionProvider mdmKafkaTopicDefinitionProvider;

    @MockBean
    private OutboxRepository outboxRepository;

    @Test
    @Order(1)
    @DisplayName("首次启动后全部 MDM Topic 存在，不创建 upstream.* 或 Catalog 外 Topic")
    void allTopicsProvisionedAndNoExtras() throws Exception {
        KafkaTestSupport.awaitReady(provisioningStatus, Duration.ofSeconds(60));

        Set<String> declared = KafkaTestSupport.declaredTopicNames(mdmKafkaTopicDefinitionProvider);
        KafkaTestSupport.assertTopicSetEquals(KAFKA.getBootstrapServers(), declared);

        assertFalse(declared.stream().anyMatch(t -> t.startsWith("upstream.")),
                "不应创建 upstream.* Topic");
        assertTrue(declared.stream().allMatch(t -> t.startsWith("mdm.")),
                "只应创建 mdm.* Topic");
        assertEquals(37, declared.size(), "MDM 应声明 37 个 topic");
    }

    @Test
    @Order(2)
    @DisplayName("重复启动幂等：新上下文重新 Provisioning 不报错、Topic 保持存在")
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void idempotentRestart() throws Exception {
        KafkaTestSupport.awaitReady(provisioningStatus, Duration.ofSeconds(60));

        Set<String> declared = KafkaTestSupport.declaredTopicNames(mdmKafkaTopicDefinitionProvider);
        KafkaTestSupport.assertTopicSetEquals(KAFKA.getBootstrapServers(), declared);
    }

    @Test
    @Order(3)
    @DisplayName("Topic 就绪后 Relay 恢复：积压 Outbox 经真实 Kafka 发送到已创建 Topic")
    void backlogFlushedAfterReady() throws Exception {
        KafkaTestSupport.awaitReady(provisioningStatus, Duration.ofSeconds(60));

        OutboxPo brand = buildOutboxPo(1L, "BRAND", "mdm.product.brand.created", "BRAND_001", "{\"code\":\"BRAND_001\"}");
        OutboxPo part = buildOutboxPo(2L, "PART", "PartCreated", "00000001AA", "{\"code\":\"00000001AA\"}");
        when(outboxRepository.findPendingEvents(anyInt())).thenReturn(Arrays.asList(brand, part));

        outboxRelayScheduler.relayEvents();

        verify(outboxRepository).markEventAsSent("1");
        verify(outboxRepository).markEventAsSent("2");
        assertFalse(KafkaTestSupport.readRecords(KAFKA.getBootstrapServers(), "mdm.product.brand.created", Duration.ofSeconds(20)).isEmpty(),
                "品牌创建事件应投递到 mdm.product.brand.created");
        assertFalse(KafkaTestSupport.readRecords(KAFKA.getBootstrapServers(), "mdm.material.part.event", Duration.ofSeconds(20)).isEmpty(),
                "零件创建事件应投递到 mdm.material.part.event");
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
