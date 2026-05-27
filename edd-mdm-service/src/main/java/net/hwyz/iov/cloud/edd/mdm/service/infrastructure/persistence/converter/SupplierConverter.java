package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SupplierStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierPo;
import org.springframework.stereotype.Component;

/**
 * 供应商转换器
 *
 * @author hwyz_leo
 */
@Component
public class SupplierConverter {

    public Supplier toDomain(SupplierPo po) {
        if (po == null) {
            return null;
        }
        return Supplier.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .shortName(po.getShortName())
                .supplierType(po.getSupplierType())
                .country(po.getCountry())
                .businessLicenseNo(po.getBusinessLicenseNo())
                .taxId(po.getTaxId())
                .registeredAddress(po.getRegisteredAddress())
                .contactName(po.getContactName())
                .contactPhone(po.getContactPhone())
                .contactEmail(po.getContactEmail())
                .bankName(po.getBankName())
                .bankAccount(po.getBankAccount())
                .cooperationStartDate(po.getCooperationStartDate())
                .description(po.getDescription())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(SupplierStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public SupplierPo toPo(Supplier domain) {
        if (domain == null) {
            return null;
        }
        return SupplierPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .shortName(domain.getShortName())
                .supplierType(domain.getSupplierType())
                .country(domain.getCountry())
                .businessLicenseNo(domain.getBusinessLicenseNo())
                .taxId(domain.getTaxId())
                .registeredAddress(domain.getRegisteredAddress())
                .contactName(domain.getContactName())
                .contactPhone(domain.getContactPhone())
                .contactEmail(domain.getContactEmail())
                .bankName(domain.getBankName())
                .bankAccount(domain.getBankAccount())
                .cooperationStartDate(domain.getCooperationStartDate())
                .description(domain.getDescription())
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }
}
