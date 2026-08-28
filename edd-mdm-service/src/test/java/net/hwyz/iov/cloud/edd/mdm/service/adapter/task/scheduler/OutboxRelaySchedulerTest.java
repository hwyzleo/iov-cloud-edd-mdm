package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicCatalog;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicProvisioningStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 事件发件箱Relay定时任务单元测试
 * <p>
 * 验证 topic 路由规则与 Provisioning 门禁（MDM-DSN-CR-034）：
 * - Product 子域（多 topic）：eventType 直接作为 topic
 * - Party 子域（多 topic）：eventType 直接作为 topic
 * - EEAD / Org / Material 子域（单 topic）：aggregateType 映射到固定 topic
 * - NOT_READY 暂停、READY 恢复、DISABLED 兼容路径
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxRelayScheduler 测试")
class OutboxRelaySchedulerTest {

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private KafkaEventGateway kafkaEventGateway;

    @Mock
    private KafkaTopicProvisioningStatus provisioningStatus;

    private KafkaTopicResolver kafkaTopicResolver;
    private OutboxRelayScheduler scheduler;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        KafkaTopicCatalog catalog = mock(KafkaTopicCatalog.class);
        lenient().when(catalog.contains(anyString())).thenReturn(true);
        ObjectProvider<KafkaTopicCatalog> provider = mock(ObjectProvider.class);
        lenient().when(provider.getIfAvailable()).thenReturn(catalog);
        kafkaTopicResolver = new KafkaTopicResolver(provider);

