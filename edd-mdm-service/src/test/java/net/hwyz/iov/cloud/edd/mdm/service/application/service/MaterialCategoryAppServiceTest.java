package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryCodeFormatInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryDepthExceededException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryNameDuplicateException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryParentNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryAbbreviationRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryHierarchyPolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.MaterialCategoryNamePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 物料分类应用服务单元测试（CR-039 创建/更新治理策略接线）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialCategoryAppService 测试")
class MaterialCategoryAppServiceTest {

    @Mock
    private MaterialCategoryRepository repository;
    @Mock
    private OutboxService outboxService;

    private MaterialCategoryAppService appService;

    private MaterialCategory cat(String code, String parentCode, MaterialCategoryStatus status) {
        return MaterialCategory.create(code, code, code, null, parentCode, null, null, "test");
    }

    @BeforeEach
    void setUp() {
        appService = new MaterialCategoryAppService(
                repository,
                outboxService,
                new MaterialCategoryCodePolicy(new MaterialCategoryAbbreviationRegistry()),
                new MaterialCategoryHierarchyPolicy(),
                new MaterialCategoryNamePolicy());
    }

    private void stubCatalog() {
        List<MaterialCategory> all = List.of(
                cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)
        );
        org.mockito.Mockito.lenient().when(repository.findAll()).thenReturn(all);
        org.mockito.Mockito.lenient().when(repository.save(any(), anyString())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    @DisplayName("创建治理")
    class CreateTests {

        @Test
        @DisplayName("标准 L1 创建成功")
        void createL1Success() {
            stubCatalog();
            when(repository.existsByCode("MC_RAW")).thenReturn(false);
            assertDoesNotThrow(() -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_RAW").name("Raw Material").nameLocal("原材料").createBy("test").build()));
            verify(outboxService).publishMaterialCategoryCreatedEvent(any());
        }

        @Test
        @DisplayName("标准 L3 创建成功（父级存在且未超深）")
        void createL3Success() {
            stubCatalog();
            when(repository.existsByCode("MC_CMP_BODY_GLASS")).thenReturn(false);
            assertDoesNotThrow(() -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                    .code("MC_CMP_BODY_GLASS").name("Vehicle Glass").nameLocal("汽车玻璃")
                    .parentCode("MC_CMP_BODY").createBy("test").build()));
        }

        @Test
        @DisplayName("code 格式非法返回 812924")
        void createInvalidFormat() {
            stubCatalog();
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                            .code("MC_CMP_BODY_biw").name("x").nameLocal("x")
                            .parentCode("MC_CMP_BODY").createBy("test").build()));
            verify(repository, never()).save(any(), anyString());
        }

