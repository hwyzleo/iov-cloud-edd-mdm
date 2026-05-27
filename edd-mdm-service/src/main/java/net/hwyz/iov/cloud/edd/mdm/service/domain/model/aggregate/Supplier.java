package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SupplierStatus;

import java.util.Date;

/**
 * 供应商聚合根
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String shortName;
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
    private SupplierStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    public static Supplier create(String code, String name, String nameLocal, String shortName,
                                  String supplierType, String country, String businessLicenseNo,
                                  String taxId, String registeredAddress, String contactName,
                                  String contactPhone, String contactEmail, String bankName,
                                  String bankAccount, Date cooperationStartDate, String description,
                                  Date effectiveFrom, Date effectiveTo, String createBy) {
        return Supplier.builder()
                .code(code).name(name).nameLocal(nameLocal).shortName(shortName)
                .supplierType(supplierType).country(country).businessLicenseNo(businessLicenseNo)
                .taxId(taxId).registeredAddress(registeredAddress).contactName(contactName)
                .contactPhone(contactPhone).contactEmail(contactEmail).bankName(bankName)
                .bankAccount(bankAccount).cooperationStartDate(cooperationStartDate).description(description)
                .sourceSystem("LOCAL").sourceId(code).ingestionChannel("LOCAL").ingestionTime(new Date())
                .effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .version(1).status(SupplierStatus.ACTIVE)
                .createBy(createBy).createTime(new Date())
                .modifyBy(createBy).modifyTime(new Date())
                .rowVersion(0).rowValid(true)
                .build();
    }

    public void update(String name, String nameLocal, String shortName,
                       String supplierType, String country, String businessLicenseNo,
                       String taxId, String registeredAddress, String contactName,
                       String contactPhone, String contactEmail, String bankName,
                       String bankAccount, Date cooperationStartDate, String description,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.shortName = shortName;
        this.supplierType = supplierType;
        this.country = country;
        this.businessLicenseNo = businessLicenseNo;
        this.taxId = taxId;
        this.registeredAddress = registeredAddress;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.cooperationStartDate = cooperationStartDate;
        this.description = description;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void deactivate(String modifyBy) {
        if (this.status != SupplierStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的供应商才能失效");
        }
        this.status = SupplierStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void delete(String modifyBy) {
        if (this.status != SupplierStatus.DRAFT) {
            throw new IllegalStateException("只有DRAFT状态的供应商才能删除");
        }
        this.rowValid = false;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }
}
