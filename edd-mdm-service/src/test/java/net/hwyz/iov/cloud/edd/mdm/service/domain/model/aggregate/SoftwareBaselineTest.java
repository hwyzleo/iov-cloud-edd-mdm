package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineFrozenException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineStatusInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 软件基线聚合根单元测试
 *
 * @author hwyz_leo
 */
@DisplayName("SoftwareBaseline 聚合根测试")
class SoftwareBaselineTest {

    @Nested
    @DisplayName("create 创建方法")
    class CreateTests {

        @Test
        @DisplayName("创建成功 - 初始 DRAFT 状态、version=1")
        void create_success() {
            SoftwareBaseline baseline = SoftwareBaseline.create(
                    "XREHSLA26PA0000001-V1", "基线V1", AnchorType.CONFIGURATION,
                    "XREHSLA26PA0000001", "V1", "首版软件基线",
                    null, null, "admin"
            );

            assertEquals("XREHSLA26PA0000001-V1", baseline.getCode());
            assertEquals("基线V1", baseline.getName());
            assertEquals(AnchorType.CONFIGURATION, baseline.getAnchorType());
            assertEquals("XREHSLA26PA0000001", baseline.getAnchorCode());
            assertEquals("V1", baseline.getBaselineVersion());
            assertEquals(BaselineStatus.DRAFT, baseline.getBaselineStatus());
            assertEquals("首版软件基线", baseline.getDescription());
            assertEquals("LOCAL", baseline.getSourceSystem());
            assertEquals(1, baseline.getVersion());
            assertEquals("ACTIVE", baseline.getStatus());
            assertEquals("admin", baseline.getCreateBy());
            assertEquals("admin", baseline.getModifyBy());
            assertEquals(0, baseline.getRowVersion());
            assertTrue(baseline.getRowValid());
            assertNotNull(baseline.getItems());
            assertTrue(baseline.getItems().isEmpty());
        }

        @Test
        @DisplayName("创建失败 - effectiveFrom 晚于 effectiveTo")
        void create_effectiveFromAfterTo_throwsException() {
            Calendar cal = Calendar.getInstance();
            cal.set(2026, Calendar.DECEMBER, 1);
            Date from = cal.getTime();
            cal.set(2026, Calendar.JANUARY, 1);
            Date to = cal.getTime();

            assertThrows(IllegalArgumentException.class, () ->
                    SoftwareBaseline.create(
                            "CODE-V1", "基线", AnchorType.CONFIGURATION,
                            "CODE", "V1", null,
                            from, to, "admin"
                    ));
        }
    }

    @Nested
    @DisplayName("update 更新方法")
    class UpdateTests {

        @Test
        @DisplayName("DRAFT 状态更新成功 - version 自增")
        void update_draft_success() {
            SoftwareBaseline baseline = createTestBaseline();

            baseline.update("新名称", "新描述", null, null, "user1");

            assertEquals("新名称", baseline.getName());
            assertEquals("新描述", baseline.getDescription());
            assertEquals(2, baseline.getVersion());
            assertEquals("user1", baseline.getModifyBy());
        }

        @Test
        @DisplayName("RELEASED 状态更新抛出 SoftwareBaselineFrozenException")
        void update_released_throwsFrozen() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");

