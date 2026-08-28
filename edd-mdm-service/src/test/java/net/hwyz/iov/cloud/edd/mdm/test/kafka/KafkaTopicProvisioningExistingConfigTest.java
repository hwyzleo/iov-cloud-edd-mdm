package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 已存在 Topic 配置不被修改集成测试（MDM-DSN-CR-034）
 * <p>
 * 环境中已预建 Topic（分区数与声明不同），FW-KAFKA Provisioning 仅做存在性检查，
 * 不修改已有 Topic 的分区数、副本数或配置。
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
@Testcontainers
@DisplayName("已存在 Topic 配置不被修改集成测试")
class KafkaTopicProvisioningExistingConfigTest {

    @Container
    static final MdmKafkaContainer KAFKA = KafkaTestSupport.newKafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private KafkaTopicProvisioningStatus provisioningStatus;

    @MockBean
    private OutboxRepository outboxRepository;

    /**
     * 预建 Topic：分区数为 5（与 MDM 声明分区数 1 不同）
     */
    private static final String PRE_BUILT_TOPIC = "mdm.product.brand.created";

    @BeforeAll
    static void preCreateTopicWithDifferentConfig() throws Exception {
        KafkaTestSupport.preCreateTopic(KAFKA.getBootstrapServers(), PRE_BUILT_TOPIC, 5, (short) 1);
    }

    @Test
    @DisplayName("Provisioning 只检查存在性，已存在 Topic 分区数保持不变")
    void existingTopicConfigurationNotModified() throws Exception {
        KafkaTestSupport.awaitReady(provisioningStatus, Duration.ofSeconds(60));

        assertEquals(5, KafkaTestSupport.partitionsOf(KAFKA.getBootstrapServers(), PRE_BUILT_TOPIC),
                "已存在 Topic 的分区数不应被修改");
    }

    @Test
    @DisplayName("其余 MDM Topic 仍被正常创建")
    void otherTopicsStillCreated() throws Exception {
        KafkaTestSupport.awaitReady(provisioningStatus, Duration.ofSeconds(60));

        assertTrue(KafkaTestSupport.listNonInternalTopics(KAFKA.getBootstrapServers())
                        .containsAll(java.util.Set.of(
                                "mdm.product.model.created",
                                "mdm.eead.vehicleNode.event",
                                "mdm.material.part.event")),
                "其余 MDM Topic 应被正常创建");
    }
}
