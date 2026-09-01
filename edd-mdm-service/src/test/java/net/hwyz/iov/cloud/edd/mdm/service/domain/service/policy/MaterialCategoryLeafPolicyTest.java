package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryNotLeafException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.PartCategoryInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * 物料品类叶子校验策略单元测试（CR-039 §7）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MaterialCategoryLeafPolicy 测试")
class MaterialCategoryLeafPolicyTest {

    @Mock
    private MaterialCategoryRepository repository;

    private MaterialCategoryLeafPolicy leafPolicy;

    private MaterialCategory cat(String code, String parentCode, MaterialCategoryStatus status) {
        return MaterialCategory.builder()
                .code(code).parentCode(parentCode).status(status).rowValid(true)
                .build();
    }

    @BeforeEach
    void setUp() {
        leafPolicy = new MaterialCategoryLeafPolicy(repository, new MaterialCategoryHierarchyPolicy());
        List<MaterialCategory> all = List.of(
                cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE),
                cat("MC_CMP_BODY_GLASS", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)
        );
        org.mockito.Mockito.lenient().when(repository.findAll()).thenReturn(all);
    }

    @Nested
    @DisplayName("可归类叶子正例")
    class LeafSuccessTests {

        @Test
        @DisplayName("标准 L3 叶子可被 Part 引用")
        void standardL3Assignable() {
            when(repository.findByCode("MC_CMP_BODY_BIW"))
                    .thenReturn(Optional.of(cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)));
            assertDoesNotThrow(() -> leafPolicy.assertAssignable("MC_CMP_BODY_BIW"));
        }
    }

    @Nested
    @DisplayName("非叶子反例（812923）")
    class NotLeafTests {

        @Test
        @DisplayName("L1 不能被 Part 引用")
        void l1NotAssignable() {
            when(repository.findByCode("MC_CMP"))
                    .thenReturn(Optional.of(cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE)));
            assertThrows(MaterialCategoryNotLeafException.class,
                    () -> leafPolicy.assertAssignable("MC_CMP"));
        }

        @Test
        @DisplayName("L2 不能被 Part 引用")
        void l2NotAssignable() {
            when(repository.findByCode("MC_CMP_BODY"))
                    .thenReturn(Optional.of(cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE)));
            assertThrows(MaterialCategoryNotLeafException.class,
                    () -> leafPolicy.assertAssignable("MC_CMP_BODY"));
        }

        @Test
        @DisplayName("L3 但存在 ACTIVE 子节点（第四层挂载）不能被 Part 引用")
        void l3WithActiveChildNotAssignable() {
            // MC_CMP_BODY_BIW 下存在 ACTIVE 子节点
            when(repository.findAll()).thenReturn(List.of(
                    cat("MC_CMP", null, MaterialCategoryStatus.ACTIVE),
                    cat("MC_CMP_BODY", "MC_CMP", MaterialCategoryStatus.ACTIVE),
                    cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE),
                    cat("MC_CMP_BODY_BIW_CHILD", "MC_CMP_BODY_BIW", MaterialCategoryStatus.ACTIVE)
            ));
            when(repository.findByCode("MC_CMP_BODY_BIW"))
                    .thenReturn(Optional.of(cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.ACTIVE)));
            assertThrows(MaterialCategoryNotLeafException.class,
                    () -> leafPolicy.assertAssignable("MC_CMP_BODY_BIW"));
        }
    }

    @Nested
    @DisplayName("不存在/非 ACTIVE 反例（812911）")
    class InvalidCategoryTests {

        @Test
        @DisplayName("不存在返回 812911")
        void notExist() {
            when(repository.findByCode("MC_XXX")).thenReturn(Optional.empty());
            assertThrows(PartCategoryInvalidException.class,
                    () -> leafPolicy.assertAssignable("MC_XXX"));
        }

        @Test
        @DisplayName("非 ACTIVE 返回 812911")
        void notActive() {
            when(repository.findByCode("MC_CMP_BODY_BIW"))
                    .thenReturn(Optional.of(cat("MC_CMP_BODY_BIW", "MC_CMP_BODY", MaterialCategoryStatus.INACTIVE)));
            assertThrows(PartCategoryInvalidException.class,
                    () -> leafPolicy.assertAssignable("MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("空 code 返回 812911")
        void blankCode() {
            assertThrows(PartCategoryInvalidException.class,
                    () -> leafPolicy.assertAssignable("  "));
        }
    }
}