            assertThrows(SoftwareBaselineFrozenException.class, () ->
                    baseline.update("新名称", null, null, null, "user1"));
        }
    }

    @Nested
    @DisplayName("release 发布方法")
    class ReleaseTests {

        @Test
        @DisplayName("DRAFT -> RELEASED 成功 - 写入 releasedAt/releasedBy")
        void release_draft_success() {
            SoftwareBaseline baseline = createTestBaseline();

            baseline.release("admin");

            assertEquals(BaselineStatus.RELEASED, baseline.getBaselineStatus());
            assertNotNull(baseline.getReleasedAt());
            assertEquals("admin", baseline.getReleasedBy());
            assertEquals(2, baseline.getVersion());
        }

        @Test
        @DisplayName("RELEASED -> RELEASED 抛出 SoftwareBaselineStatusInvalidException")
        void release_released_throwsStatusInvalid() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");

            assertThrows(SoftwareBaselineStatusInvalidException.class, () ->
                    baseline.release("admin"));
        }
    }

    @Nested
    @DisplayName("supersede 取代方法")
    class SupersedeTests {

        @Test
        @DisplayName("RELEASED -> SUPERSEDED 成功 - 写入 supersededByCode")
        void supersede_released_success() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");

            baseline.supersede("CODE-V2", "admin");

            assertEquals(BaselineStatus.SUPERSEDED, baseline.getBaselineStatus());
            assertEquals("CODE-V2", baseline.getSupersededByCode());
        }

        @Test
        @DisplayName("DRAFT -> SUPERSEDED 抛出 SoftwareBaselineStatusInvalidException")
        void supersede_draft_throwsStatusInvalid() {
            SoftwareBaseline baseline = createTestBaseline();

            assertThrows(SoftwareBaselineStatusInvalidException.class, () ->
                    baseline.supersede("CODE-V2", "admin"));
        }
    }

    @Nested
    @DisplayName("bindItem 绑定基线项方法")
    class BindItemTests {

        @Test
        @DisplayName("DRAFT 状态绑定基线项成功 - version 自增")
        void bindItem_draft_success() {
            SoftwareBaseline baseline = createTestBaseline();
            SoftwareBaselineItem item = createTestItem(baseline.getCode(), "PART001");

            baseline.bindItem(item);

            assertEquals(1, baseline.getItems().size());
            assertEquals(2, baseline.getVersion());
        }

        @Test
        @DisplayName("RELEASED 状态绑定基线项抛出 SoftwareBaselineFrozenException")
        void bindItem_released_throwsFrozen() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");
            SoftwareBaselineItem item = createTestItem(baseline.getCode(), "PART001");

            assertThrows(SoftwareBaselineFrozenException.class, () ->
                    baseline.bindItem(item));
        }

        @Test
        @DisplayName("重复绑定同一 partCode 抛出 IllegalStateException")
        void bindItem_duplicate_throwsException() {
            SoftwareBaseline baseline = createTestBaseline();
            SoftwareBaselineItem item = createTestItem(baseline.getCode(), "PART001");
            baseline.bindItem(item);

            SoftwareBaselineItem dup = createTestItem(baseline.getCode(), "PART001");

            assertThrows(IllegalStateException.class, () -> baseline.bindItem(dup));
        }
    }

    @Nested
    @DisplayName("unbindItem 解绑基线项方法")
    class UnbindItemTests {

        @Test
        @DisplayName("DRAFT 状态解绑基线项成功 - rowValid 置 false")
        void unbindItem_draft_success() {
            SoftwareBaseline baseline = createTestBaseline();
            SoftwareBaselineItem item = createTestItem(baseline.getCode(), "PART001");
            baseline.bindItem(item);

            baseline.unbindItem("PART001", "admin");

            assertFalse(item.getRowValid());
            assertEquals(3, baseline.getVersion());
        }

        @Test
        @DisplayName("RELEASED 状态解绑基线项抛出 SoftwareBaselineFrozenException")
        void unbindItem_released_throwsFrozen() {
            SoftwareBaseline baseline = createTestBaseline();
            baseline.release("admin");

            assertThrows(SoftwareBaselineFrozenException.class, () ->
                    baseline.unbindItem("PART001", "admin"));
        }

        @Test
        @DisplayName("解绑不存在的 partCode 抛出 IllegalStateException")
        void unbindItem_notFound_throwsException() {
            SoftwareBaseline baseline = createTestBaseline();

            assertThrows(IllegalStateException.class, () ->
                    baseline.unbindItem("NOT_EXIST", "admin"));
        }
    }

    @Nested
    @DisplayName("getActiveItems 获取有效基线项")
    class GetActiveItemsTests {

        @Test
        @DisplayName("仅返回 rowValid=true 的基线项")
        void getActiveItems_filtersInvalid() {
            SoftwareBaseline baseline = createTestBaseline();
            SoftwareBaselineItem item1 = createTestItem(baseline.getCode(), "PART001");
            SoftwareBaselineItem item2 = createTestItem(baseline.getCode(), "PART002");
            baseline.bindItem(item1);
            baseline.bindItem(item2);
            baseline.unbindItem("PART001", "admin");

            assertEquals(1, baseline.getActiveItems().size());
            assertEquals("PART002", baseline.getActiveItems().get(0).getPartCode());
        }
    }

    private SoftwareBaseline createTestBaseline() {
        return SoftwareBaseline.create(
                "XREHSLA26PA0000001-V1", "基线V1", AnchorType.CONFIGURATION,
                "XREHSLA26PA0000001", "V1", "测试基线",
                null, null, "admin"
        );
    }

    private SoftwareBaselineItem createTestItem(String baselineCode, String partCode) {
        return SoftwareBaselineItem.create(
                baselineCode, partCode,
                "TBOX", null,
                "admin"
        );
    }
}
