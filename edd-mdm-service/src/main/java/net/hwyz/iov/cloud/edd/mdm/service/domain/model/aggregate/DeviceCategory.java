package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;

import java.util.Date;

/**
 * 设备类别聚合根（EEAD 子域）
 * <p>
 * 扁平字典，无 parent_code，不与 nodeType 关联。
 * 比 nodeType 更细、需业务侧动态维护的子分类维度。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategory {

    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private Integer sortOrder;
    private String source;
    private String externalRefId;
    private Long externalVersion;
    private Date lastSyncTime;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private DeviceCategoryStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    public static DeviceCategory create(String code, String name, String nameLocal, String description,
                                         Integer sortOrder, Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        Date now = new Date();
        return DeviceCategory.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .source("MANUAL").externalRefId(null).externalVersion(null).lastSyncTime(now)
                .version(1).effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .status(DeviceCategoryStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }

    public void update(String name, String nameLocal, String description, Integer sortOrder,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void deactivate(String modifyBy) {
        if (this.status != DeviceCategoryStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的设备类别才能失效");
        }
        this.status = DeviceCategoryStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
