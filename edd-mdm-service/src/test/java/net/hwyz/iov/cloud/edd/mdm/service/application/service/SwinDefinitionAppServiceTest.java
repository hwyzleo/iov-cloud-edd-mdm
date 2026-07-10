package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinDefinitionQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinDefinitionDto;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionDuplicateSwinCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSchemeNotActiveException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSingleSwinConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SwinDefinitionDeletionDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SWIN定义应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SwinDefinitionAppService 测试")
class SwinDefinitionAppServiceTest {

    @Mock
    private SwinDefinitionRepository swinDefinitionRepository;
    @Mock
    private SwinSchemeRepository swinSchemeRepository;
    @Mock
    private SwinDefinitionDeletionDomainService swinDefinitionDeletionDomainService;

    private SwinDefinitionAppService swinDefinitionAppService;

    @BeforeEach
    void setUp() {
        swinDefinitionAppService = new SwinDefinitionAppService(swinDefinitionRepository, swinSchemeRepository, swinDefinitionDeletionDomainService);
    }

    @Nested
    @DisplayName("createSwinDefinition 测试")
    class CreateSwinDefinitionTests {

        @Test
        @DisplayName("创建成功 - SINGLE_SWIN 路由，无冲突")
        void create_success_singleSwin() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN001")).thenReturn(false);
            when(swinSchemeRepository.findByCode("SCHEME001")).thenReturn(Optional.of(createActiveScheme(SwinRoute.SINGLE_SWIN)));
            when(swinDefinitionRepository.countActiveByTypeRef("VARIANT", "VAR001")).thenReturn(0L);
            doNothing().when(swinDefinitionRepository).save(any(SwinDefinition.class));

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN001").schemeCode("SCHEME001").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test Definition").nameLocal("测试定义").description("Test Description").createBy("testUser").build();

            SwinDefinitionDto result = swinDefinitionAppService.createSwinDefinition(cmd);