        scheduler = new OutboxRelayScheduler(outboxRepository, kafkaEventGateway, kafkaTopicResolver, provisioningStatus);
        when(provisioningStatus.state()).thenReturn(KafkaTopicProvisioningStatus.State.READY);
    }

    @Nested
    @DisplayName("Product 子域 - eventType 直接作为 topic")
    class ProductTopicTests {

        @Test
        @DisplayName("品牌创建事件 → mdm.product.brand.created")
        void relayEvents_brandCreated_usesEventTypeAsTopic() {
            OutboxPo event = buildOutboxPo("BRAND", "mdm.product.brand.created", "BRAND_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.product.brand.created"), eq("BRAND_001"), anyString());
            verify(outboxRepository).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("车系更新事件 → mdm.product.carLine.updated")
        void relayEvents_carLineUpdated_usesEventTypeAsTopic() {
            OutboxPo event = buildOutboxPo("CAR_LINE", "mdm.product.carLine.updated", "CARLINE_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.product.carLine.updated"), eq("CARLINE_001"), anyString());
        }

        @Test
        @DisplayName("平台失效事件 → mdm.product.platform.deactivated")
        void relayEvents_platformDeactivated_usesEventTypeAsTopic() {
            OutboxPo event = buildOutboxPo("PLATFORM", "mdm.product.platform.deactivated", "PLATFORM_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.product.platform.deactivated"), eq("PLATFORM_001"), anyString());
        }
    }

    @Nested
    @DisplayName("Party 子域 - eventType 直接作为 topic")
    class PartyTopicTests {

        @Test
        @DisplayName("供应商创建事件 → mdm.party.supplier.created")
        void relayEvents_supplierCreated_usesEventTypeAsTopic() {
            OutboxPo event = buildOutboxPo("SUPPLIER", "mdm.party.supplier.created", "SUPPLIER_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.party.supplier.created"), eq("SUPPLIER_001"), anyString());
        }
    }

    @Nested
    @DisplayName("EEAD 子域 - 单 topic 映射")
    class EeadTopicTests {

        @Test
        @DisplayName("车载节点创建事件 → mdm.eead.vehicleNode.event")
        void relayEvents_vehicleNodeCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("VEHICLE_NODE", "VehicleNodeCreated", "TBOX");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.vehicleNode.event"), eq("TBOX"), anyString());
        }

        @Test
        @DisplayName("设备类别删除事件 → mdm.eead.deviceCategory.event")
        void relayEvents_deviceCategoryDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("DEVICE_CATEGORY", "DeviceCategoryDeleted", "DC001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.deviceCategory.event"), eq("DC001"), anyString());
        }

        @Test
        @DisplayName("SWIN 方案创建事件 → mdm.eead.swinScheme.event")
        void relayEvents_swinSchemeCreated_routesToSwinSchemeTopic() {
            OutboxPo event = buildOutboxPo("SWIN_SCHEME", "SwinSchemeCreated", "SCHEME_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.swinScheme.event"), eq("SCHEME_001"), anyString());
        }

        @Test
        @DisplayName("TA 基线发布事件 → mdm.eead.typeApprovalBaseline.event")
        void relayEvents_taBaselineReleased_routesToTypeApprovalBaselineTopic() {
            OutboxPo event = buildOutboxPo("TYPE_APPROVAL_BASELINE", "TypeApprovalBaselineReleased", "TAB_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.typeApprovalBaseline.event"), eq("TAB_001"), anyString());
        }
    }

    @Nested
    @DisplayName("Org 子域 - 单 topic 映射")
    class OrgTopicTests {

        @Test
        @DisplayName("工厂创建事件 → mdm.org.plant.event")
        void relayEvents_plantCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PLANT", "PlantCreated", "PLT_CN_CD_01");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.org.plant.event"), eq("PLT_CN_CD_01"), anyString());
        }
    }

    @Nested
    @DisplayName("Material 子域 - 单 topic 映射")
    class MaterialTopicTests {

        @Test
        @DisplayName("零件更新事件 → mdm.material.part.event")
        void relayEvents_partUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PART", "PartUpdated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("软件基线发布事件 → mdm.material.softwareBaseline.event")
        void relayEvents_softwareBaselineReleased_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("SOFTWARE_BASELINE", "SoftwareBaselineReleased", "SWB-V1");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.softwareBaseline.event"), eq("SWB-V1"), anyString());
        }
    }

    @Nested
    @DisplayName("Provisioning 门禁")
    class ProvisioningGateTests {

        @Test
        @DisplayName("NOT_READY 时不查询 outbox、不发送、不增加重试")
        void relayEvents_notReady_skipsRound() {
            when(provisioningStatus.state()).thenReturn(KafkaTopicProvisioningStatus.State.NOT_READY);

            scheduler.relayEvents();

            verify(outboxRepository, never()).findPendingEvents(anyInt());
            verify(kafkaEventGateway, never()).send(anyString(), anyString(), anyString());
            verify(outboxRepository, never()).incrementRetryCount(anyString());
        }

        @Test
        @DisplayName("READY 后恢复扫描与发送")
        void relayEvents_ready_resumesScanAndSend() {
            when(provisioningStatus.state()).thenReturn(KafkaTopicProvisioningStatus.State.READY);
            OutboxPo event = buildOutboxPo("BRAND", "mdm.product.brand.created", "BRAND_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(outboxRepository).findPendingEvents(100);
            verify(kafkaEventGateway).send(eq("mdm.product.brand.created"), eq("BRAND_001"), anyString());
            verify(outboxRepository).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("DISABLED 走既有兼容路径（照常扫描发送）")
        void relayEvents_disabled_usesLegacyPath() {
            when(provisioningStatus.state()).thenReturn(KafkaTopicProvisioningStatus.State.DISABLED);
            OutboxPo event = buildOutboxPo("PART", "PartCreated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(outboxRepository).findPendingEvents(100);
            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("topic 未登记时跳过发送且不增加重试")
        void relayEvents_unresolvableTopic_skipsSendWithoutRetry() {
            KafkaTopicCatalog catalog = mock(KafkaTopicCatalog.class);
            when(catalog.contains(anyString())).thenReturn(false);
            ObjectProvider<KafkaTopicCatalog> provider = mock(ObjectProvider.class);
            when(provider.getIfAvailable()).thenReturn(catalog);
            scheduler = new OutboxRelayScheduler(outboxRepository, kafkaEventGateway,
                    new KafkaTopicResolver(provider), provisioningStatus);
            when(provisioningStatus.state()).thenReturn(KafkaTopicProvisioningStatus.State.READY);
            OutboxPo event = buildOutboxPo("BRAND", "mdm.product.brand.created", "BRAND_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway, never()).send(anyString(), anyString(), anyString());
            verify(outboxRepository, never()).markEventAsSent(anyString());
            verify(outboxRepository, never()).incrementRetryCount(anyString());
        }
    }

    @Nested
    @DisplayName("批量与异常场景")
    class BatchAndErrorTests {

        @Test
        @DisplayName("无待发送事件时不调用 Kafka")
        void relayEvents_noPendingEvents_doesNotCallKafka() {
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.emptyList());

            scheduler.relayEvents();

            verify(kafkaEventGateway, never()).send(anyString(), anyString(), anyString());
            verify(outboxRepository, never()).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("批量事件按顺序发送")
        void relayEvents_multipleEvents_sendsAll() {
            OutboxPo event1 = buildOutboxPo("BRAND", "mdm.product.brand.created", "BRAND_001");
            OutboxPo event2 = buildOutboxPo("VEHICLE_NODE", "VehicleNodeCreated", "TBOX");
            OutboxPo event3 = buildOutboxPo("PART", "PartCreated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Arrays.asList(event1, event2, event3));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.product.brand.created"), eq("BRAND_001"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.eead.vehicleNode.event"), eq("TBOX"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
            verify(outboxRepository, times(3)).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("发送失败时增加重试次数不中断后续事件")
        void relayEvents_sendFails_continuesWithNextEvent() {
            OutboxPo event1 = buildOutboxPo("BRAND", "mdm.product.brand.created", "BRAND_001");
            event1.setRetryCount(0);
            OutboxPo event2 = buildOutboxPo("PART", "PartCreated", "00000001AA");
            event2.setRetryCount(0);
            when(outboxRepository.findPendingEvents(100)).thenReturn(Arrays.asList(event1, event2));
            doThrow(new RuntimeException("Kafka 连接失败"))
                    .when(kafkaEventGateway).send(eq("mdm.product.brand.created"), anyString(), anyString());

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.product.brand.created"), eq("BRAND_001"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
            verify(outboxRepository).incrementRetryCount(eq(String.valueOf(event1.getId())));
            verify(outboxRepository).markEventAsSent(eq(String.valueOf(event2.getId())));
        }
    }

    private OutboxPo buildOutboxPo(String aggregateType, String eventType, String aggregateId) {
        return OutboxPo.builder()
                .id(1L)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload("{}")
                .occurredAt(new Date())
                .sent(false)
                .retryCount(0)
                .build();
    }
}
