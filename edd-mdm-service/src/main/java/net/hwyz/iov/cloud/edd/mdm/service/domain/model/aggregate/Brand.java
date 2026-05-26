package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.InvalidEffectiveDateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BrandStatus;

import java.util.Date;

/**
 * 品牌聚合根
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 官方名称
     */
    private String name;

    /**
     * 本地化名称
     */
    private String nameLocal;

    /**
     * 品牌描述
     */
    private String description;

    /**
     * Logo URL
     */
    private String logo;

    /**
     * 国家
     */
    private String country;

    /**
     * 创立年份
     */
    private Integer foundedYear;

    /**
     * 业务版本号
     */
    private Integer version;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 状态
     */
    private BrandStatus status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 乐观锁版本号
     */
    private Integer rowVersion;

    /**
     * 行有效标记
     */
    private Boolean rowValid;

    /**
     * 校验生效期合法性
     *
     * @throws InvalidEffectiveDateException 生效期无效异常
     */
    public void validateEffectiveDate() throws InvalidEffectiveDateException {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new InvalidEffectiveDateException("生效开始时间不能晚于生效结束时间");
        }
    }

    /**
     * 创建品牌
     *
     * @param code        业务主键
     * @param name        官方名称
     * @param nameLocal   本地化名称
     * @param description 品牌描述
     * @param logo        Logo URL
     * @param country     国家
     * @param foundedYear 创立年份
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy    创建人
     * @return 品牌聚合根
     */
    public static Brand create(String code, String name, String nameLocal, String description,
                               String logo, String country, Integer foundedYear,
                               Date effectiveFrom, Date effectiveTo, String createBy) {
        Brand brand = Brand.builder()
                .code(code)
                .name(name)
                .nameLocal(nameLocal)
                .description(description)
                .logo(logo)
                .country(country)
                .foundedYear(foundedYear)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .version(1)
                .status(BrandStatus.ACTIVE)
                .createBy(createBy)
                .createTime(new Date())
                .modifyBy(createBy)
                .modifyTime(new Date())
                .rowVersion(0)
                .rowValid(true)
                .build();

        brand.validateEffectiveDate();
        return brand;
    }

    /**
     * 更新品牌
     *
     * @param name        官方名称
     * @param nameLocal   本地化名称
     * @param description 品牌描述
     * @param logo        Logo URL
     * @param country     国家
     * @param foundedYear 创立年份
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy    修改人
     */
    public void update(String name, String nameLocal, String description,
                       String logo, String country, Integer foundedYear,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.logo = logo;
        this.country = country;
        this.foundedYear = foundedYear;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();

        validateEffectiveDate();
    }

    /**
     * 失效品牌
     *
     * @param modifyBy 修改人
     */
    public void deactivate(String modifyBy) {
        if (this.status != BrandStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的品牌才能失效");
        }
        this.status = BrandStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 删除品牌（仅DRAFT状态可删除）
     *
     * @param modifyBy 修改人
     */
    public void delete(String modifyBy) {
        if (this.status != BrandStatus.DRAFT) {
            throw new IllegalStateException("只有DRAFT状态的品牌才能删除");
        }
        this.rowValid = false;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }
}
