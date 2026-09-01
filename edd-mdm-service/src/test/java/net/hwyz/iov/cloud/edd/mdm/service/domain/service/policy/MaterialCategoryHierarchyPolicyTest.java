package net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryDepthExceededException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MaterialCategoryLoopDetectedException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.MaterialCategoryStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 物料品类层级策略单元测试（CR-039 §2）
 *
 * @author hwyz_leo
 */
@DisplayName("MaterialCategoryHierarchyPolicy 测试")
class MaterialCategoryHierarchyPolicyTest {

    private MaterialCategoryHierarchyPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new MaterialCategoryHierarchyPolicy();
    }

    private MaterialCategory cat(String code, String parentCode) {
        return MaterialCategory.builder()
                .code(code).parentCode(parentCode)
                .status(MaterialCategoryStatus.ACTIVE).rowValid(true)
                .build();
    }

    private Map<String, MaterialCategory> catalog() {
        Map<String, MaterialCategory> map = new HashMap<>();
        map.put("MC_CMP", cat("MC_CMP", null));
        map.put("MC_CMP_BODY", cat("MC_CMP_BODY", "MC_CMP"));
        map.put("MC_CMP_BODY_BIW", cat("MC_CMP_BODY_BIW", "MC_CMP_BODY"));
        map.put("MC_RAW", cat("MC_RAW", null));
        return map;
    }

    @Nested
    @DisplayName("深度计算")
    class DepthTests {

        @Test
        @DisplayName("L1/L2/L3 深度分别为 1/2/3")
        void computeDepthPerLevel() {
            Map<String, MaterialCategory> map = catalog();
            assertEquals(1, policy.computeDepth(map, "MC_CMP"));
            assertEquals(2, policy.computeDepth(map, "MC_CMP_BODY"));
            assertEquals(3, policy.computeDepth(map, "MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("空 code 深度为 0")
        void nullCodeDepthZero() {
            assertEquals(0, policy.computeDepth(catalog(), null));
        }
    }

    @Nested
    @DisplayName("最大深度（禁止第四层 812926）")
    class MaxDepthTests {

        @Test
        @DisplayName("在 L3 下创建子节点（第四层）被拒绝")
        void createUnderL3Rejected() {
            Map<String, MaterialCategory> map = catalog();
            assertThrows(MaterialCategoryDepthExceededException.class,
                    () -> policy.assertNotExceedingDepth(map, "MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("在 L1/L2 下创建子节点合法")
        void createUnderL1OrL2Allowed() {
            Map<String, MaterialCategory> map = catalog();
            assertDoesNotThrowUnderL1(map);
            assertDoesNotThrowUnderL2(map);
        }

        private void assertDoesNotThrowUnderL1(Map<String, MaterialCategory> map) {
            policy.assertNotExceedingDepth(map, "MC_CMP");
        }

        private void assertDoesNotThrowUnderL2(Map<String, MaterialCategory> map) {
            policy.assertNotExceedingDepth(map, "MC_CMP_BODY");
        }
    }

    @Nested
    @DisplayName("环路检测（812906）")
    class LoopTests {

        @Test
        @DisplayName("把父级设为自身形成环路被拒绝")
        void selfLoopRejected() {
            assertThrows(MaterialCategoryLoopDetectedException.class,
                    () -> policy.assertNoLoop(catalog(), "MC_CMP_BODY", "MC_CMP_BODY"));
        }

        @Test
        @DisplayName("把父级设为后代形成环路被拒绝")
        void descendantLoopRejected() {
            assertThrows(MaterialCategoryLoopDetectedException.class,
                    () -> policy.assertNoLoop(catalog(), "MC_CMP", "MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("合法换父不触发环路")
        void validReparentAllowed() {
            Map<String, MaterialCategory> map = catalog();
            policy.assertNoLoop(map, "MC_CMP_BODY", "MC_RAW");
        }
    }

    @Nested
    @DisplayName("叶子判定")
    class LeafTests {

        @Test
        @DisplayName("深度=3 且无 ACTIVE 子节点为叶子")
        void l3Leaf() {
            assertTrue(policy.isLeaf(catalog(), "MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("L1/L2 非叶子")
        void l1L2NotLeaf() {
            assertFalse(policy.isLeaf(catalog(), "MC_CMP"));
            assertFalse(policy.isLeaf(catalog(), "MC_CMP_BODY"));
        }

        @Test
        @DisplayName("存在 ACTIVE 子节点则非叶子")
        void withActiveChildNotLeaf() {
            Map<String, MaterialCategory> map = catalog();
            map.put("MC_CMP_BODY_BIW_CHILD", cat("MC_CMP_BODY_BIW_CHILD", "MC_CMP_BODY_BIW"));
            assertFalse(policy.isLeaf(map, "MC_CMP_BODY_BIW"));
        }

        @Test
        @DisplayName("仅 INACTIVE 子节点不影响叶子判定")
        void inactiveChildStillLeaf() {
            Map<String, MaterialCategory> map = catalog();
            MaterialCategory child = cat("MC_CMP_BODY_BIW_CHILD", "MC_CMP_BODY_BIW");
            child.setStatus(MaterialCategoryStatus.INACTIVE);
            map.put("MC_CMP_BODY_BIW_CHILD", child);
            assertTrue(policy.isLeaf(map, "MC_CMP_BODY_BIW"));
        }
    }
}
