package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * broker 暂时不可用场景集成测试（MDM-DSN-CR-041 §4.1 / §7）
 * <p>
 * broker 指向不可达地址且 fail-fast=false 时：服务保持正常就绪，
 * MdmKafkaTopicReadiness 为 NOT_READY，Outbox Relay 暂停
 * （不查询 mdm_outbox、不增加 retry_count）。
 *
 * @author hwyz_leo
 */
@SpringBootTest(
        classes = KafkaTopicInitializationTestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "edd.mdm.kafka.topic-initialization.enabled=true",
                "edd.mdm.kafka.topic-initialization.create-missing-producer-topics=true",
                "edd.mdm.kafka.topic-initialization.fail-fast=false",
                "spring.kafka.bootstrap-servers=127.0.0.1:1",
                "spring.kafka.admin.properties.request.timeout.ms=3000",
                "spring.kafka.admin.properties.default.api.timeout.ms=3000"
        })
@DisplayName("broker 不可用场景集成测试")
class MdmKafkaTopicInitializerBrokerDownTest {

    @Autowired
    private MdmKafkaTopicReadiness readiness;

    @Autowired
    private OutboxRelayScheduler outboxRelayScheduler;

    @MockBean
    private OutboxRepository outboxRepository;

    @Test
    @DisplayName("broker 不可用时服务保持就绪，状态 NOT_READY，Relay 暂停不扫描")
    void brokerUnavailable_serviceReady_relayPaused() {
        // 服务正常启动（上下文已加载）且状态为 NOT_READY
        assertNotNull(readiness);
        assertEquals(MdmKafkaTopicReadiness.State.NOT_READY, readiness.state());
        assertTrue(readiness.snapshot().lastFailure().isPresent(), "预检失败应记录失败原因");
        assertEquals(19, readiness.snapshot().missingProducerTopics().size(),
                "缺失清单应包含全部生产 Topic");

        // Relay 门禁：NOT_READY 时不查询 mdm_outbox
        outboxRelayScheduler.relayEvents();
        verify(outboxRepository, never()).findPendingEvents(anyInt());
        verify(outboxRepository, never()).incrementRetryCount(anyString());
    }
}
