package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineFrozenException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineStatusInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TaBaselineStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 型式批准基线聚合根（EEAD 子域）
 * <p>
 * 把型式级型批基准从 OTA 活动级快照与 SwinManagedSystem 手工松引用上收为 MDM 主数据。
 * TA 基线由锚点范围内 RELEASED SoftwareBaseline 经型批相关过滤 + node↔part 归一卷积而成。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeApprovalBaseline {

    private Long id;
    private String taBaselineCode;
    private String swinCode;
    private AnchorType anchorType;
    private String anchorCode;
    private TaBaselineStatus status;
    private String projectionDigest;
    private String sourceBaselineScope;
    private Date effectiveFrom;
    private String remark;
    private Integer version;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    @Builder.Default
    private List<TaBaselineItem> items = new ArrayList<>();

    /**
     * 创建新的型式批准基线
     *
     * @param taBaselineCode     业务键
     * @param swinCode           SWIN 定义代码
     * @param anchorType         锚定层级类型
     * @param anchorCode         锚点编码
     * @param projectionDigest   投影摘要
     * @param sourceBaselineScope 参与卷积的 SoftwareBaseline code 列表
     * @param createBy           创建人
     * @return 新创建的 TypeApprovalBaseline 实例
     */
    public static TypeApprovalBaseline create(String taBaselineCode, String swinCode,
                                               AnchorType anchorType, String anchorCode,
                                               String projectionDigest, String sourceBaselineScope,
                                               String createBy) {
        Date now = new Date();
        return TypeApprovalBaseline.builder()
                .taBaselineCode(taBaselineCode)
                .swinCode(swinCode)
                .anchorType(anchorType)
                .anchorCode(anchorCode)
                .status(TaBaselineStatus.DRAFT)
                .projectionDigest(projectionDigest)
                .sourceBaselineScope(sourceBaselineScope)
                .version(1)
                .createBy(createBy)
                .createTime(now)
                .modifyBy(createBy)
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .items(new ArrayList<>())
                .build();
    }

    /**
     * 发布基线（DRAFT -> RELEASED）
     *
     * @param releasedBy 发布人
     */
    public void release(String releasedBy) {
        if (this.status != TaBaselineStatus.DRAFT) {
            throw new TaBaselineStatusInvalidException(this.taBaselineCode, this.status.name(), TaBaselineStatus.RELEASED.name());
        }
        this.status = TaBaselineStatus.RELEASED;
        this.effectiveFrom = new Date();
        this.version = this.version + 1;
        this.modifyBy = releasedBy;
        this.modifyTime = new Date();
    }

    /**
     * 冻结基线（RELEASED -> FROZEN）
     *
     * @param frozenBy 冻结人
     */
    public void freeze(String frozenBy) {
        if (this.status != TaBaselineStatus.RELEASED) {
            throw new TaBaselineStatusInvalidException(this.taBaselineCode, this.status.name(), TaBaselineStatus.FROZEN.name());
        }
        this.status = TaBaselineStatus.FROZEN;
        this.version = this.version + 1;
        this.modifyBy = frozenBy;
        this.modifyTime = new Date();
    }

    /**
     * 重新计算（RELEASED -> DRAFT）
     *
     * @param operator 操作人
     */
    public void recompute(String operator) {
        if (this.status != TaBaselineStatus.RELEASED) {
            throw new TaBaselineStatusInvalidException(this.taBaselineCode, this.status.name(), TaBaselineStatus.DRAFT.name());
        }
        this.status = TaBaselineStatus.DRAFT;
        this.version = this.version + 1;
        this.modifyBy = operator;
        this.modifyTime = new Date();
    }

    /**
     * 确保基线可编辑（非 FROZEN 状态）
     */
    public void ensureEditable() {
        if (this.status == TaBaselineStatus.FROZEN) {
            throw new TaBaselineFrozenException(this.taBaselineCode);
        }
    }

    /**
     * 添加基线项
     *
     * @param item 基线项
     */
    public void addItem(TaBaselineItem item) {
        ensureEditable();
        items.add(item);
    }

    /**
     * 获取活跃的基线项列表
     *
     * @return 活跃的基线项列表
     */
    public List<TaBaselineItem> getActiveItems() {
        return items.stream()
                .filter(i -> i.getRowValid() != null && i.getRowValid())
                .toList();
    }

    /**
     * 标记为删除中
     *
     * @param modifyBy 操作人
     */
    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }
}
