package net.hwyz.iov.cloud.edd.mdm.service.application.service;

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

            SwinScheme result = swinSchemeAppService.createSwinScheme("TEST_SCHEME", "Test Scheme", "测试方案",
                    "Test Description", "SINGLE_SWIN", 1, new Date(), new Date(System.currentTimeMillis() + 86400000L), "testUser");

            assertNotNull(result);
            assertEquals("TEST_SCHEME", result.getCode());
            assertEquals(SwinRoute.SINGLE_SWIN, result.getRoute());
            verify(swinSchemeRepository).existsByCode("TEST_SCHEME");
            verify(swinSchemeRepository).save(any(SwinScheme.class));
        }

        @Test
        @DisplayName("创建失败 - code 重复抛 SwinSchemeDuplicateCodeException")
        void create_duplicateCode_throwsException() {
            when(swinSchemeRepository.existsByCode("TEST_SCHEME")).thenReturn(true);

            assertThrows(SwinSchemeDuplicateCodeException.class, () -> {
                swinSchemeAppService.createSwinScheme("TEST_SCHEME", "Test Scheme", null, null,
                        "SINGLE_SWIN", 0, null, null, "testUser");
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

            SwinScheme result = swinSchemeAppService.updateSwinScheme("TEST_SCHEME", "Updated Name", "更新名称",
                    "Updated Description", "MULTI_SWIN", 2, new Date(), new Date(System.currentTimeMillis() + 86400000L), "modifier");

            assertNotNull(result);
            assertEquals("Updated Name", result.getName());
            assertEquals(SwinRoute.MULTI_SWIN, result.getRoute());
            verify(swinSchemeRepository).findByCode("TEST_SCHEME");
            verify(swinSchemeRepository).save(any(SwinScheme.class));
        }

        @Test
        @DisplayName("更新失败 - 不存在抛 SwinSchemeNotExistException")
        void update_notExist_throwsException() {
            when(swinSchemeRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

            assertThrows(SwinSchemeNotExistException.class, () -> {
                swinSchemeAppService.updateSwinScheme("NONEXISTENT", "Name", null, null,
                        "SINGLE_SWIN", 0, null, null, "modifier");
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

            SwinScheme result = swinSchemeAppService.deactivateSwinScheme("TEST_SCHEME", "admin");

            assertNotNull(result);
            assertEquals(SwinSchemeStatus.INACTIVE, result.getStatus());
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

            SwinScheme result = swinSchemeAppService.getSwinSchemeByCode("TEST_SCHEME");

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
