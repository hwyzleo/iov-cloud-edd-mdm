package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinSchemeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinSchemeDto;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SWIN编码方案应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SwinSchemeAppService 测试")
class SwinSchemeAppServiceTest {

    @Mock
    private SwinSchemeRepository swinSchemeRepository;
    @Mock
    private SwinDefinitionRepository swinDefinitionRepository;

    private SwinSchemeAppService swinSchemeAppService;

    @BeforeEach
    void setUp() {
        swinSchemeAppService = new SwinSchemeAppService(swinSchemeRepository, swinDefinitionRepository);
    }

    @Nested
    @DisplayName("createSwinScheme 测试")
    class CreateSwinSchemeTests {

        @Test
        @DisplayName("创建成功 - 验证 repository.save 被调用")
        void create_success() {
            when(swinSchemeRepository.existsByCode("TEST_SCHEME")).thenReturn(false);
            doNothing().when(swinSchemeRepository).save(any(SwinScheme.class));

            SwinSchemeCreateCmd cmd = SwinSchemeCreateCmd.builder()
                    .code("TEST_SCHEME").name("Test Scheme").nameLocal("测试方案")
                    .description("Test Description").route("SINGLE_SWIN").sortOrder(1)
                    .effectiveFrom(new Date()).effectiveTo(new Date(System.currentTimeMillis() + 86400000L))
                    .createBy("testUser").build();

            SwinSchemeDto result = swinSchemeAppService.createSwinScheme(cmd);

            assertNotNull(result);
            assertEquals("TEST_SCHEME", result.getCode());
            assertEquals("SINGLE_SWIN", result.getRoute());
            verify(swinSchemeRepository).existsByCode("TEST_SCHEME");
            verify(swinSchemeRepository).save(any(SwinScheme.class));
        }

