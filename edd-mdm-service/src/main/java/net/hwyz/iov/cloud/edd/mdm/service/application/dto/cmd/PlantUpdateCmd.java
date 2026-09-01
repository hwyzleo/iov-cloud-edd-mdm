package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
     * 英文标准名称
     */
    @NotBlank(message = "工厂英文标准名称不能为空")
    @Size(max = 128, message = "工厂英文标准名称不能超过128字符")
    private String name;

    /**
     * 本地化名称
     */
    @Size(max = 128, message = "工厂本地化名称不能超过128字符")
    private String nameLocal;

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
