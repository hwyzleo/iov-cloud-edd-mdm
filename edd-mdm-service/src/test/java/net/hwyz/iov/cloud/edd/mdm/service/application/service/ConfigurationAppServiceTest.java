package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmBaseException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmErrorCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.ConfigurationStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 配置应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationAppService 测试")
class ConfigurationAppServiceTest {

    @Mock
    private ProductDomainService productDomainService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;
    @Mock
    private OptionCodeRepository optionCodeRepository;
    @Mock
    private SoftwareBaselineRepository softwareBaselineRepository;

    private ConfigurationAppService configurationAppService;

    @BeforeEach
    void setUp() {
        configurationAppService = new ConfigurationAppService(
                productDomainService,
                outboxService,
                configurationOptionCodeBindingRepository,
                optionCodeRepository,
                softwareBaselineRepository
        );
    }

    @Nested
    @DisplayName("resolveConfigurationCode 测试")
    class ResolveConfigurationCodeTests {

        @Test
        @DisplayName("正常反查 - 返回配置code")
        void resolveConfigurationCode_normal_returnsCode() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            when(productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes))
                    .thenReturn("VAR0010000001");

            // When
            String result = configurationAppService.resolveConfigurationCode(variantCode, optionCodes);

            // Then
            assertEquals("VAR0010000001", result);
            verify(productDomainService).findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes);
        }

        @Test
        @DisplayName("无匹配结果 - 返回null")
        void resolveConfigurationCode_noMatch_returnsNull() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            when(productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes))
                    .thenReturn(null);

            // When
            String result = configurationAppService.resolveConfigurationCode(variantCode, optionCodes);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("调用领域服务 - 参数正确传递")
        void resolveConfigurationCode_callsDomainServiceWithCorrectParams() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            when(productDomainService.findConfigurationCodeByVariantAndOptionCodes(anyString(), anyList()))
                    .thenReturn("VAR0010000001");

            // When
            configurationAppService.resolveConfigurationCode(variantCode, optionCodes);

            // Then
            verify(productDomainService).findConfigurationCodeByVariantAndOptionCodes(eq(variantCode), eq(optionCodes));
        }
    }

    @Nested
    @DisplayName("findByOptionCodes 测试")
    class FindByOptionCodesTests {

        @Test
        @DisplayName("正常查询 - 返回配置列表")
        void findByOptionCodes_normal_returnsList() {
            // Given
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            Configuration config1 = createTestConfiguration("VAR0010000001", "VAR001");
            Configuration config2 = createTestConfiguration("VAR0010000002", "VAR001");
            when(productDomainService.findConfigurationsByOptionCodes(optionCodes))
                    .thenReturn(Arrays.asList(config1, config2));

            // When
            List<ConfigurationDto> result = configurationAppService.findByOptionCodes(optionCodes);

            // Then
            assertEquals(2, result.size());
            assertEquals("VAR0010000001", result.get(0).getCode());
            assertEquals("VAR0010000002", result.get(1).getCode());
        }

        @Test
        @DisplayName("无匹配结果 - 返回空列表")
        void findByOptionCodes_noMatch_returnsEmptyList() {
            // Given
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            when(productDomainService.findConfigurationsByOptionCodes(optionCodes))
                    .thenReturn(Collections.emptyList());

            // When
            List<ConfigurationDto> result = configurationAppService.findByOptionCodes(optionCodes);

            // Then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("createConfiguration/updateConfiguration 名称长度校验测试（CR-033）")
    class NameLengthValidationTests {

        @Test
        @DisplayName("创建 - name 512 字符成功写入")
        void createConfiguration_name512_success() {
            // Given
            String name = "n".repeat(Configuration.NAME_MAX_LENGTH);
            ConfigurationCreateCmd cmd = ConfigurationCreateCmd.builder()
                    .name(name).nameLocal("本地名称").variantCode("VAR001")
                    .description("desc").createBy("admin").build();
            Configuration config = createTestConfiguration("VAR0010000001", "VAR001");
            when(productDomainService.createConfiguration(anyString(), anyString(), anyString(), anyString(),
                    any(), any(), anyString())).thenReturn(config);

            // When
            ConfigurationDto result = configurationAppService.createConfiguration(cmd);

            // Then
            assertNotNull(result);
            verify(productDomainService).createConfiguration(eq(name), anyString(), eq("VAR001"),
                    anyString(), any(), any(), eq("admin"));
            verify(outboxService).publishConfigurationCreatedEvent(config);
        }

        @Test
        @DisplayName("创建 - name 513 字符被拒，不进入持久化与事件")
        void createConfiguration_name513_rejected() {
            // Given
            String name = "n".repeat(Configuration.NAME_MAX_LENGTH + 1);
            ConfigurationCreateCmd cmd = ConfigurationCreateCmd.builder()
                    .name(name).nameLocal("本地名称").variantCode("VAR001")
                    .description("desc").createBy("admin").build();

            // When
            MdmBaseException ex = assertThrows(MdmBaseException.class,
                    () -> configurationAppService.createConfiguration(cmd));

            // Then
            assertEquals(MdmErrorCode.CONFIGURATION_NAME_TOO_LONG.getCode(), ex.getErrorCode().getCode());
            verifyNoInteractions(productDomainService);
            verifyNoInteractions(outboxService);
        }

        @Test
        @DisplayName("创建 - nameLocal 513 字符被拒，不进入持久化与事件")
        void createConfiguration_nameLocal513_rejected() {
            // Given
            String nameLocal = "n".repeat(Configuration.NAME_MAX_LENGTH + 1);
            ConfigurationCreateCmd cmd = ConfigurationCreateCmd.builder()
                    .name("正常名称").nameLocal(nameLocal).variantCode("VAR001")
                    .description("desc").createBy("admin").build();

            // When
            MdmBaseException ex = assertThrows(MdmBaseException.class,
                    () -> configurationAppService.createConfiguration(cmd));

            // Then
            assertEquals(MdmErrorCode.CONFIGURATION_NAME_TOO_LONG.getCode(), ex.getErrorCode().getCode());
            verifyNoInteractions(productDomainService);
            verifyNoInteractions(outboxService);
        }

        @Test
        @DisplayName("更新 - name 513 字符被拒，不进入持久化与事件")
        void updateConfiguration_name513_rejected() {
            // Given
            String name = "n".repeat(Configuration.NAME_MAX_LENGTH + 1);
            ConfigurationUpdateCmd cmd = ConfigurationUpdateCmd.builder()
                    .name(name).nameLocal("本地名称").description("desc").modifyBy("admin").build();

            // When
            MdmBaseException ex = assertThrows(MdmBaseException.class,
                    () -> configurationAppService.updateConfiguration("VAR0010000001", cmd));

            // Then
            assertEquals(MdmErrorCode.CONFIGURATION_NAME_TOO_LONG.getCode(), ex.getErrorCode().getCode());
            verifyNoInteractions(productDomainService);
            verifyNoInteractions(outboxService);
        }

        @Test
        @DisplayName("更新 - name 512 字符成功写入")
        void updateConfiguration_name512_success() {
            // Given
            String name = "n".repeat(Configuration.NAME_MAX_LENGTH);
            ConfigurationUpdateCmd cmd = ConfigurationUpdateCmd.builder()
                    .name(name).nameLocal("本地名称").description("desc").modifyBy("admin").build();
            Configuration config = createTestConfiguration("VAR0010000001", "VAR001");
            when(productDomainService.updateConfiguration(anyString(), anyString(), anyString(), anyString(),
                    any(), any(), anyString())).thenReturn(config);

            // When
            ConfigurationDto result = configurationAppService.updateConfiguration("VAR0010000001", cmd);

            // Then
            assertNotNull(result);
            verify(productDomainService).updateConfiguration(eq("VAR0010000001"), eq(name), anyString(),
                    anyString(), any(), any(), eq("admin"));
            verify(outboxService).publishConfigurationUpdatedEvent(config);
        }
    }

    private Configuration createTestConfiguration(String code, String variantCode) {
        Configuration config = Configuration.create(
                code, "测试配置", "Test Config", variantCode,
                "测试配置描述", null, null, "admin"
        );
        config.setStatus(ConfigurationStatus.ACTIVE);
        return config;
    }
}