        @Test
        @DisplayName("创建失败 - code 重复抛 SwinSchemeDuplicateCodeException")
        void create_duplicateCode_throwsException() {
            when(swinSchemeRepository.existsByCode("TEST_SCHEME")).thenReturn(true);

            SwinSchemeCreateCmd cmd = SwinSchemeCreateCmd.builder()
                    .code("TEST_SCHEME").name("Test Scheme").route("SINGLE_SWIN").createBy("testUser").build();

            assertThrows(SwinSchemeDuplicateCodeException.class, () -> {
                swinSchemeAppService.createSwinScheme(cmd);
            });

            verify(swinSchemeRepository).existsByCode("TEST_SCHEME");
            verify(swinSchemeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateSwinScheme 测试")
    class UpdateSwinSchemeTests {

        @Test
        @DisplayName("更新成功")
        void update_success() {
            SwinScheme existingScheme = createTestScheme();
            when(swinSchemeRepository.findByCode("TEST_SCHEME")).thenReturn(Optional.of(existingScheme));
            doNothing().when(swinSchemeRepository).save(any(SwinScheme.class));

            SwinSchemeUpdateCmd cmd = SwinSchemeUpdateCmd.builder()
                    .code("TEST_SCHEME").name("Updated Name").nameLocal("更新名称")
                    .description("Updated Description").route("MULTI_SWIN").sortOrder(2)
                    .effectiveFrom(new Date()).effectiveTo(new Date(System.currentTimeMillis() + 86400000L))
                    .modifyBy("modifier").build();

            SwinSchemeDto result = swinSchemeAppService.updateSwinScheme(cmd);

            assertNotNull(result);
            assertEquals("Updated Name", result.getName());
            assertEquals("MULTI_SWIN", result.getRoute());
            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
            verify(swinSchemeRepository).save(any(SwinScheme.class));
        }

        @Test
        @DisplayName("更新失败 - 不存在抛 SwinSchemeNotExistException")
        void update_notExist_throwsException() {
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            SwinSchemeUpdateCmd cmd = SwinSchemeUpdateCmd.builder()
                    .code("NONEXISTENT").name("Name").route("SINGLE_SWIN").modifyBy("modifier").build();

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinSchemeAppService.updateSwinScheme(cmd);
            });

            verify(swinSchemeRepository).findByCode("NONEXISTENT");
            verify(swinSchemeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteSwinScheme 测试")
    class DeleteSwinSchemeTests {

        @Test
        @DisplayName("删除成功 - 无引用")
        void delete_success_noReference() {
            SwinScheme existingScheme = createTestScheme();
            when(swinSchemeRepository.findByCode("TEST_SCHEME")).thenReturn(Optional.of(existingScheme));
            when(swinDefinitionRepository.countBySchemeCode("TEST_SCHEME")).thenReturn(0L);
            doNothing().when(swinSchemeRepository).deleteByCode("TEST_SCHEME");

            swinSchemeAppService.deleteSwinScheme("TEST_SCHEME", "admin");

            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
            verify(swinDefinitionRepository).countBySchemeCode("TEST_SCHEME");
            verify(swinSchemeRepository).deleteByCode("TEST_SCHEME");
        }

        @Test
        @DisplayName("删除失败 - 有引用抛 SwinSchemeHasReferenceException")
        void delete_hasReference_throwsException() {
            SwinScheme existingScheme = createTestScheme();
            when(swinSchemeRepository.findByCode("TEST_SCHEME")).thenReturn(Optional.of(existingScheme));
            when(swinDefinitionRepository.countBySchemeCode("TEST_SCHEME")).thenReturn(3L);

            assertThrows(SwinSchemeHasReferenceException.class, () -> {
                swinSchemeAppService.deleteSwinScheme("TEST_SCHEME", "admin");
            });

            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
            verify(swinDefinitionRepository).countBySchemeCode("TEST_SCHEME");
            verify(swinSchemeRepository, never()).deleteByCode(anyString());
        }

        @Test
        @DisplayName("删除失败 - 不存在抛 SwinSchemeNotExistException")
        void delete_notExist_throwsException() {
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinSchemeAppService.deleteSwinScheme("NONEXISTENT", "admin");
            });

            verify(swinSchemeRepository).findByCode("NONEXISTENT");
            verify(swinSchemeRepository, never()).deleteByCode(anyString());
        }
    }

    @Nested
    @DisplayName("deactivateSwinScheme 测试")
    class DeactivateSwinSchemeTests {

        @Test
        @DisplayName("失效成功")
        void deactivate_success() {
            SwinScheme existingScheme = createTestScheme();
            when(swinSchemeRepository.findByCode("TEST_SCHEME")).thenReturn(Optional.of(existingScheme));
            doNothing().when(swinSchemeRepository).save(any(SwinScheme.class));

            SwinSchemeDto result = swinSchemeAppService.deactivateSwinScheme("TEST_SCHEME", "admin");

            assertNotNull(result);
            assertEquals("INACTIVE", result.getStatus());
            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
            verify(swinSchemeRepository).save(any(SwinScheme.class));
        }

        @Test
        @DisplayName("失效失败 - 不存在抛 SwinSchemeNotExistException")
        void deactivate_notExist_throwsException() {
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinSchemeAppService.deactivateSwinScheme("NONEXISTENT", "admin");
            });

            verify(swinSchemeRepository).findByCode("NONEXISTENT");
            verify(swinSchemeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getSwinSchemeByCode 测试")
    class GetSwinSchemeByCodeTests {

        @Test
        @DisplayName("获取成功")
        void get_success() {
            SwinScheme existingScheme = createTestScheme();
            when(swinSchemeRepository.findByCode("TEST_SCHEME")).thenReturn(Optional.of(existingScheme));

            SwinSchemeDto result = swinSchemeAppService.getSwinSchemeByCode("TEST_SCHEME");

            assertNotNull(result);
            assertEquals("TEST_SCHEME", result.getCode());
            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
        }

        @Test
        @DisplayName("获取失败 - 不存在抛 SwinSchemeNotExistException")
        void get_notExist_throwsException() {
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinSchemeAppService.getSwinSchemeByCode("NONEXISTENT");
            });

            verify(swinSchemeRepository).findByCode("NONEXISTENT");
        }
    }

    private SwinScheme createTestScheme() {
        return SwinScheme.create("TEST_SCHEME", "Test Scheme", null, null, SwinRoute.SINGLE_SWIN, 0, null, null, "testUser");
    }
}
