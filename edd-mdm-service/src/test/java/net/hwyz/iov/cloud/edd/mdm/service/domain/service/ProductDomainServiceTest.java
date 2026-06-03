package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VariantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.*;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 产品领域服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductDomainService 测试")
class ProductDomainServiceTest {

    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CarLineRepository carLineRepository;
    @Mock
    private PlatformRepository platformRepository;
    @Mock
    private OptionFamilyRepository optionFamilyRepository;
    @Mock
    private OptionCodeRepository optionCodeRepository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private VariantRepository variantRepository;
    @Mock
    private VariantOptionCodeBindingRepository variantOptionCodeBindingRepository;
    @Mock
    private ConfigurationRepository configurationRepository;
    @Mock
    private ConfigurationSeqRepository configurationSeqRepository;
    @Mock
    private ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;

    private ProductDomainService productDomainService;

    @BeforeEach
    void setUp() {
        productDomainService = new ProductDomainService(
                brandRepository,
                carLineRepository,
                platformRepository,
                optionFamilyRepository,
                optionCodeRepository,
                modelRepository,
                variantRepository,
                variantOptionCodeBindingRepository,
                configurationRepository,
                configurationSeqRepository,
                configurationOptionCodeBindingRepository
        );
    }

    @Nested
    @DisplayName("findConfigurationCodeByVariantAndOptionCodes 测试")
    class FindConfigurationCodeByVariantAndOptionCodesTests {

        @Test
        @DisplayName("正常反查 - 返回匹配的配置code")
        void findConfigurationCode_normal_returnsCode() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            Variant variant = createTestVariant(variantCode, VariantStatus.ACTIVE);
            when(variantRepository.findByCode(variantCode)).thenReturn(Optional.of(variant));
            when(configurationOptionCodeBindingRepository.findConfigurationCodeByVariantAndOptionCodes(
                    eq(variantCode), eq(optionCodes), eq(optionCodes.size())))
                    .thenReturn(Collections.singletonList("VAR0010000001"));

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes);

            // Then
            assertEquals("VAR0010000001", result);
            verify(variantRepository).findByCode(variantCode);
            verify(configurationOptionCodeBindingRepository).findConfigurationCodeByVariantAndOptionCodes(
                    eq(variantCode), eq(optionCodes), eq(optionCodes.size()));
        }

        @Test
        @DisplayName("无匹配结果 - 返回null")
        void findConfigurationCode_noMatch_returnsNull() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            Variant variant = createTestVariant(variantCode, VariantStatus.ACTIVE);
            when(variantRepository.findByCode(variantCode)).thenReturn(Optional.of(variant));
            when(configurationOptionCodeBindingRepository.findConfigurationCodeByVariantAndOptionCodes(
                    eq(variantCode), eq(optionCodes), eq(optionCodes.size())))
                    .thenReturn(Collections.emptyList());

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("版本不存在 - 抛出异常")
        void findConfigurationCode_variantNotExist_throwsException() {
            // Given
            String variantCode = "VAR_NOT_EXIST";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            when(variantRepository.findByCode(variantCode)).thenReturn(Optional.empty());

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes));
        }

        @Test
        @DisplayName("版本状态非ACTIVE - 抛出异常")
        void findConfigurationCode_variantNotActive_throwsException() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            Variant variant = createTestVariant(variantCode, VariantStatus.INACTIVE);
            when(variantRepository.findByCode(variantCode)).thenReturn(Optional.of(variant));

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes));
        }

        @Test
        @DisplayName("variantCode为空 - 返回null")
        void findConfigurationCode_nullVariantCode_returnsNull() {
            // Given
            List<String> optionCodes = Arrays.asList("OC001", "OC002");

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(null, optionCodes);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("optionCodes为空 - 返回null")
        void findConfigurationCode_emptyOptionCodes_returnsNull() {
            // Given
            String variantCode = "VAR001";

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, Collections.emptyList());

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("optionCodes为null - 返回null")
        void findConfigurationCode_nullOptionCodes_returnsNull() {
            // Given
            String variantCode = "VAR001";

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, null);

            // Then
            assertNull(result);
        }

        @Test
        @DisplayName("多个匹配结果 - 返回第一个并记录警告")
        void findConfigurationCode_multipleMatches_returnsFirst() {
            // Given
            String variantCode = "VAR001";
            List<String> optionCodes = Arrays.asList("OC001", "OC002");
            Variant variant = createTestVariant(variantCode, VariantStatus.ACTIVE);
            when(variantRepository.findByCode(variantCode)).thenReturn(Optional.of(variant));
            when(configurationOptionCodeBindingRepository.findConfigurationCodeByVariantAndOptionCodes(
                    eq(variantCode), eq(optionCodes), eq(optionCodes.size())))
                    .thenReturn(Arrays.asList("VAR0010000001", "VAR0010000002"));

            // When
            String result = productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes);

            // Then
            assertEquals("VAR0010000001", result);
        }
    }

    private Variant createTestVariant(String code, VariantStatus status) {
        Variant variant = Variant.create(
                code, "测试版本", "Test Variant", "MODEL001",
                "测试版本描述", null, null, "admin"
        );
        variant.setStatus(status);
        return variant;
    }
}
