package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;

import java.util.Date;

/**
 * 工厂历史快照实体
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantHistory {

    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    private String code;
    private String name;
    private String nameEn;
    private String shortName;
    private String description;
    private PlantType plantType;
    private String legalEntityCode;
    private String costCenterCode;
    private String country;
    private String province;
    private String city;
    private String address;
    private java.math.BigDecimal longitude;
    private java.math.BigDecimal latitude;
    private String timezone;
    private Long annualCapacity;
    private Integer productionLines;
    private Date operationalStartDate;
    private String mesInstance;

    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;

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
}
