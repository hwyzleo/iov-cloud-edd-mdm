package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineHasDownstreamRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.VmdServiceUnavailableException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.OtaBaselineLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaBaselineReferenceCheckResult;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 软件基线删除领域服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SoftwareBaselineDeletionDomainService 测试")
class SoftwareBaselineDeletionDomainServiceTest {

    @Mock
    private SoftwareBaselineRepository softwareBaselineRepository;

    @Mock
    private OtaBaselineLookupGateway otaBaselineLookupGateway;

    private SoftwareBaselineDeletionDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new SoftwareBaselineDeletionDomainService(
                softwareBaselineRepository, otaBaselineLookupGateway);
    }

    @Nested
    @DisplayName("DRAFT 状态直删分支")
    class DraftDirectDeleteTests {

        @Test
        @DisplayName("DRAFT 状态直接物理删除，不调用 OTA 反查 Gateway")
        void checkAndDelete_draft_directDelete() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.DRAFT);

            domainService.checkAndDelete(baseline, "admin", false);

            verify(otaBaselineLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(softwareBaselineRepository).delete(eq(baseline), eq("admin"), eq(false));
        }
    }

    @Nested
    @DisplayName("force 旁路分支")
    class ForceDeleteTests {

        @Test
        @DisplayName("RELEASED 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_released_forceDelete() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.RELEASED);

            domainService.checkAndDelete(baseline, "admin", true);

            verify(otaBaselineLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(softwareBaselineRepository).delete(eq(baseline), eq("admin"), eq(true));
        }

        @Test
        @DisplayName("SUPERSEDED 状态 + forceDelete=true 跳过反查直接删除")
        void checkAndDelete_superseded_forceDelete() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.SUPERSEDED);

            domainService.checkAndDelete(baseline, "admin", true);

            verify(otaBaselineLookupGateway, never()).checkReferences(anyString(), anyInt());
            verify(softwareBaselineRepository).delete(eq(baseline), eq("admin"), eq(true));
        }
    }

    @Nested
    @DisplayName("RELEASED/SUPERSEDED 反查分支")
    class LookupTests {

        @Test
        @DisplayName("反查无引用 - 删除成功")
        void checkAndDelete_noReference_deleteSuccess() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.RELEASED);
            OtaBaselineReferenceCheckResult result = OtaBaselineReferenceCheckResult.builder()
                    .referenceCount(0)
                    .samples(Collections.emptyList())
                    .available(true)
                    .build();
            when(otaBaselineLookupGateway.checkReferences("SWB-V1", 10)).thenReturn(result);

            domainService.checkAndDelete(baseline, "admin", false);

            verify(softwareBaselineRepository).delete(eq(baseline), eq("admin"), eq(false));
        }

        @Test
        @DisplayName("反查有引用 - 抛出 SoftwareBaselineHasDownstreamRefException")
        void checkAndDelete_hasReference_throwsException() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.RELEASED);
            OtaBaselineReferenceCheckResult result = OtaBaselineReferenceCheckResult.builder()
                    .referenceCount(3)
                    .referencingService("ota:BaselineProjection")
                    .samples(java.util.Arrays.asList("TB_001", "TB_002", "TB_003"))
                    .available(true)
                    .build();
            when(otaBaselineLookupGateway.checkReferences("SWB-V1", 10)).thenReturn(result);

            SoftwareBaselineHasDownstreamRefException exception = assertThrows(
                    SoftwareBaselineHasDownstreamRefException.class,
                    () -> domainService.checkAndDelete(baseline, "admin", false)
            );

            assertEquals("SWB-V1", exception.getBaselineCode());
            assertEquals(3, exception.getReferenceCount());
            verify(softwareBaselineRepository, never()).delete(any(), anyString(), anyBoolean());
        }

        @Test
        @DisplayName("反查不可用 - 抛出 VmdServiceUnavailableException")
        void checkAndDelete_unavailable_throwsException() {
            SoftwareBaseline baseline = createTestBaseline(BaselineStatus.RELEASED);
            OtaBaselineReferenceCheckResult result = OtaBaselineReferenceCheckResult.unavailable("ota:BaselineProjection");
            when(otaBaselineLookupGateway.checkReferences("SWB-V1", 10)).thenReturn(result);

            assertThrows(VmdServiceUnavailableException.class,
                    () -> domainService.checkAndDelete(baseline, "admin", false));

            verify(softwareBaselineRepository, never()).delete(any(), anyString(), anyBoolean());
        }
    }

    private SoftwareBaseline createTestBaseline(BaselineStatus status) {
        SoftwareBaseline baseline = SoftwareBaseline.create(
                "SWB-V1", "测试基线", AnchorType.CONFIGURATION,
                "CONFIG001", "V1", "测试",
                null, null, "admin"
        );
        if (status == BaselineStatus.RELEASED) {
            baseline.release("admin");
        } else if (status == BaselineStatus.SUPERSEDED) {
            baseline.release("admin");
            baseline.supersede("SWB-V2", "admin");
        }
        return baseline;
    }
}
