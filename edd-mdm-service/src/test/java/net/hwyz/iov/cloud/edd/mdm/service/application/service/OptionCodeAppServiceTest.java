package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmBaseException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmErrorCode;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFamilyPrefixMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionCodeCodePolicy;
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
    @Mock
    private OutboxService outboxService;

    private OptionCodeAppService optionCodeAppService;

    @BeforeEach
    void setUp() {
        optionCodeAppService = new OptionCodeAppService(
                optionCodeRepository,
                optionFamilyRepository,
                variantOptionCodeBindingRepository,
                configurationOptionCodeBindingRepository,
                new OptionCodeCodePolicy(),
                outboxService
        );
    }

    @Nested
    @DisplayName("createOptionCode 创建测试（CR-040 校验顺序）")
    class CreateOptionCodeTests {

        private OptionFamily activeFamily() {
            return OptionFamily.builder()
                    .code("OF_EXT_BODY_COLOR")
                    .status(OptionFamilyStatus.ACTIVE)
                    .build();
        }

        private OptionCodeCreateCmd createCmd(String code, String optionFamilyCode) {
            return OptionCodeCreateCmd.builder()
                    .code(code).name("Black").nameLocal("黑色")
                    .optionFamilyCode(optionFamilyCode)
                    .createBy("admin")
                    .build();
        }

        @Test
        @DisplayName("族不存在 -> 先返回引用错误，不执行编码校验/保存")
        void familyMissing_returnsReferenceErrorFirst() {
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, () -> optionCodeAppService.createOptionCode(
                    createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR")));
            verify(optionCodeRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("族非 ACTIVE -> 先返回引用错误")
        void familyInactive_returnsReferenceErrorFirst() {
            OptionFamily family = OptionFamily.builder().code("OF_EXT_BODY_COLOR")
                    .status(OptionFamilyStatus.INACTIVE).build();
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.of(family));

            assertThrows(IllegalArgumentException.class, () -> optionCodeAppService.createOptionCode(
                    createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR")));
            verify(optionCodeRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("code 格式非法 -> 抛 812127，不保存")
        void invalidFormat_throws812127() {
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.of(activeFamily()));

            OptionCodeFormatInvalidException ex = assertThrows(OptionCodeFormatInvalidException.class,
                    () -> optionCodeAppService.createOptionCode(createCmd("CLR_BLACK", "OF_EXT_BODY_COLOR")));
            assertEquals(MdmErrorCode.OPTION_CODE_FORMAT_INVALID, ex.getErrorCode());
            verify(optionCodeRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("格式合法但族主干不一致 -> 抛 812128")
        void familyMismatch_throws812128() {
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.of(activeFamily()));

            OptionCodeFamilyPrefixMismatchException ex = assertThrows(OptionCodeFamilyPrefixMismatchException.class,
                    () -> optionCodeAppService.createOptionCode(createCmd("OC_EXT_WHEEL_BLACK", "OF_EXT_BODY_COLOR")));
            assertEquals(MdmErrorCode.OPTION_CODE_FAMILY_PREFIX_MISMATCH, ex.getErrorCode());
            verify(optionCodeRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("code 全局重复 -> 沿用既有 812101 重复语义（DuplicateCodeException）")
        void duplicate_throwsDuplicate() {
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.of(activeFamily()));
            when(optionCodeRepository.existsByCode("OC_EXT_BODY_COLOR_BLACK")).thenReturn(true);

            assertThrows(DuplicateCodeException.class, () -> optionCodeAppService
                    .createOptionCode(createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR")));
            verify(optionCodeRepository, never()).save(any(), any());
        }

        @Test
        @DisplayName("合法创建 -> 保存并返回")
        void validCreate_saves() {
            when(optionFamilyRepository.findByCode("OF_EXT_BODY_COLOR")).thenReturn(Optional.of(activeFamily()));
            when(optionCodeRepository.existsByCode("OC_EXT_BODY_COLOR_BLACK")).thenReturn(false);
            when(optionCodeRepository.save(any(), eq("CREATE"))).thenAnswer(inv -> inv.getArgument(0));

            OptionCodeDto dto = optionCodeAppService.createOptionCode(
                    createCmd("OC_EXT_BODY_COLOR_BLACK", "OF_EXT_BODY_COLOR"));

            assertNotNull(dto);
            assertEquals("OC_EXT_BODY_COLOR_BLACK", dto.getCode());
            verify(optionCodeRepository).save(any(), eq("CREATE"));
            verify(outboxService).publishOptionCodeCreatedEvent(any());
        }
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