            assertNotNull(result);
            assertEquals("SWIN001", result.getSwinCode());
            assertEquals("SCHEME001", result.getSchemeCode());
            verify(swinDefinitionRepository).existsBySwinCode("SWIN001");
            verify(swinSchemeRepository).findByCode("SCHEME001");
            verify(swinDefinitionRepository).countActiveByTypeRef("VARIANT", "VAR001");
            verify(swinDefinitionRepository).save(any(SwinDefinition.class));
        }

        @Test
        @DisplayName("创建成功 - MULTI_SWIN 路由，可多个")
        void create_success_multiSwin() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN002")).thenReturn(false);
            when(swinSchemeRepository.findByCode("SCHEME001")).thenReturn(Optional.of(createActiveScheme(SwinRoute.MULTI_SWIN)));
            doNothing().when(swinDefinitionRepository).save(any(SwinDefinition.class));

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN002").schemeCode("SCHEME001").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test Definition 2").createBy("testUser").build();

            SwinDefinitionDto result = swinDefinitionAppService.createSwinDefinition(cmd);

            assertNotNull(result);
            assertEquals("SWIN002", result.getSwinCode());
            verify(swinDefinitionRepository, never()).countActiveByTypeRef(anyString(), anyString());
        }

        @Test
        @DisplayName("创建失败 - swinCode 重复抛 SwinDefinitionDuplicateSwinCodeException")
        void create_duplicateSwinCode_throwsException() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN001")).thenReturn(true);

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN001").schemeCode("SCHEME001").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test").createBy("testUser").build();

            assertThrows(SwinDefinitionDuplicateSwinCodeException.class, () -> {
                swinDefinitionAppService.createSwinDefinition(cmd);
            });

            verify(swinDefinitionRepository).existsBySwinCode("SWIN001");
            verify(swinSchemeRepository, never()).findByCode(anyString());
            verify(swinDefinitionRepository, never()).save(any());
        }

        @Test
        @DisplayName("创建失败 - 编码方案不存在抛 SwinSchemeNotExistException")
        void create_schemeNotExist_throwsException() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN001")).thenReturn(false);
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN001").schemeCode("NONEXISTENT").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test").createBy("testUser").build();

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinDefinitionAppService.createSwinDefinition(cmd);
            });

            verify(swinSchemeRepository).findByCode("NONEXISTENT");
            verify(swinDefinitionRepository, never()).save(any());
        }

        @Test
        @DisplayName("创建失败 - 编码方案非 ACTIVE 抛 SwinDefinitionSchemeNotActiveException")
        void create_schemeNotActive_throwsException() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN001")).thenReturn(false);
            SwinScheme inactiveScheme = createActiveScheme(SwinRoute.SINGLE_SWIN);
            inactiveScheme.setStatus(SwinSchemeStatus.INACTIVE);
            when(swinSchemeRepository.findByCode("SCHEME001")).thenReturn(Optional.of(inactiveScheme));

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN001").schemeCode("SCHEME001").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test").createBy("testUser").build();

            assertThrows(SwinDefinitionSchemeNotActiveException.class, () -> {
                swinDefinitionAppService.createSwinDefinition(cmd);
            });

            verify(swinDefinitionRepository, never()).save(any());
        }

        @Test
        @DisplayName("创建失败 - SINGLE_SWIN 路由冲突抛 SwinDefinitionSingleSwinConflictException")
        void create_singleSwinConflict_throwsException() {
            when(swinDefinitionRepository.existsBySwinCode("SWIN002")).thenReturn(false);
            when(swinSchemeRepository.findByCode("SCHEME001")).thenReturn(Optional.of(createActiveScheme(SwinRoute.SINGLE_SWIN)));
            when(swinDefinitionRepository.countActiveByTypeRef("VARIANT", "VAR001")).thenReturn(1L);

            SwinDefinitionCreateCmd cmd = SwinDefinitionCreateCmd.builder()
                    .swinCode("SWIN002").schemeCode("SCHEME001").typeRefType("VARIANT").typeRefCode("VAR001")
                    .name("Test").createBy("testUser").build();

            assertThrows(SwinDefinitionSingleSwinConflictException.class, () -> {
                swinDefinitionAppService.createSwinDefinition(cmd);
            });

            verify(swinDefinitionRepository).countActiveByTypeRef("VARIANT", "VAR001");
            verify(swinDefinitionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateSwinDefinition 测试")
    class UpdateSwinDefinitionTests {

        @Test
        @DisplayName("更新成功")
        void update_success() {
            SwinDefinition existingDefinition = createTestDefinition();
            when(swinDefinitionRepository.findBySwinCode("SWIN001")).thenReturn(Optional.of(existingDefinition));
            doNothing().when(swinDefinitionRepository).save(any(SwinDefinition.class));

            SwinDefinitionUpdateCmd cmd = SwinDefinitionUpdateCmd.builder()
                    .swinCode("SWIN001").name("Updated Name").nameLocal("更新名称")
                    .description("Updated Description").modifyBy("modifier").build();

            SwinDefinitionDto result = swinDefinitionAppService.updateSwinDefinition(cmd);

            assertNotNull(result);
            assertEquals("Updated Name", result.getName());
            assertEquals("更新名称", result.getNameLocal());
            verify(swinDefinitionRepository).findBySwinCode("SWIN001");
            verify(swinDefinitionRepository).save(any(SwinDefinition.class));
        }

        @Test
        @DisplayName("更新失败 - 不存在抛 SwinDefinitionNotExistException")
        void update_notExist_throwsException() {
            when(swinDefinitionRepository.findBySwinCode("NONEXISTENT")).thenReturn(Optional.empty());

            SwinDefinitionUpdateCmd cmd = SwinDefinitionUpdateCmd.builder()
                    .swinCode("NONEXISTENT").name("Name").modifyBy("modifier").build();

            assertThrows(SwinDefinitionNotExistException.class, () -> {
                swinDefinitionAppService.updateSwinDefinition(cmd);
            });

            verify(swinDefinitionRepository).findBySwinCode("NONEXISTENT");
            verify(swinDefinitionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteSwinDefinition 测试")
    class DeleteSwinDefinitionTests {

        @Test
        @DisplayName("删除成功")
        void delete_success() {
            SwinDefinition existingDefinition = createTestDefinition();
            when(swinDefinitionRepository.findBySwinCode("SWIN001")).thenReturn(Optional.of(existingDefinition));
            doNothing().when(swinDefinitionRepository).deleteBySwinCode("SWIN001");

            swinDefinitionAppService.deleteSwinDefinition("SWIN001", "admin");

            verify(swinDefinitionRepository).findBySwinCode("SWIN001");
            verify(swinDefinitionRepository).deleteBySwinCode("SWIN001");
        }

        @Test
        @DisplayName("删除失败 - 不存在抛 SwinDefinitionNotExistException")
        void delete_notExist_throwsException() {
            when(swinDefinitionRepository.findBySwinCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinDefinitionNotExistException.class, () -> {
                swinDefinitionAppService.deleteSwinDefinition("NONEXISTENT", "admin");
            });

            verify(swinDefinitionRepository).findBySwinCode("NONEXISTENT");
            verify(swinDefinitionRepository, never()).deleteBySwinCode(anyString());
        }
    }

    @Nested
    @DisplayName("deactivateSwinDefinition 测试")
    class DeactivateSwinDefinitionTests {

        @Test
        @DisplayName("失效成功")
        void deactivate_success() {
            SwinDefinition existingDefinition = createTestDefinition();
            when(swinDefinitionRepository.findBySwinCode("SWIN001")).thenReturn(Optional.of(existingDefinition));
            doNothing().when(swinDefinitionRepository).save(any(SwinDefinition.class));

            SwinDefinitionDto result = swinDefinitionAppService.deactivateSwinDefinition("SWIN001", "admin");

            assertNotNull(result);
            assertEquals("INACTIVE", result.getStatus());
            verify(swinDefinitionRepository).findBySwinCode("SWIN001");
            verify(swinDefinitionRepository).save(any(SwinDefinition.class));
        }

        @Test
        @DisplayName("失效失败 - 不存在抛 SwinDefinitionNotExistException")
        void deactivate_notExist_throwsException() {
            when(swinDefinitionRepository.findBySwinCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinDefinitionNotExistException.class, () -> {
                swinDefinitionAppService.deactivateSwinDefinition("NONEXISTENT", "admin");
            });

            verify(swinDefinitionRepository).findBySwinCode("NONEXISTENT");
            verify(swinDefinitionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getSwinDefinitionBySwinCode 测试")
    class GetSwinDefinitionBySwinCodeTests {

        @Test
        @DisplayName("获取成功")
        void get_success() {
            SwinDefinition existingDefinition = createTestDefinition();
            when(swinDefinitionRepository.findBySwinCode("SWIN001")).thenReturn(Optional.of(existingDefinition));

            SwinDefinitionDto result = swinDefinitionAppService.getSwinDefinitionBySwinCode("SWIN001");

            assertNotNull(result);
            assertEquals("SWIN001", result.getSwinCode());
            verify(swinDefinitionRepository).findBySwinCode("SWIN001");
        }

        @Test
        @DisplayName("获取失败 - 不存在抛 SwinDefinitionNotExistException")
        void get_notExist_throwsException() {
            when(swinDefinitionRepository.findBySwinCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinDefinitionNotExistException.class, () -> {
                swinDefinitionAppService.getSwinDefinitionBySwinCode("NONEXISTENT");
            });

            verify(swinDefinitionRepository).findBySwinCode("NONEXISTENT");
        }
    }

    private SwinScheme createActiveScheme(SwinRoute route) {
        return SwinScheme.create("SCHEME001", "Test Scheme", null, null, route, null, null, 0, null, null, "testUser");
    }

    private SwinDefinition createTestDefinition() {
        return SwinDefinition.create("SWIN001", "SCHEME001", "VARIANT", "VAR001", "Test Definition", null, null, "testUser");
    }
}
