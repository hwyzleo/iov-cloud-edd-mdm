package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.VehicleNodeHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.VehiclePartReverseLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehicleNodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehiclePartReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 车载节点删除领域服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleNodeDeletionDomainService 测试")
class VehicleNodeDeletionDomainServiceTest {

    @Mock
    private VehicleNodeRepository vehicleNodeRepository;
    @Mock
    private SwinManagedSystemRepository swinManagedSystemRepository;

    @Mock
    private VehiclePartReverseLookupGateway vehiclePartReverseLookupGateway;

    private VehicleNodeDeletionDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new VehicleNodeDeletionDomainService(vehicleNodeRepository, swinManagedSystemRepository, vehiclePartReverseLookupGateway);
    }

    @Nested
    @DisplayName("DRAFT 状态直删分支")
    class DraftDirectDeleteTests {

        @Test
        @DisplayName("DRAFT 状态直接物理删除，不调用反查 Gateway")
        void checkAndDelete_draft_directDelete() {
            VehicleNode node = createTestNode(VehicleNodeStatus.DRAFT);

            domainService.checkAndDelete(node, "admin", false);

            verify(vehiclePartReverseLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(vehicleNodeRepository).delete(eq(node), eq("admin"), eq(false));
        }
    }

    @Nested
    @DisplayName("force 旁路分支")
    class ForceDeleteTests {

        @Test
        @DisplayName("ACTIVE 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_active_forceDelete() {
            VehicleNode node = createTestNode(VehicleNodeStatus.ACTIVE);

            domainService.checkAndDelete(node, "admin", true);

            verify(vehiclePartReverseLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(vehicleNodeRepository).delete(eq(node), eq("admin"), eq(true));
        }

        @Test
        @DisplayName("INACTIVE 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_inactive_forceDelete() {
            VehicleNode node = createTestNode(VehicleNodeStatus.INACTIVE);

            domainService.checkAndDelete(node, "admin", true);

            verify(vehiclePartReverseLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(vehicleNodeRepository).delete(eq(node), eq("admin"), eq(true));
        }
    }

    @Nested
    @DisplayName("ACTIVE/INACTIVE 反查分支")
    class LookupTests {

        @Test
        @DisplayName("反查无引用 - 删除成功")
        void checkAndDelete_noReference_deleteSuccess() {
            VehicleNode node = createTestNode(VehicleNodeStatus.ACTIVE);
            when(swinManagedSystemRepository.countByVehicleNodeCode("TBOX")).thenReturn(0L);
            VehiclePartReferenceCheckResult result = VehiclePartReferenceCheckResult.builder()
                    .referenceCount(0)
                    .samples(Collections.emptyList())
                    .available(true)
                    .build();
            when(vehiclePartReverseLookupGateway.checkReferences("TBOX", 10)).thenReturn(result);

            domainService.checkAndDelete(node, "admin", false);

            verify(vehicleNodeRepository).delete(eq(node), eq("admin"), eq(false));
        }

        @Test
        @DisplayName("反查有引用 - 抛出 VehicleNodeHasDownstreamRefException")
        void checkAndDelete_hasReference_throwsException() {
            VehicleNode node = createTestNode(VehicleNodeStatus.ACTIVE);
            when(swinManagedSystemRepository.countByVehicleNodeCode("TBOX")).thenReturn(0L);
            VehiclePartReferenceCheckResult result = VehiclePartReferenceCheckResult.builder()
                    .referenceCount(5)
                    .referencingService("VMD")
                    .samples(Arrays.asList("PART001", "PART002", "PART003"))
                    .available(true)
                    .build();
            when(vehiclePartReverseLookupGateway.checkReferences("TBOX", 10)).thenReturn(result);

            VehicleNodeHasDownstreamRefException exception = assertThrows(
                    VehicleNodeHasDownstreamRefException.class,
                    () -> domainService.checkAndDelete(node, "admin", false)
            );

            assertEquals("TBOX", exception.getNodeCode());
            assertEquals(5, exception.getReferenceCount());
            verify(vehicleNodeRepository, never()).delete(any(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("反查不可用 - 抛出 VmdServiceUnavailableException")
        void checkAndDelete_unavailable_throwsException() {
            VehicleNode node = createTestNode(VehicleNodeStatus.ACTIVE);
            when(swinManagedSystemRepository.countByVehicleNodeCode("TBOX")).thenReturn(0L);
            VehiclePartReferenceCheckResult result = VehiclePartReferenceCheckResult.unavailable("VMD");
            when(vehiclePartReverseLookupGateway.checkReferences("TBOX", 10)).thenReturn(result);

            assertThrows(VmdServiceUnavailableException.class,
                    () -> domainService.checkAndDelete(node, "admin", false));

            verify(vehicleNodeRepository, never()).delete(any(), anyString(), anyBoolean());
        }
    }

    private VehicleNode createTestNode(VehicleNodeStatus status) {
        VehicleNode node = VehicleNode.create(
                "TBOX", "远程信息处理器", "Telematics Box", "车载通讯终端",
                NodeType.TELEMATICS, FunctionalDomain.CONNECTIVITY, "T-BOX",
                true, OtaSupportType.BOTH,
                null, null,
                null, null, "admin"
        );
        node.setStatus(status);
        return node;
    }
}
