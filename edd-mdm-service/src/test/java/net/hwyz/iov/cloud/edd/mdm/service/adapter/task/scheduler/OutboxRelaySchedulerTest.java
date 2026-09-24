package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.KafkaTopicResolver;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicReadiness;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 事件发件箱Relay定时任务单元测试
 * <p>
 * 验证 Topic 路由与就绪门禁（MDM-DSN-CR-041 F29）：
 * - 19 个聚合类型经 KafkaTopicResolver 路由到 Kafka Topic 目录对应 topic
 * - UNKNOWN / NOT_READY 暂停、READY 恢复、DISABLED 兼容路径
 * - 未登记聚合类型跳过发送且不增加重试
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
    private MdmKafkaTopicReadiness readiness;

    private KafkaTopicResolver kafkaTopicResolver;
    private OutboxRelayScheduler scheduler;

    @BeforeEach
    void setUp() {
        kafkaTopicResolver = new KafkaTopicResolver(new MdmKafkaTopicProperties());
        scheduler = new OutboxRelayScheduler(outboxRepository, kafkaEventGateway, kafkaTopicResolver, readiness);
        when(readiness.state()).thenReturn(MdmKafkaTopicReadiness.State.READY);
    }

    @Nested
    @DisplayName("Product 子域")
    class ProductTopicTests {

        @Test
        @DisplayName("品牌创建事件 → mdm.brand")
        void relayEvents_brandCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("BRAND", "BrandCreated", "BRAND_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.brand"), eq("BRAND_001"), anyString());
            verify(outboxRepository).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("车系更新事件 → mdm.car-line")
        void relayEvents_carLineUpdated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("CAR_LINE", "CarLineUpdated", "CARLINE_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.car-line"), eq("CARLINE_001"), anyString());
        }

        @Test
        @DisplayName("平台失效事件 → mdm.platform")
        void relayEvents_platformDeactivated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("PLATFORM", "PlatformDeactivated", "PLATFORM_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.platform"), eq("PLATFORM_001"), anyString());
        }
    }

    @Nested
    @DisplayName("Party 子域")
    class PartyTopicTests {

        @Test
        @DisplayName("供应商创建事件 → mdm.supplier")
        void relayEvents_supplierCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("SUPPLIER", "SupplierCreated", "SUPPLIER_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.supplier"), eq("SUPPLIER_001"), anyString());
        }
    }

    @Nested
    @DisplayName("EEAD 子域")
    class EeadTopicTests {

        @Test
        @DisplayName("车载节点创建事件 → mdm.vehicle-node")
        void relayEvents_vehicleNodeCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("VEHICLE_NODE", "VehicleNodeCreated", "TBOX");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.vehicle-node"), eq("TBOX"), anyString());
        }

        @Test
        @DisplayName("设备类别删除事件 → mdm.device-category")
        void relayEvents_deviceCategoryDeleted_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("DEVICE_CATEGORY", "DeviceCategoryDeleted", "DC001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.device-category"), eq("DC001"), anyString());
        }

        @Test
        @DisplayName("SWIN 方案创建事件 → mdm.swin-scheme")
        void relayEvents_swinSchemeCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("SWIN_SCHEME", "SwinSchemeCreated", "SCHEME_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.swin-scheme"), eq("SCHEME_001"), anyString());
        }

        @Test
        @DisplayName("SWIN 定义补发事件 → mdm.swin-definition")
        void relayEvents_swinDefinitionRepublish_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("SWIN_DEFINITION", "SwinDefinitionUpdated", "SWIN_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.swin-definition"), eq("SWIN_001"), anyString());
        }

        @Test
        @DisplayName("RXSWIN 登记创建事件 → mdm.rxswin")
        void relayEvents_rxswinRegistryCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("RXSWIN_REGISTRY", "RxswinRegistryCreated", "RXSWIN_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.rxswin"), eq("RXSWIN_001"), anyString());
        }

        @Test
        @DisplayName("TA 基线发布事件 → mdm.type-approval-baseline")
        void relayEvents_taBaselineReleased_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("TYPE_APPROVAL_BASELINE", "TypeApprovalBaselineReleased", "TAB_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.type-approval-baseline"), eq("TAB_001"), anyString());
        }
    }

    @Nested
    @DisplayName("Org 子域")
    class OrgTopicTests {

        @Test
        @DisplayName("工厂创建事件 → mdm.plant")
        void relayEvents_plantCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("PLANT", "PlantCreated", "PLT_CN_CD_01");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.plant"), eq("PLT_CN_CD_01"), anyString());
        }
    }

    @Nested
    @DisplayName("Material 子域")
    class MaterialTopicTests {

        @Test
        @DisplayName("零件更新事件 → mdm.part")
        void relayEvents_partUpdated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("PART", "PartUpdated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.part"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("物料分类创建事件 → mdm.material-category")
        void relayEvents_materialCategoryCreated_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("MATERIAL_CATEGORY", "MaterialCategoryCreated", "MC_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material-category"), eq("MC_001"), anyString());
        }

        @Test
        @DisplayName("软件基线发布事件 → mdm.software-baseline")
        void relayEvents_softwareBaselineReleased_routesToCatalogTopic() {
            OutboxPo event = buildOutboxPo("SOFTWARE_BASELINE", "SoftwareBaselineReleased", "SWB-V1");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.software-baseline"), eq("SWB-V1"), anyString());
        }
    }

    @Nested
    @DisplayName("就绪门禁")
    class ReadinessGateTests {

        @Test
        @DisplayName("UNKNOWN 时不查询 outbox、不发送、不增加重试")
        void relayEvents_unknown_skipsRound() {
            when(readiness.state()).thenReturn(MdmKafkaTopicReadiness.State.UNKNOWN);
            stubSnapshot();

            scheduler.relayEvents();

            verify(outboxRepository, never()).findPendingEvents(anyInt());
            verify(kafkaEventGateway, never()).send(anyString(), anyString(), anyString());
            verify(outboxRepository, never()).incrementRetryCount(anyString());
        }

        @Test
        @DisplayName("NOT_READY 时不查询 outbox、不发送、不增加重试")
        void relayEvents_notReady_skipsRound() {
            when(readiness.state()).thenReturn(MdmKafkaTopicReadiness.State.NOT_READY);
            stubSnapshot();

            scheduler.relayEvents();

            verify(outboxRepository, never()).findPendingEvents(anyInt());
            verify(kafkaEventGateway, never()).send(anyString(), anyString(), anyString());
            verify(outboxRepository, never()).incrementRetryCount(anyString());
        }

        private void stubSnapshot() {
            when(readiness.snapshot()).thenReturn(new MdmKafkaTopicReadiness.Snapshot(
                    MdmKafkaTopicReadiness.State.NOT_READY, java.util.Set.of("mdm.brand"), java.util.Set.of(),
                    19, 0, true, java.time.Instant.now(), java.util.Optional.empty()));
        }

        @Test
        @DisplayName("READY 后恢复扫描与发送")
        void relayEvents_ready_resumesScanAndSend() {
            OutboxPo event = buildOutboxPo("BRAND", "BrandCreated", "BRAND_001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(outboxRepository).findPendingEvents(100);
            verify(kafkaEventGateway).send(eq("mdm.brand"), eq("BRAND_001"), anyString());
            verify(outboxRepository).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("DISABLED 走兼容路径（照常扫描发送）")
        void relayEvents_disabled_usesCompatibilityPath() {
            when(readiness.state()).thenReturn(MdmKafkaTopicReadiness.State.DISABLED);
            OutboxPo event = buildOutboxPo("PART", "PartCreated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(outboxRepository).findPendingEvents(100);
            verify(kafkaEventGateway).send(eq("mdm.part"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("未登记聚合类型时跳过发送且不增加重试")
        void relayEvents_unknownAggregateType_skipsSendWithoutRetry() {
            OutboxPo event = buildOutboxPo("UNKNOWN_ENTITY", "WhateverHappened", "X001");
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
        @DisplayName("批量事件按顺序发送到目录 topic")
        void relayEvents_multipleEvents_sendsAll() {
            OutboxPo event1 = buildOutboxPo("BRAND", "BrandCreated", "BRAND_001");
            OutboxPo event2 = buildOutboxPo("VEHICLE_NODE", "VehicleNodeCreated", "TBOX");
            OutboxPo event3 = buildOutboxPo("PART", "PartCreated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Arrays.asList(event1, event2, event3));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.brand"), eq("BRAND_001"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.vehicle-node"), eq("TBOX"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.part"), eq("00000001AA"), anyString());
            verify(outboxRepository, times(3)).markEventAsSent(anyString());
        }

        @Test
        @DisplayName("发送失败时增加重试次数不中断后续事件")
        void relayEvents_sendFails_continuesWithNextEvent() {
            OutboxPo event1 = buildOutboxPo("BRAND", "BrandCreated", "BRAND_001");
            event1.setRetryCount(0);
            OutboxPo event2 = buildOutboxPo("PART", "PartCreated", "00000001AA");
            event2.setRetryCount(0);
            when(outboxRepository.findPendingEvents(100)).thenReturn(Arrays.asList(event1, event2));
            doThrow(new RuntimeException("Kafka 连接失败"))
                    .when(kafkaEventGateway).send(eq("mdm.brand"), anyString(), anyString());

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.brand"), eq("BRAND_001"), anyString());
            verify(kafkaEventGateway).send(eq("mdm.part"), eq("00000001AA"), anyString());
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
