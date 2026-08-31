package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.OptionFamilyCatalogTier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.OptionFamilyCatalogLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 选项族标准目录 Bootstrap 单元测试（CR-035 §5.3 幂等导入）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OptionFamilyCatalogBootstrap 测试")
class OptionFamilyCatalogBootstrapTest {

    @Mock
    private OptionFamilyCatalogLoader catalogLoader;
    @Mock
    private OptionFamilyAppService optionFamilyAppService;

    private OptionFamilyCatalogBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new OptionFamilyCatalogBootstrap(catalogLoader, optionFamilyAppService);
    }

    private List<OptionFamilyCatalogEntry> catalog() {
        return List.of(
                entry(OptionFamilyCatalogTier.CORE, "OF_EXT_BODY_COLOR", "Body Color", "车身颜色", OptionFamilyCategory.EXTERIOR, null),
                entry(OptionFamilyCatalogTier.CORE, "OF_EXT_WHEEL", "Wheel", "轮毂", OptionFamilyCategory.EXTERIOR, null),
                entry(OptionFamilyCatalogTier.CONDITIONAL, "OF_EXT_STYLE_PACKAGE", "Exterior Styling Package", "外观风格套件",
                        OptionFamilyCategory.EXTERIOR, "存在不可拆外观套件")
        );
    }

    private OptionFamilyCatalogEntry entry(OptionFamilyCatalogTier tier, String code, String name, String nameLocal,
                                           OptionFamilyCategory category, String activationCondition) {
        return OptionFamilyCatalogEntry.builder()
                .tier(tier).code(code).name(name).nameLocal(nameLocal)
                .category(category).activationCondition(activationCondition)
                .build();
    }

    private OptionFamilyDto dto(String code, String name, String nameLocal, OptionFamilyCategory category) {
        return OptionFamilyDto.builder()
                .code(code).name(name).nameLocal(nameLocal)
                .category(category.name())
                .build();
    }

    @Nested
    @DisplayName("首次导入")
    class FirstImportTests {

        @Test
        @DisplayName("只导入 CORE，创建走 AppService，Conditional 不创建")
        void createAllCore() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(optionFamilyAppService.existsOptionFamily(any())).thenReturn(false);

            OptionFamilyCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(2, result.getCreated());
            assertEquals(0, result.getSkipped());
            assertEquals(0, result.getConflicted());
            assertEquals(0, result.getFailed());
            // 只创建 2 个 CORE，Conditional 不创建
            ArgumentCaptor<OptionFamilyCreateCmd> captor = ArgumentCaptor.forClass(OptionFamilyCreateCmd.class);
            verify(optionFamilyAppService, times(2)).createOptionFamily(captor.capture());
            List<OptionFamilyCreateCmd> cmds = captor.getAllValues();
            assertTrue(cmds.stream().noneMatch(c -> c.getCode().equals("OF_EXT_STYLE_PACKAGE")));
            // 系统身份执行
            assertTrue(cmds.stream().allMatch(c -> "system".equals(c.getCreateBy())));
            // 不覆盖 existing 查询（全部新建）
            verify(optionFamilyAppService, never()).getOptionFamilyByCode(any());
        }

        @Test
        @DisplayName("Conditional 条目即使不存在也不创建")
        void conditionalNeverCreated() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(optionFamilyAppService.existsOptionFamily(any())).thenReturn(false);
            bootstrap.bootstrap();
            verify(optionFamilyAppService, times(2)).createOptionFamily(any());
        }
    }

    @Nested
    @DisplayName("重复执行幂等")
    class IdempotentTests {

        @Test
        @DisplayName("全部已存在且完全一致 -> 全部跳过，不创建")
        void allSkipped() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(optionFamilyAppService.existsOptionFamily(any())).thenReturn(true);
            when(optionFamilyAppService.getOptionFamilyByCode("OF_EXT_BODY_COLOR"))
                    .thenReturn(dto("OF_EXT_BODY_COLOR", "Body Color", "车身颜色", OptionFamilyCategory.EXTERIOR));
            when(optionFamilyAppService.getOptionFamilyByCode("OF_EXT_WHEEL"))
                    .thenReturn(dto("OF_EXT_WHEEL", "Wheel", "轮毂", OptionFamilyCategory.EXTERIOR));

            OptionFamilyCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(0, result.getCreated());
            assertEquals(2, result.getSkipped());
            assertEquals(0, result.getConflicted());
            assertEquals(0, result.getFailed());
            verify(optionFamilyAppService, never()).createOptionFamily(any());
        }

        @Test
        @DisplayName("已存在但语义不一致 -> 冲突跳过，不覆盖业务数据")
        void conflictedSkipped() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(optionFamilyAppService.existsOptionFamily(any())).thenReturn(true);
            // 现存 name 与目录不一致（Body Color 已被改为 "Exterior Color"）
            when(optionFamilyAppService.getOptionFamilyByCode("OF_EXT_BODY_COLOR"))
                    .thenReturn(dto("OF_EXT_BODY_COLOR", "Exterior Color", "车身颜色", OptionFamilyCategory.EXTERIOR));
            when(optionFamilyAppService.getOptionFamilyByCode("OF_EXT_WHEEL"))
                    .thenReturn(dto("OF_EXT_WHEEL", "Wheel", "轮毂", OptionFamilyCategory.EXTERIOR));

            OptionFamilyCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(1, result.getConflicted());
            assertEquals(1, result.getSkipped());
            assertEquals(0, result.getCreated());
            verify(optionFamilyAppService, never()).createOptionFamily(any());
        }
    }

    @Nested
    @DisplayName("失败处理")
    class FailureTests {

        @Test
        @DisplayName("单条创建失败不影响其它成功项")
        void singleFailureContinues() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(optionFamilyAppService.existsOptionFamily(any())).thenReturn(false);
            // OF_EXT_BODY_COLOR 创建失败，OF_EXT_WHEEL 成功
            when(optionFamilyAppService.createOptionFamily(any())).thenThrow(
                    new RuntimeException("mock create failure"));

            OptionFamilyCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(2, result.getFailed());
            assertEquals(0, result.getCreated());
            assertTrue(result.getDetails().size() >= 2);
        }
    }
}
