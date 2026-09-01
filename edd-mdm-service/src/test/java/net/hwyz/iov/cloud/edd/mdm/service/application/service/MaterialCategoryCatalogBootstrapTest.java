package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.catalog.MaterialCategoryCatalogEntry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryCatalogStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MaterialCategoryCatalogLoader;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 物料品类标准目录 Bootstrap 单元测试（CR-039 §6 拓扑幂等初始化）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialCategoryCatalogBootstrap 测试")
class MaterialCategoryCatalogBootstrapTest {

    @Mock
    private MaterialCategoryCatalogLoader catalogLoader;
    @Mock
    private MaterialCategoryAppService materialCategoryAppService;

    private MaterialCategoryCatalogBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new MaterialCategoryCatalogBootstrap(catalogLoader, materialCategoryAppService);
        org.mockito.Mockito.lenient().when(catalogLoader.loadVersion()).thenReturn(1);
    }

    private List<MaterialCategoryCatalogEntry> catalog() {
        return List.of(
                entry("MC_CMP", 1, "Component / Part", "零部件", null, 1),
                entry("MC_CMP_BODY", 2, "Body Structure & Closures", "车身结构与闭合", "MC_CMP", 1),
                entry("MC_CMP_BODY_BIW", 3, "Body-in-White", "白车身", "MC_CMP_BODY", 1)
        );
    }

    private MaterialCategoryCatalogEntry entry(String code, int level, String name, String nameLocal,
                                               String parentCode, int sortOrder) {
        return MaterialCategoryCatalogEntry.builder()
                .code(code).level(level).name(name).nameLocal(nameLocal)
                .parentCode(parentCode).sortOrder(sortOrder)
                .build();
    }

    private MaterialCategoryDto dto(String code, String name, String nameLocal, String description,
                                    String parentCode) {
        return MaterialCategoryDto.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description).parentCode(parentCode)
                .build();
    }

    @Nested
    @DisplayName("首次初始化（拓扑顺序）")
    class FirstImportTests {

        @Test
        @DisplayName("全部条目创建，且严格按 L1→L2→L3 拓扑顺序走 AppService")
        void firstImportCreatesAllInTopologicalOrder() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory(any())).thenReturn(false);
            when(materialCategoryAppService.createMaterialCategory(any()))
                    .thenAnswer(inv -> dto(inv.getArgument(0, MaterialCategoryCreateCmd.class).getCode(),
                            "x", "x", "x", "x"));

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(3, result.getCreated());
            assertEquals(0, result.getSkipped());
            assertEquals(0, result.getConflicted());
            assertEquals(0, result.getFailed());
            assertEquals(0, result.getDependencyFailed());
            assertEquals(MaterialCategoryCatalogStatus.VALID.name(), result.getCatalogStatus());
            assertEquals(1, result.getCatalogVersion());
            assertEquals(MaterialCategoryCatalogBootstrap.BOOTSTRAP_OPERATOR, result.getOperator());
            ArgumentCaptor<MaterialCategoryCreateCmd> captor = ArgumentCaptor.forClass(MaterialCategoryCreateCmd.class);
            verify(materialCategoryAppService, times(3)).createMaterialCategory(captor.capture());
            assertEquals("MC_CMP", captor.getAllValues().get(0).getCode());
            assertEquals("MC_CMP_BODY", captor.getAllValues().get(1).getCode());
            assertEquals("MC_CMP_BODY_BIW", captor.getAllValues().get(2).getCode());
            assertEquals("MC_CMP", captor.getAllValues().get(1).getParentCode());
            assertEquals("system", captor.getAllValues().get(0).getCreateBy());
        }
    }

    @Nested
    @DisplayName("幂等跳过")
    class SkipTests {

        @Test
        @DisplayName("已存在且完全一致则跳过，不重复创建")
        void existingMatchingSkipped() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory(any())).thenReturn(true);
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP"))
                    .thenReturn(dto("MC_CMP", "Component / Part", "零部件", null, null));
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP_BODY"))
                    .thenReturn(dto("MC_CMP_BODY", "Body Structure & Closures", "车身结构与闭合", null, "MC_CMP"));
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP_BODY_BIW"))
                    .thenReturn(dto("MC_CMP_BODY_BIW", "Body-in-White", "白车身", null, "MC_CMP_BODY"));

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(3, result.getSkipped());
            assertEquals(0, result.getCreated());
            verify(materialCategoryAppService, never()).createMaterialCategory(any());
        }
    }

    @Nested
    @DisplayName("冲突不覆盖")
    class ConflictTests {

        @Test
        @DisplayName("已存在但名称/父级不一致则记录冲突并跳过，不覆盖业务数据")
        void existingConflictNotOverwritten() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory(any())).thenReturn(true);
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP"))
                    .thenReturn(dto("MC_CMP", "Component / Part", "零部件", null, null));
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP_BODY"))
                    .thenReturn(dto("MC_CMP_BODY", "Different Name", "不同名称", null, "MC_CMP"));
            // MC_CMP_BODY_BIW 依赖失败（父冲突未就绪），不会被查询

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(1, result.getSkipped());
            assertEquals(1, result.getConflicted());
            assertEquals(1, result.getDependencyFailed());
            assertEquals(0, result.getCreated());
            verify(materialCategoryAppService, never()).createMaterialCategory(any());
            assertTrue(result.getDetails().stream().anyMatch(d -> d.startsWith("MC_CMP_BODY")));
        }
    }

    @Nested
    @DisplayName("父节点依赖失败隔离")
    class DependencyFailureTests {

        @Test
        @DisplayName("父节点冲突未就绪时，其子树标记 dependency failed，其他分支继续")
        void parentConflictCausesDependencyFailure() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory(any())).thenReturn(true);
            // MC_CMP 冲突（未就绪）→ MC_CMP_BODY / MC_CMP_BODY_BIW 依赖失败
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP"))
                    .thenReturn(dto("MC_CMP", "Different", "不同", null, null));

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(1, result.getConflicted());
            assertEquals(2, result.getDependencyFailed());
            assertEquals(0, result.getCreated());
            verify(materialCategoryAppService, never()).createMaterialCategory(any());
            assertTrue(result.getDetails().stream()
                    .anyMatch(d -> d.startsWith("MC_CMP_BODY") && d.contains("依赖失败")));
        }
    }

    @Nested
    @DisplayName("单条失败不回滚")
    class FailureTests {

        @Test
        @DisplayName("单条创建失败记录 failed，其余仍成功")
        void singleFailureDoesNotRollbackOthers() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory(any())).thenReturn(false);
            when(materialCategoryAppService.createMaterialCategory(any()))
                    .thenAnswer(inv -> dto(inv.getArgument(0, MaterialCategoryCreateCmd.class).getCode(),
                            "x", "x", "x", "x"));
            doThrow(new RuntimeException("DB down"))
                    .when(materialCategoryAppService).createMaterialCategory(
                    argThat(cmd -> "MC_CMP_BODY".equals(cmd.getCode())));

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(1, result.getCreated());
            assertEquals(1, result.getFailed());
            // MC_CMP_BODY 失败 → 其子 MC_CMP_BODY_BIW 依赖失败
            assertEquals(1, result.getDependencyFailed());
            assertTrue(result.getDetails().stream().anyMatch(d -> d.startsWith("MC_CMP_BODY")));
        }
    }

    @Nested
    @DisplayName("目录非法禁用")
    class InvalidCatalogTests {

        @Test
        @DisplayName("目录加载失败时标记 INVALID 且不执行任何初始化")
        void invalidCatalogDisabled() {
            when(catalogLoader.load()).thenThrow(new IllegalStateException("目录规模非法"));

            MaterialCategoryCatalogBootstrapResult result = bootstrap.bootstrap();

            assertEquals(MaterialCategoryCatalogStatus.INVALID.name(), result.getCatalogStatus());
            assertEquals(0, result.getCreated());
            verify(materialCategoryAppService, never()).createMaterialCategory(any());
        }

        @Test
        @DisplayName("目录非法时 preview 返回 INVALID")
        void invalidCatalogPreview() {
            when(catalogLoader.load()).thenThrow(new IllegalStateException("目录规模非法"));

            MaterialCategoryCatalogPreviewResult result = bootstrap.preview();

            assertEquals(MaterialCategoryCatalogStatus.INVALID.name(), result.getCatalogStatus());
        }
    }

    @Nested
    @DisplayName("预检 preview")
    class PreviewTests {

        @Test
        @DisplayName("统计 missing/initialized/conflict 与 level 计数")
        void previewCounts() {
            when(catalogLoader.load()).thenReturn(catalog());
            when(materialCategoryAppService.existsMaterialCategory("MC_CMP")).thenReturn(true);
            when(materialCategoryAppService.existsMaterialCategory("MC_CMP_BODY")).thenReturn(true);
            when(materialCategoryAppService.existsMaterialCategory("MC_CMP_BODY_BIW")).thenReturn(false);
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP"))
                    .thenReturn(dto("MC_CMP", "Component / Part", "零部件", null, null));
            when(materialCategoryAppService.getMaterialCategoryByCode("MC_CMP_BODY"))
                    .thenReturn(dto("MC_CMP_BODY", "Changed", "改变", null, "MC_CMP"));

            MaterialCategoryCatalogPreviewResult preview = bootstrap.preview();

            assertEquals(MaterialCategoryCatalogStatus.VALID.name(), preview.getCatalogStatus());
            assertEquals(1, preview.getCatalogVersion());
            assertEquals(3, preview.getTotal());
            assertEquals(1, preview.getLevel1Count());
            assertEquals(1, preview.getLevel2Count());
            assertEquals(1, preview.getLevel3Count());
            assertEquals(1, preview.getInitialized());
            assertEquals(1, preview.getMissing());
            assertEquals(1, preview.getConflicted());
            assertEquals(3, preview.getItems().size());
            assertTrue(preview.getConflicts().stream().anyMatch(c -> c.startsWith("MC_CMP_BODY")));
        }
    }
}
