package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工厂历史版本响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantHistoryResponse {

    /**
     * 快照ID
     */
    private Long snapshotId;

    /**
     * 关联主表id
     */
    private Long entityId;

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
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 来源系统ID
     */
    private String sourceId;

    /**
     * 来源版本
     */
    private String sourceVersion;

    /**
     * 数据接入渠道
     */
    private String ingestionChannel;

    /**
     * 数据接入时间
     */
    private Date ingestionTime;

    /**
     * 来源数据哈希
     */
    private String sourcePayloadHash;

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
    private String status;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 快照时间
     */
    private Date snapshotTime;

    /**
     * 操作人
     */
    private String operator;

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
}
