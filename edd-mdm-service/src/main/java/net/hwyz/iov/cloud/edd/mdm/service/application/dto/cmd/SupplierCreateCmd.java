package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 供应商创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreateCmd {

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
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 创建人
     */
    private String createBy;
}
