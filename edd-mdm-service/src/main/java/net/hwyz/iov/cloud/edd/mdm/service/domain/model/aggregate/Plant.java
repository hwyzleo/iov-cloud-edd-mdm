package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;

import java.util.Date;

/**
 * 工厂聚合根（Org 子域）
 * <p>
 * 同步来源字段采用 Brand/Party 模式（source_system / source_id / source_version / ingestion_channel / ingestion_time / source_payload_hash）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Plant {

    private Long id;

    // === 身份属性 ===

    private String code;
    private String name;
    private String nameEn;
    private String shortName;
    private String description;

    // === 分类属性 ===

    private PlantType plantType;
    private String legalEntityCode;
    private String costCenterCode;

    // === 位置属性 ===

    private String country;
    private String province;
    private String city;
    private String address;
    private java.math.BigDecimal longitude;
    private java.math.BigDecimal latitude;
    private String timezone;

    // === 运营属性 ===

    private Long annualCapacity;
    private Integer productionLines;
    private Date operationalStartDate;
    private String mesInstance;

    // === MDM 同步与治理字段（Brand/Party 模式） ===

    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;

    // === 通用治理字段 ===

    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private PlantStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 创建工厂
     * <p>
     * 默认 status=ACTIVE、source_system=LOCAL、ingestion_channel=LOCAL
     */
    public static Plant create(String code, String name, String nameEn, String shortName, String description,
                               PlantType plantType, String legalEntityCode, String costCenterCode,
                               String country, String province, String city, String address,
                               java.math.BigDecimal longitude, java.math.BigDecimal latitude, String timezone,
                               Long annualCapacity, Integer productionLines, Date operationalStartDate, String mesInstance,
                               Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        Date now = new Date();
        return Plant.builder()
                .code(code).name(name).nameEn(nameEn).shortName(shortName).description(description)
                .plantType(plantType).legalEntityCode(legalEntityCode).costCenterCode(costCenterCode)
                .country(country).province(province).city(city).address(address)
                .longitude(longitude).latitude(latitude).timezone(timezone)
                .annualCapacity(annualCapacity).productionLines(productionLines)
                .operationalStartDate(operationalStartDate).mesInstance(mesInstance)
                .sourceSystem("LOCAL").sourceId(code).ingestionChannel("LOCAL").ingestionTime(now)
                .version(1).effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .status(PlantStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }

    /**
     * 更新工厂（code 不可变）
     */
    public void update(String name, String nameEn, String shortName, String description,
                       PlantType plantType, String legalEntityCode, String costCenterCode,
                       String country, String province, String city, String address,
                       java.math.BigDecimal longitude, java.math.BigDecimal latitude, String timezone,
                       Long annualCapacity, Integer productionLines, Date operationalStartDate, String mesInstance,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        this.name = name;
        this.nameEn = nameEn;
        this.shortName = shortName;
        this.description = description;
        this.plantType = plantType;
        this.legalEntityCode = legalEntityCode;
        this.costCenterCode = costCenterCode;
        this.country = country;
        this.province = province;
        this.city = city;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.timezone = timezone;
        this.annualCapacity = annualCapacity;
        this.productionLines = productionLines;
        this.operationalStartDate = operationalStartDate;
        this.mesInstance = mesInstance;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 失效工厂（status=ACTIVE → INACTIVE）
     */
    public void deactivate(String modifyBy) {
        if (this.status != PlantStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的工厂才能失效");
        }
        this.status = PlantStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 标记审计字段（用于物理删除前持久化最后一份审计记录）
     */
    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 校验生效期（effectiveFrom <= effectiveTo）
     */
    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
