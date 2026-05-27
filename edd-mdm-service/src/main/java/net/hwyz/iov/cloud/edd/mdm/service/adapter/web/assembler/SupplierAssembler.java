package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 供应商Assembler
 *
 * @author hwyz_leo
 */
@Component
public class SupplierAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 供应商DTO
     * @return 供应商响应对象
     */
    public SupplierResponse toResponse(SupplierDto dto) {
        if (dto == null) {
            return null;
        }
        return SupplierResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .shortName(dto.getShortName())
                .supplierType(dto.getSupplierType())
                .country(dto.getCountry())
                .businessLicenseNo(dto.getBusinessLicenseNo())
                .taxId(dto.getTaxId())
                .registeredAddress(dto.getRegisteredAddress())
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .bankName(dto.getBankName())
                .bankAccount(dto.getBankAccount())
                .cooperationStartDate(dto.getCooperationStartDate())
                .description(dto.getDescription())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }

    /**
     * 历史版本DTO转换为响应对象
     *
     * @param dto 供应商历史版本DTO
     * @return 供应商历史版本响应对象
     */
    public SupplierHistoryResponse toHistoryResponse(SupplierHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return SupplierHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .shortName(dto.getShortName())
                .supplierType(dto.getSupplierType())
                .country(dto.getCountry())
                .businessLicenseNo(dto.getBusinessLicenseNo())
                .taxId(dto.getTaxId())
                .registeredAddress(dto.getRegisteredAddress())
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .bankName(dto.getBankName())
                .bankAccount(dto.getBankAccount())
                .cooperationStartDate(dto.getCooperationStartDate())
                .description(dto.getDescription())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .operationType(dto.getOperationType())
                .snapshotTime(dto.getSnapshotTime())
                .operator(dto.getOperator())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }
}
