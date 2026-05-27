package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 供应商DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDto {

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
     * 简称
     */
    private String shortName;

    /**
     * 供应商类型
     */
    private String supplierType;

    /**
     * 国家
     */
    private String country;

    /**
     * 营业执照号
     */
    private String businessLicenseNo;

    /**
     * 税务ID
     */
    private String taxId;

    /**
     * 注册地址
     */
    private String registeredAddress;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 合作开始日期
     */
    private Date cooperationStartDate;

    /**
     * 描述
     */
    private String description;

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
