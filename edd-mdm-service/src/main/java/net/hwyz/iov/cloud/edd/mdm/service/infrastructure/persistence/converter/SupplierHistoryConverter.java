package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SupplierHistory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 供应商历史版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class SupplierHistoryConverter {

    public SupplierHistory toDomain(SupplierHistoryPo po) {
        if (po == null) {
            return null;
        }
        return SupplierHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
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
                .status(po.getStatus())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .build();
    }
}
