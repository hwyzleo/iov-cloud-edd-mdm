package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PlantHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.PlantDownstreamLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlantRepository;
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
 * 工厂删除领域服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlantDeletionDomainService 测试")
class PlantDeletionDomainServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantDownstreamLookupGateway plantDownstreamLookupGateway;

    private PlantDeletionDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new PlantDeletionDomainService(plantRepository, plantDownstreamLookupGateway);
    }

    @Nested
    @DisplayName("DRAFT 状态直删分支")
    class DraftDirectDeleteTests {

        @Test
        @DisplayName("DRAFT 状态直接物理删除，不调用反查 Gateway")
        void checkAndDelete_draft_directDelete() {
            Plant plant = createTestPlant(PlantStatus.DRAFT);

            domainService.checkAndDelete(plant, "admin", false);

            verify(plantDownstreamLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(plantRepository).delete(eq(plant), eq("admin"), eq(false));
        }
    }

    @Nested
    @DisplayName("force 旁路分支")
    class ForceDeleteTests {

        @Test
        @DisplayName("ACTIVE 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_active_forceDelete() {
            Plant plant = createTestPlant(PlantStatus.ACTIVE);

            domainService.checkAndDelete(plant, "admin", true);

            verify(plantDownstreamLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(plantRepository).delete(eq(plant), eq("admin"), eq(true));
        }

        @Test
        @DisplayName("INACTIVE 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_inactive_forceDelete() {
            Plant plant = createTestPlant(PlantStatus.INACTIVE);

            domainService.checkAndDelete(plant, "admin", true);

            verify(plantDownstreamLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(plantRepository).delete(eq(plant), eq("admin"), eq(true));
        }
    }

    @Nested
    @DisplayName("ACTIVE/INACTIVE 反查分支")
    class LookupTests {

        @Test
        @DisplayName("反查无引用 - 删除成功")
        void checkAndDelete_noReference_deleteSuccess() {
            Plant plant = createTestPlant(PlantStatus.ACTIVE);
            PlantReferenceCheckResult result = PlantReferenceCheckResult.builder()
                    .referenceCount(0)
                    .samples(Collections.emptyList())
                    .available(true)
                    .build();
            when(plantDownstreamLookupGateway.checkReferences("PLT_CN_CD_01", 10)).thenReturn(result);

            domainService.checkAndDelete(plant, "admin", false);

            verify(plantRepository).delete(eq(plant), eq("admin"), eq(false));
        }

        @Test
        @DisplayName("反查有引用 - 抛出 PlantHasDownstreamRefException")
        void checkAndDelete_hasReference_throwsException() {
            Plant plant = createTestPlant(PlantStatus.ACTIVE);
            PlantReferenceCheckResult result = PlantReferenceCheckResult.builder()
                    .referenceCount(5)
                    .referencingService("VMD")
                    .samples(Arrays.asList("VEH001", "VEH002", "VEH003"))
                    .available(true)
                    .build();
            when(plantDownstreamLookupGateway.checkReferences("PLT_CN_CD_01", 10)).thenReturn(result);

            PlantHasDownstreamRefException exception = assertThrows(
                    PlantHasDownstreamRefException.class,
                    () -> domainService.checkAndDelete(plant, "admin", false)
            );

            assertEquals("PLT_CN_CD_01", exception.getPlantCode());
            assertEquals(5, exception.getReferenceCount());
            verify(plantRepository, never()).delete(any(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("反查不可用 - 抛出 VmdServiceUnavailableException")
        void checkAndDelete_unavailable_throwsException() {
            Plant plant = createTestPlant(PlantStatus.ACTIVE);
            PlantReferenceCheckResult result = PlantReferenceCheckResult.unavailable("VMD");
            when(plantDownstreamLookupGateway.checkReferences("PLT_CN_CD_01", 10)).thenReturn(result);

            assertThrows(VmdServiceUnavailableException.class,
                    () -> domainService.checkAndDelete(plant, "admin", false));

            verify(plantRepository, never()).delete(any(), anyString(), anyBoolean());
        }
    }

    private Plant createTestPlant(PlantStatus status) {
        Plant plant = Plant.create(
                "PLT_CN_CD_01", "成都工厂", "Chengdu Plant", "成都", "整车总装工厂",
                PlantType.VEHICLE_ASSEMBLY, null, null,
                null, null, null, null,
                null, null, null,
                null, null, null, null,
                null, null, "admin"
        );
        plant.setStatus(status);
        return plant;
    }
}