        @Test
        @DisplayName("code 重复返回 812902")
        void createDuplicateCode() {
            stubCatalog();
            when(repository.existsByCode("MC_CMP_BODY")).thenReturn(true);
            assertThrows(MaterialCategoryDuplicateCodeException.class,
                    () -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                            .code("MC_CMP_BODY").name("x").nameLocal("x")
                            .parentCode("MC_CMP").createBy("test").build()));
        }

        @Test
        @DisplayName("父级不存在（前缀祖先被删除）返回 812905")
        void createParentNotExist() {
            // 现存目录不含 MC_CMP_BODY（被删除），MC_CMP_BODY_GLASS 的父级前缀匹配但父不存在
            List<MaterialCategory> all = List.of(
                    cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                    cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)
            );
            when(repository.findAll()).thenReturn(all);
            when(repository.existsByCode("MC_CMP_BODY_GLASS")).thenReturn(false);
            assertThrows(MaterialCategoryParentNotExistException.class,
                    () -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                            .code("MC_CMP_BODY_GLASS").name("Vehicle Glass").nameLocal("汽车玻璃")
                            .parentCode("MC_CMP_BODY").createBy("test").build()));
        }

        @Test
        @DisplayName("在 L3 下创建第四层返回 812926")
        void createFourthLevel() {
            stubCatalog();
            when(repository.existsByCode("MC_CMP_BODY_BIW_CHILD")).thenReturn(false);
            assertThrows(MaterialCategoryDepthExceededException.class,
                    () -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                            .code("MC_CMP_BODY_BIW_CHILD").name("Child").nameLocal("子项")
                            .parentCode("MC_CMP_BODY_BIW").createBy("test").build()));
        }

        @Test
        @DisplayName("名称标准化后重复返回 812925")
        void createNameDuplicate() {
            stubCatalog();
            when(repository.existsByCode("MC_CMP_BODY_GLASS")).thenReturn(false);
            // name 与 MC_CMP_BODY_BIW 的 name（code 即 name）标准化后相同 → 812925
            assertThrows(MaterialCategoryNameDuplicateException.class,
                    () -> appService.createMaterialCategory(MaterialCategoryCreateCmd.builder()
                            .code("MC_CMP_BODY_GLASS").name("MC_CMP_BODY_BIW").nameLocal("白车身")
                            .parentCode("MC_CMP_BODY").createBy("test").build()));
        }
    }

    @Nested
    @DisplayName("更新治理")
    class UpdateTests {

        @Test
        @DisplayName("非分类普通更新（父级/名称不变）成功")
        void updateOrdinarySuccess() {
            stubCatalog();
            MaterialCategory existing = cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE);
            when(repository.findByCode("MC_CMP_BODY")).thenReturn(Optional.of(existing));
            assertDoesNotThrow(() -> appService.updateMaterialCategory(MaterialCategoryUpdateCmd.builder()
                    .code("MC_CMP_BODY").name("MC_CMP_BODY").nameLocal("MC_CMP_BODY")
                    .parentCode("MC_CMP").modifyBy("test").build()));
        }

        @Test
        @DisplayName("换父违反父子前缀规则返回 812924（层级语义与父链前缀强约束）")
        void updateReparentPrefixViolation() {
            stubCatalog();
            MaterialCategory existing = cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE);
            when(repository.findByCode("MC_CMP")).thenReturn(Optional.of(existing));
            assertThrows(MaterialCategoryCodeFormatInvalidException.class,
                    () -> appService.updateMaterialCategory(MaterialCategoryUpdateCmd.builder()
                            .code("MC_CMP").name("MC_CMP").nameLocal("MC_CMP")
                            .parentCode("MC_CMP_BODY_BIW").modifyBy("test").build()));
        }

        @Test
        @DisplayName("换父到不存在父级（前缀祖先被删除）返回 812905")
        void updateParentNotExist() {
            // 现存目录不含 MC_CMP_BODY（被删除），换父到前缀匹配但不存在的 MC_CMP_BODY → 812905
            List<MaterialCategory> all = List.of(
                    cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                    cat("MC_CMP_BODY_BIW", "MC_CMP", MaterialCategoryStatus.ACTIVE)
            );
            when(repository.findAll()).thenReturn(all);
            MaterialCategory existing = cat("MC_CMP_BODY_BIW", "MC_CMP", MaterialCategoryStatus.ACTIVE);
            when(repository.findByCode("MC_CMP_BODY_BIW")).thenReturn(Optional.of(existing));
            assertThrows(MaterialCategoryParentNotExistException.class,
                    () -> appService.updateMaterialCategory(MaterialCategoryUpdateCmd.builder()
                            .code("MC_CMP_BODY_BIW").name("MC_CMP_BODY_BIW").nameLocal("MC_CMP_BODY_BIW")
                            .parentCode("MC_CMP_BODY").modifyBy("test").build()));
        }

        @Test
        @DisplayName("名称变化且与现存重复返回 812925")
        void updateNameDuplicate() {
            stubCatalog();
            MaterialCategory existing = cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE);
            when(repository.findByCode("MC_CMP_BODY")).thenReturn(Optional.of(existing));
            assertThrows(MaterialCategoryNameDuplicateException.class,
                    () -> appService.updateMaterialCategory(MaterialCategoryUpdateCmd.builder()
                            .code("MC_CMP_BODY").name("MC_CMP_BODY_BIW").nameLocal("白车身")
                            .parentCode("MC_CMP").modifyBy("test").build()));
        }

        @Test
        @DisplayName("不存在返回 812901")
        void updateNotExist() {
            stubCatalog();
            when(repository.findByCode("MC_XXX")).thenReturn(Optional.empty());
            assertThrows(MaterialCategoryNotExistException.class,
                    () -> appService.updateMaterialCategory(MaterialCategoryUpdateCmd.builder()
                            .code("MC_XXX").name("x").nameLocal("x").modifyBy("test").build()));
        }
    }
}
