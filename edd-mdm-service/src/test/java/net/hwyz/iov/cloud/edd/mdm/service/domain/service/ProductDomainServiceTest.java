package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VariantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCategoryPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyHasChildrenReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyNameDuplicateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.*;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyNotFoundException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionFamilyNamePolicy;
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
                configurationOptionCodeBindingRepository,
                new OptionFamilyCodePolicy(),
                new OptionFamilyNamePolicy()
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

    @Nested
    @DisplayName("createOptionFamily 校验测试（CR-035）")
    class CreateOptionFamilyValidationTests {

        @Test
        @DisplayName("合法标准 code + 无重名 -> 创建成功")
        void createOptionFamily_validCode_succeeds() {
            when(optionFamilyRepository.existsByCode("OF_EXT_BODY_COLOR")).thenReturn(false);
            when(optionFamilyRepository.findAllForNameCheck()).thenReturn(Collections.emptyList());
            when(optionFamilyRepository.save(any(), eq("CREATE"))).thenAnswer(inv -> inv.getArgument(0));

            OptionFamily result = productDomainService.createOptionFamily(
                    "OF_EXT_BODY_COLOR", "Body Color", "车身颜色", null,
                    OptionFamilyCategory.EXTERIOR, null, null, "admin");

            assertEquals("OF_EXT_BODY_COLOR", result.getCode());
            verify(optionFamilyRepository).save(any(), eq("CREATE"));
        }

        @Test
        @DisplayName("合法扩展 code + 无重名 -> 创建成功")
        void createOptionFamily_validExtensionCode_succeeds() {
            when(optionFamilyRepository.existsByCode("OF_EXT_X_OFFROAD_APPEARANCE")).thenReturn(false);
            when(optionFamilyRepository.findAllForNameCheck()).thenReturn(Collections.emptyList());
            when(optionFamilyRepository.save(any(), eq("CREATE"))).thenAnswer(inv -> inv.getArgument(0));

            OptionFamily result = productDomainService.createOptionFamily(
                    "OF_EXT_X_OFFROAD_APPEARANCE", "Off-road Appearance Package", "越野外观套件", null,
                    OptionFamilyCategory.EXTERIOR, null, null, "admin");

            assertEquals("OF_EXT_X_OFFROAD_APPEARANCE", result.getCode());
        }

        @Test
        @DisplayName("code 格式非法 -> 抛 812125，不查重/不保存")
        void createOptionFamily_invalidFormat_throws() {
            assertThrows(OptionFamilyCodeFormatInvalidException.class, () ->
                    productDomainService.createOptionFamily(
                            "of_ext_body_color", "Body Color", "车身颜色", null,
                            OptionFamilyCategory.EXTERIOR, null, null, "admin"));
            verify(optionFamilyRepository, never()).existsByCode(any());
            verify(optionFamilyRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("code 前缀与 category 不一致 -> 抛 812126")
        void createOptionFamily_prefixCategoryMismatch_throws() {
            assertThrows(OptionFamilyCategoryPrefixMismatchException.class, () ->
                    productDomainService.createOptionFamily(
                            "OF_EXT_BODY_COLOR", "Body Color", "车身颜色", null,
                            OptionFamilyCategory.INTERIOR, null, null, "admin"));
            verify(optionFamilyRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("英文名标准化后重复 -> 抛 812127")
        void createOptionFamily_duplicateName_throws() {
            when(optionFamilyRepository.existsByCode("OF_EXT_BODY_COLOR")).thenReturn(false);
            OptionFamily existing = OptionFamily.builder()
                    .code("OF_EXT_BODY_COLOR")
                    .name("Body Color")
                    .nameLocal("车身颜色")
                    .category(OptionFamilyCategory.EXTERIOR)
                    .status(OptionFamilyStatus.ACTIVE)
                    .rowValid(true)
                    .build();
            when(optionFamilyRepository.findAllForNameCheck()).thenReturn(Collections.singletonList(existing));

            assertThrows(OptionFamilyNameDuplicateException.class, () ->
                    productDomainService.createOptionFamily(
                            "OF_EXT_BODY_COLOR", "  body   color ", "车身颜色", null,
                            OptionFamilyCategory.EXTERIOR, null, null, "admin"));
            verify(optionFamilyRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("中文名全半角空格差异重复 -> 抛 812127")
        void createOptionFamily_duplicateChineseName_throws() {
            when(optionFamilyRepository.existsByCode(any())).thenReturn(false);
            OptionFamily existing = OptionFamily.builder()
                    .code("OF_EXT_BODY_COLOR")
                    .name("Body Color")
                    .nameLocal("车身颜色")
                    .category(OptionFamilyCategory.EXTERIOR)
                    .status(OptionFamilyStatus.ACTIVE)
                    .rowValid(true)
                    .build();
            when(optionFamilyRepository.findAllForNameCheck()).thenReturn(Collections.singletonList(existing));

            assertThrows(OptionFamilyNameDuplicateException.class, () ->
                    productDomainService.createOptionFamily(
                            "OF_EXT_WHEEL", "Wheel", "车身 颜色", null,
                            OptionFamilyCategory.EXTERIOR, null, null, "admin"));
        }

        @Test
        @DisplayName("code 已存在 -> 抛 DuplicateCodeException")
        void createOptionFamily_codeExists_throws() {
            when(optionFamilyRepository.existsByCode("OF_EXT_BODY_COLOR")).thenReturn(true);

            assertThrows(DuplicateCodeException.class, () ->
                    productDomainService.createOptionFamily(
                            "OF_EXT_BODY_COLOR", "Body Color", "车身颜色", null,
                            OptionFamilyCategory.EXTERIOR, null, null, "admin"));
            verify(optionFamilyRepository, never()).save(any(), any());
        }
    }

    @Nested
    @DisplayName("deleteOptionFamily 删除测试（下游选项码依赖检查）")
    class DeleteOptionFamilyTests {

        private OptionFamily draftFamily() {
            return OptionFamily.builder()
                    .code("OF_EXT_TRIM")
                    .name("Trim")
                    .category(OptionFamilyCategory.EXTERIOR)
                    .status(OptionFamilyStatus.DRAFT)
                    .rowValid(true)
                    .build();
        }

        @Test
        @DisplayName("存在子级选项码 -> 拒绝删除并抛 812108")
        void deleteOptionFamily_withChildren_rejects() {
            String code = "OF_EXT_TRIM";
            OptionFamily family = draftFamily();
            when(optionFamilyRepository.findByCode(code)).thenReturn(Optional.of(family));
            when(optionCodeRepository.existsByOptionFamilyCode(code)).thenReturn(true);

            OptionFamilyHasChildrenReferenceException ex = assertThrows(
                    OptionFamilyHasChildrenReferenceException.class,
                    () -> productDomainService.deleteOptionFamily(code, "admin"));

            assertEquals(code, ex.getOptionFamilyCode());
            assertTrue(family.getRowValid());
            verify(optionFamilyRepository, never()).save(any(), any());
            verify(optionFamilyRepository, never()).delete(any());
        }

        @Test
        @DisplayName("无子级选项码且 DRAFT -> 删除成功")
        void deleteOptionFamily_withoutChildren_succeeds() {
            String code = "OF_EXT_TRIM";
            OptionFamily family = draftFamily();
            when(optionFamilyRepository.findByCode(code)).thenReturn(Optional.of(family));
            when(optionCodeRepository.existsByOptionFamilyCode(code)).thenReturn(false);

            productDomainService.deleteOptionFamily(code, "admin");

            assertFalse(family.getRowValid());
            verify(optionFamilyRepository).delete(family);
        }

        @Test
        @DisplayName("ACTIVE 无子级选项码 -> 删除成功（删除不限状态）")
        void deleteOptionFamily_activeWithoutChildren_succeeds() {
            String code = "OF_EXT_TRIM";
            OptionFamily family = OptionFamily.builder()
                    .code(code)
                    .name("Trim")
                    .category(OptionFamilyCategory.EXTERIOR)
                    .status(OptionFamilyStatus.ACTIVE)
                    .rowValid(true)
                    .build();
            when(optionFamilyRepository.findByCode(code)).thenReturn(Optional.of(family));
            when(optionCodeRepository.existsByOptionFamilyCode(code)).thenReturn(false);

            productDomainService.deleteOptionFamily(code, "admin");

            assertFalse(family.getRowValid());
            verify(optionFamilyRepository).delete(family);
        }

        @Test
        @DisplayName("ACTIVE 存在子级选项码 -> 拒绝删除并抛 812108")
        void deleteOptionFamily_activeWithChildren_rejects() {
            String code = "OF_EXT_TRIM";
            OptionFamily family = OptionFamily.builder()
                    .code(code)
                    .name("Trim")
                    .category(OptionFamilyCategory.EXTERIOR)
                    .status(OptionFamilyStatus.ACTIVE)
                    .rowValid(true)
                    .build();
            when(optionFamilyRepository.findByCode(code)).thenReturn(Optional.of(family));
            when(optionCodeRepository.existsByOptionFamilyCode(code)).thenReturn(true);

            OptionFamilyHasChildrenReferenceException ex = assertThrows(
                    OptionFamilyHasChildrenReferenceException.class,
                    () -> productDomainService.deleteOptionFamily(code, "admin"));

            assertEquals(code, ex.getOptionFamilyCode());
            verify(optionFamilyRepository, never()).save(any(), any());
            verify(optionFamilyRepository, never()).delete(any());
        }

        @Test
        @DisplayName("选项族不存在 -> 抛 OptionFamilyNotFoundException")
        void deleteOptionFamily_notExist_throws() {
            String code = "OF_EXT_TRIM";
            when(optionFamilyRepository.findByCode(code)).thenReturn(Optional.empty());
            assertThrows(OptionFamilyNotFoundException.class,
                    () -> productDomainService.deleteOptionFamily(code, "admin"));
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
