package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineFrozenException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineStatusInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TaBaselineStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 型式批准基线聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("TypeApprovalBaseline 聚合根测试")
class TypeApprovalBaselineTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 初始 DRAFT 状态、version=1")
        void create_success() {
            TypeApprovalBaseline baseline = TypeApprovalBaseline.create(
                    "TAB000000000000000001",
                    "SWIN001",
                    AnchorType.VARIANT,
                    "VAR001",
                    "sha256:abc123",
                    "SWB001,SWB002",
                    "admin"
            );

            assertEquals("TAB000000000000000001", baseline.getTaBaselineCode());
            assertEquals("SWIN001", baseline.getSwinCode());
            assertEquals(AnchorType.VARIANT, baseline.getAnchorType());
            assertEquals("VAR001", baseline.getAnchorCode());
            assertEquals(TaBaselineStatus.DRAFT, baseline.getStatus());
            assertEquals("sha256:abc123", baseline.getProjectionDigest());
            assertEquals("SWB001,SWB002", baseline.getSourceBaselineScope());
            assertEquals(1, baseline.getVersion());
            assertEquals("admin", baseline.getCreateBy());
            assertEquals("admin", baseline.getModifyBy());
            assertEquals(0, baseline.getRowVersion());
            assertTrue(baseline.getRowValid());
            assertNotNull(baseline.getItems());
            assertTrue(baseline.getItems().isEmpty());
        }
    }

    @Nested
    @DisplayName("release 发布方法")
    class ReleaseTests {

        @Test
        @DisplayName("DRAFT -> RELEASED 成功 - 写入 effectiveFrom")
        void release_draft_success() {
            TypeApprovalBaseline baseline = createTestBaseline();

            baseline.release("admin");

            assertEquals(TaBaselineStatus.RELEASED, baseline.getStatus());
            assertNotNull(baseline.getEffectiveFrom());
            assertEquals(2, baseline.getVersion());
            assertEquals("admin", baseline.getModifyBy());
        }

        @Test
        @DisplayName("RELEASED -> RELEASED 抛出 TaBaselineStatusInvalidException")
        void release_released_throwsStatusInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");

            assertThrows(TaBaselineStatusInvalidException.class, () ->
                    baseline.release("admin"));
        }

        @Test
        @DisplayName("FROZEN -> RELEASED 抛出 TaBaselineStatusInvalidException")
        void release_frozen_throwsStatusInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");
            baseline.freeze("admin");

            assertThrows(TaBaselineStatusInvalidException.class, () ->
                    baseline.release("admin"));
        }
    }

    @Nested
    @DisplayName("freeze 冻结方法")
    class FreezeTests {

        @Test
        @DisplayName("RELEASED -> FROZEN 成功")
        void freeze_released_success() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");

            baseline.freeze("admin");

            assertEquals(TaBaselineStatus.FROZEN, baseline.getStatus());
            assertEquals(3, baseline.getVersion());
            assertEquals("admin", baseline.getModifyBy());
        }

        @Test
        @DisplayName("DRAFT -> FROZEN 抛出 TaBaselineStatusInvalidException")
        void freeze_draft_throwsStatusInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();

            assertThrows(TaBaselineStatusInvalidException.class, () ->
                    baseline.freeze("admin"));
        }

        @Test
        @DisplayName("FROZEN -> FROZEN 抛出 TaBaselineStatusInvalidException")
        void freeze_frozen_throwsStatusInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");
            baseline.freeze("admin");

            assertThrows(TaBaselineStatusInvalidException.class, () ->
                    baseline.freeze("admin"));
        }
    }

    @Nested
    @DisplayName("recompute 重新计算方法")
    class RecomputeTests {

        @Test
        @DisplayName("RELEASED -> DRAFT 成功")
        void recompute_released_success() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");

            baseline.recompute("admin");

            assertEquals(TaBaselineStatus.DRAFT, baseline.getStatus());
            assertEquals(3, baseline.getVersion());
        }

        @Test
        @DisplayName("DRAFT -> DRAFT 抛出 TaBaselineStatusInvalidException")
        void recompute_draft_throwsStatusInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();

            assertThrows(TaBaselineStatusInvalidException.class, () ->
                    baseline.recompute("admin"));
        }
    }

    @Nested
    @DisplayName("ensureEditable 确保可编辑")
    class EnsureEditableTests {

        @Test
        @DisplayName("DRAFT 状态 - 不抛出异常")
        void ensureEditable_draft_success() {
            TypeApprovalBaseline baseline = createTestBaseline();

            assertDoesNotThrow(baseline::ensureEditable);
        }

        @Test
        @DisplayName("RELEASED 状态 - 不抛出异常")
        void ensureEditable_released_success() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");

            assertDoesNotThrow(baseline::ensureEditable);
        }

        @Test
        @DisplayName("FROZEN 状态 - 抛出 TaBaselineFrozenException")
        void ensureEditable_frozen_throwsFrozen() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");
            baseline.freeze("admin");

            assertThrows(TaBaselineFrozenException.class, baseline::ensureEditable);
        }
    }

    @Nested
    @DisplayName("addItem 添加基线项方法")
    class AddItemTests {

        @Test
        @DisplayName("DRAFT 状态添加基线项成功")
        void addItem_draft_success() {
            TypeApprovalBaseline baseline = createTestBaseline();
            TaBaselineItem item = createTestItem(baseline.getId());

            baseline.addItem(item);

            assertEquals(1, baseline.getItems().size());
        }

        @Test
        @DisplayName("RELEASED 状态添加基线项成功")
        void addItem_released_success() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");
            TaBaselineItem item = createTestItem(baseline.getId());

            assertDoesNotThrow(() -> baseline.addItem(item));
        }

        @Test
        @DisplayName("FROZEN 状态添加基线项抛出 TaBaselineFrozenException")
        void addItem_frozen_throwsFrozen() {
            TypeApprovalBaseline baseline = createTestBaseline();
            baseline.release("admin");
            baseline.freeze("admin");
            TaBaselineItem item = createTestItem(baseline.getId());

            assertThrows(TaBaselineFrozenException.class, () -> baseline.addItem(item));
        }
    }

    @Nested
    @DisplayName("getActiveItems 获取有效基线项")
    class GetActiveItemsTests {

        @Test
        @DisplayName("仅返回 rowValid=true 的基线项")
        void getActiveItems_filtersInvalid() {
            TypeApprovalBaseline baseline = createTestBaseline();
            TaBaselineItem item1 = createTestItem(baseline.getId());
            TaBaselineItem item2 = createTestItem(baseline.getId());
            item2.setRowValid(false);
            baseline.addItem(item1);
            baseline.addItem(item2);

            assertEquals(1, baseline.getActiveItems().size());
        }
    }

    private TypeApprovalBaseline createTestBaseline() {
        return TypeApprovalBaseline.create(
                "TAB000000000000000001",
                "SWIN001",
                AnchorType.VARIANT,
                "VAR001",
                "sha256:abc123",
                "SWB001",
                "admin"
        );
    }

    private TaBaselineItem createTestItem(Long taBaselineId) {
        return TaBaselineItem.create(
                taBaselineId,
                "TBOX",
                "PART001",
                "V1",
                "SWB001",
                "admin"
        );
    }
}
