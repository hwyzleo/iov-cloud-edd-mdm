package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 供应商持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_supplier")
public class SupplierPo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;
    private String name;
    private String nameLocal;
    private String shortName;
    /** 业务分类，支持多选，逗号分隔（MATERIAL / COMPONENT / SERVICE / LOGISTICS / OTHER） */
    private String supplierType;
    private String country;
    private String businessLicenseNo;
    private String taxId;
    private String registeredAddress;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String bankName;
    private String bankAccount;
    private Date cooperationStartDate;
    private String description;

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
