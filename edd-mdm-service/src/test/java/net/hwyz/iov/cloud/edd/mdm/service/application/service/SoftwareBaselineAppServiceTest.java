package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineItemBindCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineFrozenException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineItemNotSoftwareException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineDeletionDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineRepublishDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 软件基线应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SoftwareBaselineAppService 测试")
class SoftwareBaselineAppServiceTest {

    @Mock
    private SoftwareBaselineRepository softwareBaselineRepository;
    @Mock
    private SoftwareBaselineDomainService softwareBaselineDomainService;
    @Mock
    private SoftwareBaselineDeletionDomainService softwareBaselineDeletionDomainService;
    @Mock
    private SoftwareBaselineRepublishDomainService softwareBaselineRepublishDomainService;
    @Mock
    private OutboxService outboxService;

    private SoftwareBaselineAppService appService;

    @BeforeEach
    void setUp() {
        appService = new SoftwareBaselineAppService(
                softwareBaselineRepository,
                softwareBaselineDomainService,
                softwareBaselineDeletionDomainService,
                softwareBaselineRepublishDomainService,
                outboxService
        );
    }

    @Nested
    @DisplayName("createSoftwareBaseline 测试")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 验证 repository.save 和 outbox.publish 被调用")
        void create_success() {
            SoftwareBaselineCreateCmd cmd = SoftwareBaselineCreateCmd.builder()
                    .name("基线V1")
                    .anchorType("CONFIGURATION")
                    .anchorCode("CONFIG001")
                    .baselineVersion("V1")
                    .description("首版基线")
                    .createBy("admin")
                    .build();

            SoftwareBaseline saved = createTestBaseline();
            when(softwareBaselineDomainService.generateCode(AnchorType.CONFIGURATION, "CONFIG001", "V1"))
                    .thenReturn("CONFIG001-V1");
            when(softwareBaselineRepository.save(any(SoftwareBaseline.class), eq("CREATE")))
                    .thenReturn(saved);

            SoftwareBaselineDto result = appService.createSoftwareBaseline(cmd);

            assertNotNull(result);
            assertEquals("SWB-V1", result.getCode());
            verify(softwareBaselineDomainService).validateAnchor(AnchorType.CONFIGURATION, "CONFIG001");
            verify(softwareBaselineDomainService).validateUniqueness("CONFIG001-V1", AnchorType.CONFIGURATION, "CONFIG001", "V1");
            verify(softwareBaselineRepository).save(any(SoftwareBaseline.class), eq("CREATE"));
            verify(outboxService).publishSoftwareBaselineCreatedEvent(any(SoftwareBaseline.class));
        }
    }

    @Nested
    @DisplayName("bindItem 绑定基线项测试")
    class BindItemTests {

        @Test
        @DisplayName("绑定成功 - 校验零件为软件件并冗余字段")
        void bindItem_success() {
            SoftwareBaseline baseline = createTestBaseline();
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));

            Part part = Part.builder()
                    .code("PART001")
                    .name("测试零件")
                    .drawingNo("DWG001")
                    .drawingVersion("1.0")
                    .isSoftware(true)
                    .vehicleNodeCode("TBOX")
                    .fotaUpgradeable(true)
                    .status(PartStatus.ACTIVE)
                    .build();
            when(softwareBaselineDomainService.validateBaselineItemPart("PART001")).thenReturn(part);
            when(softwareBaselineRepository.save(any(SoftwareBaseline.class), eq("UPDATE")))
                    .thenReturn(baseline);

            SoftwareBaselineItemBindCmd cmd = SoftwareBaselineItemBindCmd.builder()
                    .baselineCode("SWB-V1")
                    .partCode("PART001")
                    .operator("admin")
                    .build();

            SoftwareBaselineDto result = appService.bindItem(cmd);

            assertNotNull(result);
            verify(softwareBaselineDomainService).validateBaselineItemPart("PART001");
            verify(softwareBaselineRepository).saveItem(any());
            verify(softwareBaselineRepository).save(any(SoftwareBaseline.class), eq("UPDATE"));
            verify(outboxService).publishSoftwareBaselineUpdatedEvent(any(SoftwareBaseline.class));
        }

        @Test
        @DisplayName("绑定失败 - 零件非软件件抛 SoftwareBaselineItemNotSoftwareException")
        void bindItem_notSoftware_throwsException() {
            SoftwareBaseline baseline = createTestBaseline();
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));
            when(softwareBaselineDomainService.validateBaselineItemPart("PART001"))
                    .thenThrow(new SoftwareBaselineItemNotSoftwareException("PART001"));

            SoftwareBaselineItemBindCmd cmd = SoftwareBaselineItemBindCmd.builder()
                    .baselineCode("SWB-V1")
                    .partCode("PART001")
                    .operator("admin")
                    .build();

            assertThrows(SoftwareBaselineItemNotSoftwareException.class, () ->
                    appService.bindItem(cmd));

            verify(softwareBaselineRepository, never()).saveItem(any());
        }
    }

    @Nested
    @DisplayName("releaseSoftwareBaseline 发布测试")
    class ReleaseTests {

        @Test
        @DisplayName("发布成功 - DRAFT -> RELEASED")
        void release_success() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.bindItem(net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem.create(
                    baseline.getCode(), "PART001", "TBOX", null, "admin"));
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));
            when(softwareBaselineRepository.save(any(SoftwareBaseline.class), eq("RELEASE")))
                    .thenReturn(baseline);

            SoftwareBaselineDto result = appService.releaseSoftwareBaseline("SWB-V1", "admin");

            assertNotNull(result);
            verify(softwareBaselineRepository).save(any(SoftwareBaseline.class), eq("RELEASE"));
            verify(outboxService).publishSoftwareBaselineReleasedEvent(any(SoftwareBaseline.class));
        }

        @Test
        @DisplayName("发布失败 - 基线不存在抛 SoftwareBaselineNotExistException")
        void release_notExist_throwsException() {
            when(softwareBaselineRepository.findByCode("NOT_EXIST")).thenReturn(Optional.empty());

            assertThrows(SoftwareBaselineNotExistException.class, () ->
                    appService.releaseSoftwareBaseline("NOT_EXIST", "admin"));
        }
    }

    @Nested
    @DisplayName("deleteSoftwareBaseline 删除测试")
    class DeleteTests {

        @Test
        @DisplayName("DRAFT 删除成功 - 调用 deletionDomainService 并发布 Deleted 事件")
        void delete_draft_success() {
            SoftwareBaseline baseline = createTestBaseline();
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));
            doNothing().when(softwareBaselineDeletionDomainService).checkAndDelete(baseline, "admin", false);

            appService.deleteSoftwareBaseline("SWB-V1", "admin", false);

            verify(softwareBaselineDeletionDomainService).checkAndDelete(baseline, "admin", false);
            verify(outboxService).publishSoftwareBaselineDeletedEvent(baseline, false);
        }

        @Test
        @DisplayName("force 删除成功 - 跳过反查")
        void delete_force_success() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));
            doNothing().when(softwareBaselineDeletionDomainService).checkAndDelete(baseline, "admin", true);

            appService.deleteSoftwareBaseline("SWB-V1", "admin", true);

            verify(softwareBaselineDeletionDomainService).checkAndDelete(baseline, "admin", true);
            verify(outboxService).publishSoftwareBaselineDeletedEvent(baseline, true);
        }
    }

    @Nested
    @DisplayName("getSoftwareBaselineByCode 查询测试")
    class QueryTests {

        @Test
        @DisplayName("查询成功 - 返回含基线项清单的 DTO")
        void getByCode_success() {
            SoftwareBaseline baseline = createTestBaseline();
            when(softwareBaselineRepository.findByCode("SWB-V1")).thenReturn(Optional.of(baseline));

            SoftwareBaselineDto result = appService.getSoftwareBaselineByCode("SWB-V1");

            assertNotNull(result);
            assertEquals("SWB-V1", result.getCode());
            assertEquals("CONFIGURATION", result.getAnchorType());
            assertEquals("DRAFT", result.getBaselineStatus());
        }

        @Test
        @DisplayName("查询失败 - 基线不存在抛 SoftwareBaselineNotExistException")
        void getByCode_notExist_throwsException() {
            when(softwareBaselineRepository.findByCode("NOT_EXIST")).thenReturn(Optional.empty());

            assertThrows(SoftwareBaselineNotExistException.class, () ->
                    appService.getSoftwareBaselineByCode("NOT_EXIST"));
        }
    }

    private SoftwareBaseline createTestBaseline() {
        return SoftwareBaseline.create(
                "SWB-V1", "测试基线", AnchorType.CONFIGURATION,
                "CONFIG001", "V1", "测试",
                null, null, "admin"
        );
    }
}
