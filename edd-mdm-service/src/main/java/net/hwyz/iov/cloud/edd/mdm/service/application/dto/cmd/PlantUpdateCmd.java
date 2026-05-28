package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工厂更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantUpdateCmd {

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 工厂名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String nameEn;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 描述
     */
    private String description;

    /**
     * 工厂类型
     */
    private String plantType;

    /**
     * 法人实体编码
     */
    private String legalEntityCode;

    /**
     * 成本中心编码
     */
    private String costCenterCode;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 年产能
     */
    private Long annualCapacity;

    /**
     * 生产线数量
     */
    private Integer productionLines;

    /**
     * 运营开始日期
     */
    private Date operationalStartDate;

    /**
     * MES实例
     */
    private String mesInstance;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 修改人
     */
    private String modifyBy;
}
