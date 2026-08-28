package net.hwyz.iov.cloud.edd.mdm.test.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler.OutboxRelayScheduler;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
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
 * broker 暂时不可用场景集成测试（MDM-DSN-CR-034）
 * <p>
 * broker 指向不可达地址时：服务保持正常就绪，Provisioning 状态为 NOT_READY，
 * Outbox Relay 暂停（不查询 mdm_outbox、不增加 retry_count、不投递 DLQ）。
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
                "spring.kafka.bootstrap-servers=127.0.0.1:1"
        })
@DisplayName("broker 不可用场景集成测试")
class KafkaTopicProvisioningBrokerDownTest {

    @Autowired
    private KafkaTopicProvisioningStatus provisioningStatus;

    @Autowired
    private OutboxRelayScheduler outboxRelayScheduler;

    @MockBean
    private OutboxRepository outboxRepository;

    @Test
    @DisplayName("broker 不可用时服务保持就绪，状态 NOT_READY，Relay 暂停不扫描")
    void brokerUnavailable_serviceReady_relayPaused() throws Exception {
        // 服务正常启动（上下文已加载）且状态为 NOT_READY
        assertNotNull(provisioningStatus);
        assertEquals(KafkaTopicProvisioningStatus.State.NOT_READY, provisioningStatus.state());

        // 等待后台重试发生，状态保持 NOT_READY 并记录失败原因
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline) {
            if (provisioningStatus.lastFailure().isPresent()) {
                break;
            }
            Thread.sleep(500);
        }
        assertTrue(provisioningStatus.lastFailure().isPresent(), "后台重试后应记录 broker 失败原因");
        assertEquals(KafkaTopicProvisioningStatus.State.NOT_READY, provisioningStatus.state());

        // Relay 门禁：NOT_READY 时不查询 mdm_outbox
        outboxRelayScheduler.relayEvents();
        verify(outboxRepository, never()).findPendingEvents(anyInt());
        verify(outboxRepository, never()).incrementRetryCount(anyString());
    }
}
