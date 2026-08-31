package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmBaseException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmErrorCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
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
 * 选项码应用服务单元测试
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OptionCodeAppService 测试")
class OptionCodeAppServiceTest {

    @Mock
    private OptionCodeRepository optionCodeRepository;
    @Mock
    private OptionFamilyRepository optionFamilyRepository;
    @Mock
    private VariantOptionCodeBindingRepository variantOptionCodeBindingRepository;
    @Mock
    private ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;

    private OptionCodeAppService optionCodeAppService;

    @BeforeEach
    void setUp() {
        optionCodeAppService = new OptionCodeAppService(
                optionCodeRepository,
                optionFamilyRepository,
                variantOptionCodeBindingRepository,
                configurationOptionCodeBindingRepository
        );
    }

    @Nested
    @DisplayName("deleteOptionCode 删除测试（绑定依赖检查）")
    class DeleteOptionCodeTests {

        private OptionCode activeCode() {
            return OptionCode.builder()
                    .code("OC_BODY_COLOR_RED")
                    .name("Red")
                    .optionFamilyCode("OF_EXT_BODY_COLOR")
                    .status(OptionCodeStatus.ACTIVE)
                    .rowValid(true)
                    .build();
        }

        @Test
        @DisplayName("无绑定且任意状态 -> 删除成功")
        void deleteOptionCode_notBound_succeeds() {
            String code = "OC_BODY_COLOR_RED";
            OptionCode optionCode = activeCode();
            when(optionCodeRepository.findByCode(code)).thenReturn(Optional.of(optionCode));
            when(variantOptionCodeBindingRepository.existsByOptionCodeCode(code)).thenReturn(false);
            when(configurationOptionCodeBindingRepository.existsByOptionCodeCode(code)).thenReturn(false);

            optionCodeAppService.deleteOptionCode(code, "admin");

            assertFalse(optionCode.getRowValid());
            verify(optionCodeRepository).delete(optionCode);
        }

        @Test
        @DisplayName("被 Variant 绑定 -> 拒绝删除并抛 812108")
        void deleteOptionCode_variantBound_rejects() {
            String code = "OC_BODY_COLOR_RED";
            OptionCode optionCode = activeCode();
            when(optionCodeRepository.findByCode(code)).thenReturn(Optional.of(optionCode));
            when(variantOptionCodeBindingRepository.existsByOptionCodeCode(code)).thenReturn(true);

            MdmBaseException ex = assertThrows(MdmBaseException.class,
                    () -> optionCodeAppService.deleteOptionCode(code, "admin"));

            assertEquals(MdmErrorCode.HAS_CHILDREN_REFERENCE, ex.getErrorCode());
            verify(optionCodeRepository, never()).save(any(), any());
            verify(optionCodeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("被 Configuration 绑定 -> 拒绝删除并抛 812108")
        void deleteOptionCode_configurationBound_rejects() {
            String code = "OC_BODY_COLOR_RED";
            OptionCode optionCode = activeCode();
            when(optionCodeRepository.findByCode(code)).thenReturn(Optional.of(optionCode));
            when(variantOptionCodeBindingRepository.existsByOptionCodeCode(code)).thenReturn(false);
            when(configurationOptionCodeBindingRepository.existsByOptionCodeCode(code)).thenReturn(true);

            MdmBaseException ex = assertThrows(MdmBaseException.class,
                    () -> optionCodeAppService.deleteOptionCode(code, "admin"));

            assertEquals(MdmErrorCode.HAS_CHILDREN_REFERENCE, ex.getErrorCode());
            verify(optionCodeRepository, never()).save(any(), any());
            verify(optionCodeRepository, never()).delete(any());
        }

        @Test
        @DisplayName("选项码不存在 -> 抛 IllegalArgumentException")
        void deleteOptionCode_notExist_throws() {
            String code = "OC_NOT_EXIST";
            when(optionCodeRepository.findByCode(code)).thenReturn(Optional.empty());
            assertThrows(IllegalArgumentException.class,
                    () -> optionCodeAppService.deleteOptionCode(code, "admin"));
        }
    }
}
