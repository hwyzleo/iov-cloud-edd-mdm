package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;

import java.util.Date;

/**
 * SWIN编码方案聚合根（EEAD 子域）
 * <p>
 * 定义SWIN编码的路由类型和规则。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinScheme {

    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private SwinRoute route;
    private Integer sortOrder;
    private String source;
    private String externalRefId;
    private Long externalVersion;
    private Date lastSyncTime;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private SwinSchemeStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 创建新的SWIN编码方案
     *
     * @param code          业务主键
     * @param name          名称
     * @param nameLocal     本地化名称
     * @param description   描述
     * @param route         路由类型
     * @param sortOrder     排序序号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy      创建人
     * @return 新创建的SwinScheme实例
     */
    public static SwinScheme create(String code, String name, String nameLocal, String description,
                                     SwinRoute route, Integer sortOrder, Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        if (route == null) {
            throw new IllegalArgumentException("路由类型不能为空");
        }
        Date now = new Date();
        return SwinScheme.builder()
                .code(code).name(name).nameLocal(nameLocal).description(description)
                .route(route)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .source("MANUAL").externalRefId(null).externalVersion(null).lastSyncTime(now)
                .version(1).effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .status(SwinSchemeStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }

    /**
     * 更新SWIN编码方案
     *
     * @param name          名称
     * @param nameLocal     本地化名称
     * @param description   描述
     * @param route         路由类型
     * @param sortOrder     排序序号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy      修改人
     */
    public void update(String name, String nameLocal, String description, SwinRoute route,
                       Integer sortOrder, Date effectiveFrom, Date effectiveTo, String modifyBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        if (route == null) {
            throw new IllegalArgumentException("路由类型不能为空");
        }
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.route = route;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 使SWIN编码方案失效
     *
     * @param modifyBy 修改人
     */
    public void deactivate(String modifyBy) {
        if (this.status != SwinSchemeStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的编码方案才能失效");
        }
        this.status = SwinSchemeStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 标记为待删除
     *
     * @param modifyBy 修改人
     */
    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 验证生效日期
     *
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     */
    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
