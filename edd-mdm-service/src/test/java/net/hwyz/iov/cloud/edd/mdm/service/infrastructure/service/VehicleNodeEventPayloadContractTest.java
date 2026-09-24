package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VehicleNodeCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VehicleNodeUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * VehicleNode 事件契约测试（VMD-DSN-CR-049 §2.1 / §8 契约矩阵）
 * <p>
 * 锁定：Created/Updated 事件 payload 为完整聚合，SHALL 携带 hsmCapability 与 deviceCategory，
 * 且序列化 JSON 对下游消费者（VMD MdmVehicleNodeEvent）可解析；能力/类别变更时发布完整新值。
 * </p>
 *
 * @author hwyz_leo
 * @since 2026-09-22
 */
@DisplayName("VehicleNode 事件契约测试（CR-049）")
class VehicleNodeEventPayloadContractTest {

    private OutboxRepository outboxRepository;
    private OutboxService outboxService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        outboxRepository = mock(OutboxRepository.class);
        outboxService = new OutboxServiceImpl(outboxRepository);
    }

    private VehicleNode vehicleNode(String code, String deviceCategory, HsmCapability hsmCapability, Integer version) {
        return VehicleNode.create(
                code, code, code, null,
                NodeType.ECU, FunctionalDomain.CONNECTIVITY, deviceCategory,
                false, OtaSupportType.BOTH, hsmCapability, null,
                null, null, "test");
    }

    @Test
    @DisplayName("Created 事件 payload 应携带 hsmCapability 与 deviceCategory")
    void createdEvent_payloadShouldCarryHsmCapabilityAndDeviceCategory() throws Exception {
        VehicleNode node = vehicleNode("CCU_GEN2", "CCU", HsmCapability.HSM_FULL, 12);

        outboxService.publishVehicleNodeCreatedEvent(node);

        ArgumentCaptor<VehicleNodeCreatedEvent> captor = ArgumentCaptor.forClass(VehicleNodeCreatedEvent.class);
        verify(outboxRepository).saveVehicleNodeCreatedEvent(captor.capture());
        VehicleNodeCreatedEvent event = captor.getValue();

        assertEquals("VehicleNodeCreated", event.getEventType());
        assertEquals("CCU_GEN2", event.getEntityId());
        VehicleNode payload = (VehicleNode) event.getPayload();
        assertEquals("CCU", payload.getDeviceCategory());
        assertEquals(HsmCapability.HSM_FULL, payload.getHsmCapability());

        // 序列化 JSON 对下游消费者可解析（含 hsmCapability/deviceCategory 字段）
        String json = objectMapper.writeValueAsString(payload);
        assertTrue(json.contains("\"hsmCapability\":\"HSM_FULL\""), "事件 payload JSON 应包含 hsmCapability");
        assertTrue(json.contains("\"deviceCategory\":\"CCU\""), "事件 payload JSON 应包含 deviceCategory");
    }

    @Test
    @DisplayName("Updated 事件 payload 应携带最新 hsmCapability/deviceCategory（能力变更时发布完整新值）")
    void updatedEvent_payloadShouldCarryLatestValues() throws Exception {
        VehicleNode node = vehicleNode("TBOX_5G", "TBOX", HsmCapability.HSM_LIGHT, 15);

        outboxService.publishVehicleNodeUpdatedEvent(node);

        ArgumentCaptor<VehicleNodeUpdatedEvent> captor = ArgumentCaptor.forClass(VehicleNodeUpdatedEvent.class);
        verify(outboxRepository).saveVehicleNodeUpdatedEvent(captor.capture());
        VehicleNodeUpdatedEvent event = captor.getValue();

        assertEquals("VehicleNodeUpdated", event.getEventType());
        assertEquals("TBOX_5G", event.getEntityId());
        VehicleNode payload = (VehicleNode) event.getPayload();
        assertEquals("TBOX", payload.getDeviceCategory());
        assertEquals(HsmCapability.HSM_LIGHT, payload.getHsmCapability());

        String json = objectMapper.writeValueAsString(payload);
        assertTrue(json.contains("\"hsmCapability\":\"HSM_LIGHT\""));
        assertTrue(json.contains("\"deviceCategory\":\"TBOX\""));
    }
}
