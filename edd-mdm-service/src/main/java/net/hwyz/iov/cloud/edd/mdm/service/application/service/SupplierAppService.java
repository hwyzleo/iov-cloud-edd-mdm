package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SupplierQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SupplierHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierRepository;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 供应商应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierAppService {

    private final SupplierRepository supplierRepository;
    private final OutboxService outboxService;

    /**
     * 创建供应商
     *
     * @param cmd 创建命令
     * @return 供应商DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SupplierDto createSupplier(SupplierCreateCmd cmd) {
        log.info("创建供应商: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        Supplier supplier = Supplier.create(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getShortName(),
                cmd.getSupplierType(), cmd.getCountry(), cmd.getBusinessLicenseNo(),
                cmd.getTaxId(), cmd.getRegisteredAddress(), cmd.getContactName(),
                cmd.getContactPhone(), cmd.getContactEmail(), cmd.getBankName(),
                cmd.getBankAccount(), cmd.getCooperationStartDate(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );

        supplier = supplierRepository.save(supplier, "CREATE");

        outboxService.publishSupplierCreatedEvent(supplier);

        return convertToDto(supplier);
    }

    /**
     * 更新供应商
     *
     * @param cmd 更新命令
     * @return 供应商DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SupplierDto updateSupplier(SupplierUpdateCmd cmd) {
        log.info("更新供应商: {}", cmd.getCode());

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Supplier supplier = supplierRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在: " + cmd.getCode()));

        supplier.update(
                cmd.getName(), cmd.getNameLocal(), cmd.getShortName(),
                cmd.getSupplierType(), cmd.getCountry(), cmd.getBusinessLicenseNo(),
                cmd.getTaxId(), cmd.getRegisteredAddress(), cmd.getContactName(),
                cmd.getContactPhone(), cmd.getContactEmail(), cmd.getBankName(),
                cmd.getBankAccount(), cmd.getCooperationStartDate(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy
        );

        supplier = supplierRepository.save(supplier, "UPDATE");

        outboxService.publishSupplierUpdatedEvent(supplier);

        return convertToDto(supplier);
    }

    /**
     * 失效供应商
     *
     * @param code     供应商code
     * @param modifyBy 修改人
     * @return 供应商DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SupplierDto deactivateSupplier(String code, String modifyBy) {
        log.info("失效供应商: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Supplier supplier = supplierRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在: " + code));

        supplier.deactivate(modifyBy);

        supplier = supplierRepository.save(supplier, "DEACTIVATE");

        outboxService.publishSupplierDeactivatedEvent(supplier);

        return convertToDto(supplier);
    }

    /**
     * 删除供应商
     *
     * @param code     供应商code
     * @param modifyBy 修改人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSupplier(String code, String modifyBy) {
        log.info("删除供应商: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Supplier supplier = supplierRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在: " + code));

        supplier.delete(modifyBy);

        supplierRepository.delete(supplier);
    }

    /**
     * 根据code获取供应商
     *
     * @param code 供应商code
     * @return 供应商DTO
     */
    public SupplierDto getSupplierByCode(String code) {
        Supplier supplier = supplierRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在: " + code));
        return convertToDto(supplier);
    }

    /**
     * 分页查询供应商列表
     *
     * @param query 查询条件
     * @return 供应商DTO列表
     */
    public List<SupplierDto> listSuppliers(SupplierQuery query) {
        List<Supplier> suppliers = supplierRepository.findAll(
                query.getPage(), query.getSize(), query.getSupplierType(), query.isIncludeInactive()
        );
        return suppliers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询供应商总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countSuppliers(boolean includeInactive) {
        return supplierRepository.count(includeInactive);
    }

    /**
     * 查询供应商历史版本列表
     *
     * @param code 供应商code
     * @return 历史版本DTO列表
     */
    public List<SupplierHistoryDto> listSupplierHistory(String code) {
        List<SupplierHistory> historyList = supplierRepository.findHistoryByCode(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     *
     * @param supplier 供应商聚合根
     * @return 供应商DTO
     */
    private SupplierDto convertToDto(Supplier supplier) {
        return SupplierDto.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .nameLocal(supplier.getNameLocal())
                .shortName(supplier.getShortName())
                .supplierType(supplier.getSupplierType())
                .country(supplier.getCountry())
                .businessLicenseNo(supplier.getBusinessLicenseNo())
                .taxId(supplier.getTaxId())
                .registeredAddress(supplier.getRegisteredAddress())
                .contactName(supplier.getContactName())
                .contactPhone(supplier.getContactPhone())
                .contactEmail(supplier.getContactEmail())
                .bankName(supplier.getBankName())
                .bankAccount(supplier.getBankAccount())
                .cooperationStartDate(supplier.getCooperationStartDate())
                .description(supplier.getDescription())
                .sourceSystem(supplier.getSourceSystem())
                .sourceId(supplier.getSourceId())
                .sourceVersion(supplier.getSourceVersion())
                .ingestionChannel(supplier.getIngestionChannel())
                .ingestionTime(supplier.getIngestionTime())
                .sourcePayloadHash(supplier.getSourcePayloadHash())
                .version(supplier.getVersion())
                .effectiveFrom(supplier.getEffectiveFrom())
                .effectiveTo(supplier.getEffectiveTo())
                .status(supplier.getStatus().name())
                .createBy(supplier.getCreateBy())
                .createTime(supplier.getCreateTime())
                .modifyBy(supplier.getModifyBy())
                .modifyTime(supplier.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 供应商历史版本实体
     * @return 供应商历史版本DTO
     */
    private SupplierHistoryDto convertHistoryToDto(SupplierHistory history) {
        return SupplierHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .shortName(history.getShortName())
                .supplierType(history.getSupplierType())
                .country(history.getCountry())
                .businessLicenseNo(history.getBusinessLicenseNo())
                .taxId(history.getTaxId())
                .registeredAddress(history.getRegisteredAddress())
                .contactName(history.getContactName())
                .contactPhone(history.getContactPhone())
                .contactEmail(history.getContactEmail())
                .bankName(history.getBankName())
                .bankAccount(history.getBankAccount())
                .cooperationStartDate(history.getCooperationStartDate())
                .description(history.getDescription())
                .sourceSystem(history.getSourceSystem())
                .sourceId(history.getSourceId())
                .sourceVersion(history.getSourceVersion())
                .ingestionChannel(history.getIngestionChannel())
                .ingestionTime(history.getIngestionTime())
                .sourcePayloadHash(history.getSourcePayloadHash())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus())
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .createBy(history.getCreateBy())
                .createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy())
                .modifyTime(history.getModifyTime())
                .build();
    }
}
