package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
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
 * 已存在 Topic 配置不被修改集成测试（MDM-DSN-CR-041 §4.2 / §9.2）
 * <p>
 * 环境中已预建 Topic（分区数与声明不同），MdmKafkaTopicInitializer 仅对差集创建，
 * 不修改已有 Topic 的分区数、副本数或配置。
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
@Testcontainers
@DisplayName("已存在 Topic 配置不被修改集成测试")
class MdmKafkaTopicInitializerExistingConfigTest {

    @Container
    static final MdmKafkaContainer KAFKA = KafkaTestSupport.newKafkaContainer();

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }

    @Autowired
    private MdmKafkaTopicReadiness readiness;

    @MockBean
    private OutboxRepository outboxRepository;

    /**
     * 预建 Topic：分区数为 5（与声明分区数 1 不同）
     */
    private static final String PRE_BUILT_TOPIC = "mdm.brand";

    @BeforeAll
    static void preCreateTopicWithDifferentConfig() throws Exception {
        KafkaTestSupport.preCreateTopic(KAFKA.getBootstrapServers(), PRE_BUILT_TOPIC, 5, (short) 1);
    }

    @Test
    @DisplayName("预检只创建差集，已存在 Topic 分区数保持不变")
    void existingTopicConfigurationNotModified() throws Exception {
        KafkaTestSupport.awaitReady(readiness, Duration.ofSeconds(60));

        assertEquals(MdmKafkaTopicReadiness.State.READY, readiness.state());
        assertEquals(5, KafkaTestSupport.partitionsOf(KAFKA.getBootstrapServers(), PRE_BUILT_TOPIC),
                "已存在 Topic 的分区数不应被修改");
    }

    @Test
    @DisplayName("其余目录生产 Topic 仍被正常创建")
    void otherTopicsStillCreated() throws Exception {
        KafkaTestSupport.awaitReady(readiness, Duration.ofSeconds(60));

        assertTrue(KafkaTestSupport.listNonInternalTopics(KAFKA.getBootstrapServers())
                        .containsAll(java.util.Set.of(
                                "mdm.model",
                                "mdm.vehicle-node",
                                "mdm.part",
                                "mdm.supplier")),
                "其余目录生产 Topic 应被正常创建");
    }
}
