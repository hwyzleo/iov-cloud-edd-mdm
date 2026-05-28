package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工厂持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_org_plant")
public class PlantPo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;
    private String name;
    private String nameEn;
    private String shortName;
    private String description;
    private String plantType;
    private String legalEntityCode;
    private String costCenterCode;
    private String country;
    private String province;
    private String city;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
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
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
