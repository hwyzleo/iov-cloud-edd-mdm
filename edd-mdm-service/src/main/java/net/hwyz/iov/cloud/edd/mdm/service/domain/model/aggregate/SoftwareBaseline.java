package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineFrozenException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineStatusInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 软件基线聚合根（Material 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaseline {

    private Long id;
    private String code;
    private String name;
    private AnchorType anchorType;
    private String anchorCode;
    private String baselineVersion;
    private BaselineStatus baselineStatus;
    private Date releasedAt;
    private String releasedBy;
    private String supersededByCode;
    private String description;

    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;

    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    @Builder.Default
    private List<SoftwareBaselineItem> items = new ArrayList<>();

    public static SoftwareBaseline create(String code, String name, AnchorType anchorType, String anchorCode,
                                           String baselineVersion, String description,
                                           Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        Date now = new Date();
        return SoftwareBaseline.builder()
                .code(code)
                .name(name)
                .anchorType(anchorType)
                .anchorCode(anchorCode)
                .baselineVersion(baselineVersion)
                .baselineStatus(BaselineStatus.DRAFT)
                .description(description)
                .sourceSystem("LOCAL")
                .sourceId(code)
                .ingestionChannel("LOCAL")
                .ingestionTime(now)
                .version(1)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .status("ACTIVE")
                .createBy(createBy)
                .createTime(now)
                .modifyBy(createBy)
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .items(new ArrayList<>())
                .build();
    }

    public void update(String name, String description, Date effectiveFrom, Date effectiveTo, String modifyBy) {
        ensureEditable();
        validateEffectiveDate(effectiveFrom, effectiveTo);
        this.name = name;
        this.description = description;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void release(String releasedBy) {
        if (this.baselineStatus != BaselineStatus.DRAFT) {
            throw new SoftwareBaselineStatusInvalidException(this.baselineStatus.name(), BaselineStatus.RELEASED.name());
        }
        this.baselineStatus = BaselineStatus.RELEASED;
        this.releasedAt = new Date();
        this.releasedBy = releasedBy;
        this.version = this.version + 1;
        this.modifyBy = releasedBy;
        this.modifyTime = new Date();
    }

    public void supersede(String supersededByCode, String operator) {
        if (this.baselineStatus != BaselineStatus.RELEASED) {
            throw new SoftwareBaselineStatusInvalidException(this.baselineStatus.name(), BaselineStatus.SUPERSEDED.name());
        }
        this.baselineStatus = BaselineStatus.SUPERSEDED;
        this.supersededByCode = supersededByCode;
        this.version = this.version + 1;
        this.modifyBy = operator;
        this.modifyTime = new Date();
    }

    public void bindItem(SoftwareBaselineItem item) {
        ensureEditable();
        boolean exists = items.stream()
                .anyMatch(i -> i.getPartCode().equals(item.getPartCode()) && i.getRowValid());
        if (exists) {
            throw new IllegalStateException("基线项零件已存在: " + item.getPartCode());
        }
        items.add(item);
        this.version = this.version + 1;
        this.modifyBy = item.getModifyBy();
        this.modifyTime = new Date();
    }

    public void unbindItem(String partCode, String operator) {
        ensureEditable();
        items.stream()
                .filter(i -> i.getPartCode().equals(partCode) && i.getRowValid())
                .findFirst()
                .ifPresentOrElse(
                        item -> {
                            item.setRowValid(false);
                            item.setModifyBy(operator);
                            item.setModifyTime(new Date());
                            this.version = this.version + 1;
                            this.modifyBy = operator;
                            this.modifyTime = new Date();
                        },
                        () -> {
                            throw new IllegalStateException("基线项零件不存在: " + partCode);
                        }
                );
    }

    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public List<SoftwareBaselineItem> getActiveItems() {
        return items.stream()
                .filter(i -> i.getRowValid() != null && i.getRowValid())
                .toList();
    }

    private void ensureEditable() {
        if (this.baselineStatus != BaselineStatus.DRAFT) {
            throw new SoftwareBaselineFrozenException(this.code);
        }
    }

    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
