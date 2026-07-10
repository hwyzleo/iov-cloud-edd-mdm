package net.hwyz.iov.cloud.edd.mdm.service.adapter.task.scheduler;

import net.hwyz.iov.cloud.edd.mdm.service.application.port.gateway.KafkaEventGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
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
 * 验证 topic 路由规则：
 * - Product 子域（多 topic）：eventType 直接作为 topic
 * - Party 子域（多 topic）：eventType 直接作为 topic
 * - EEAD 子域（单 topic）：aggregateType 映射到单一 topic
 * - Org 子域（单 topic）：aggregateType 映射到单一 topic
 * - Material 子域（单 topic）：aggregateType 映射到单一 topic
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

    private OutboxRelayScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new OutboxRelayScheduler(outboxRepository, kafkaEventGateway);
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
        @DisplayName("车载节点更新事件 → mdm.eead.vehicleNode.event")
        void relayEvents_vehicleNodeUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("VEHICLE_NODE", "VehicleNodeUpdated", "TBOX");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.vehicleNode.event"), eq("TBOX"), anyString());
        }

        @Test
        @DisplayName("车载节点删除事件 → mdm.eead.vehicleNode.event")
        void relayEvents_vehicleNodeDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("VEHICLE_NODE", "VehicleNodeDeleted", "TBOX");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.vehicleNode.event"), eq("TBOX"), anyString());
        }

        @Test
        @DisplayName("设备类别创建事件 → mdm.eead.deviceCategory.event")
        void relayEvents_deviceCategoryCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("DEVICE_CATEGORY", "DeviceCategoryCreated", "DC001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.deviceCategory.event"), eq("DC001"), anyString());
        }

        @Test
        @DisplayName("设备类别更新事件 → mdm.eead.deviceCategory.event")
        void relayEvents_deviceCategoryUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("DEVICE_CATEGORY", "DeviceCategoryUpdated", "DC001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.deviceCategory.event"), eq("DC001"), anyString());
        }

        @Test
        @DisplayName("设备类别删除事件 → mdm.eead.deviceCategory.event")
        void relayEvents_deviceCategoryDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("DEVICE_CATEGORY", "DeviceCategoryDeleted", "DC001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.eead.deviceCategory.event"), eq("DC001"), anyString());
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

        @Test
        @DisplayName("工厂更新事件 → mdm.org.plant.event")
        void relayEvents_plantUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PLANT", "PlantUpdated", "PLT_CN_CD_01");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.org.plant.event"), eq("PLT_CN_CD_01"), anyString());
        }

        @Test
        @DisplayName("工厂删除事件 → mdm.org.plant.event")
        void relayEvents_plantDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PLANT", "PlantDeleted", "PLT_CN_CD_01");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.org.plant.event"), eq("PLT_CN_CD_01"), anyString());
        }
    }

    @Nested
    @DisplayName("Material 子域 - 单 topic 映射")
    class MaterialTopicTests {

        @Test
        @DisplayName("物料分类创建事件 → mdm.material.category.event")
        void relayEvents_materialCategoryCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("MATERIAL_CATEGORY", "MaterialCategoryCreated", "CAT001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.category.event"), eq("CAT001"), anyString());
        }

        @Test
        @DisplayName("物料分类更新事件 → mdm.material.category.event")
        void relayEvents_materialCategoryUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("MATERIAL_CATEGORY", "MaterialCategoryUpdated", "CAT001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.category.event"), eq("CAT001"), anyString());
        }

        @Test
        @DisplayName("物料分类删除事件 → mdm.material.category.event")
        void relayEvents_materialCategoryDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("MATERIAL_CATEGORY", "MaterialCategoryDeleted", "CAT001");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.category.event"), eq("CAT001"), anyString());
        }

        @Test
        @DisplayName("零件创建事件 → mdm.material.part.event")
        void relayEvents_partCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PART", "PartCreated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("零件更新事件 → mdm.material.part.event")
        void relayEvents_partUpdated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PART", "PartUpdated", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("零件删除事件 → mdm.material.part.event")
        void relayEvents_partDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("PART", "PartDeleted", "00000001AA");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.part.event"), eq("00000001AA"), anyString());
        }

        @Test
        @DisplayName("软件基线创建事件 -> mdm.material.softwareBaseline.event")
        void relayEvents_softwareBaselineCreated_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("SOFTWARE_BASELINE", "SoftwareBaselineCreated", "SWB-V1");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.softwareBaseline.event"), eq("SWB-V1"), anyString());
        }

        @Test
        @DisplayName("软件基线发布事件 -> mdm.material.softwareBaseline.event")
        void relayEvents_softwareBaselineReleased_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("SOFTWARE_BASELINE", "SoftwareBaselineReleased", "SWB-V1");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.softwareBaseline.event"), eq("SWB-V1"), anyString());
        }

        @Test
        @DisplayName("软件基线删除事件 -> mdm.material.softwareBaseline.event")
        void relayEvents_softwareBaselineDeleted_routesToSingleTopic() {
            OutboxPo event = buildOutboxPo("SOFTWARE_BASELINE", "SoftwareBaselineDeleted", "SWB-V1");
            when(outboxRepository.findPendingEvents(100)).thenReturn(Collections.singletonList(event));

            scheduler.relayEvents();

            verify(kafkaEventGateway).send(eq("mdm.material.softwareBaseline.event"), eq("SWB-V1"), anyString());
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
