package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SupplierHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SupplierRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.SupplierConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.SupplierHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SupplierHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SupplierMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SupplierPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 供应商仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierMapper supplierMapper;
    private final SupplierConverter supplierConverter;
    private final SupplierHistoryMapper supplierHistoryMapper;
    private final SupplierHistoryConverter supplierHistoryConverter;

    @Override
    public Supplier save(Supplier supplier, String operationType) {
        SupplierPo po = supplierConverter.toPo(supplier);
        if (po.getId() == null) {
            supplierMapper.insert(po);
        } else {
            supplierMapper.updateById(po);
        }
        if (operationType != null) {
            SupplierHistoryPo historyPo = SupplierHistoryPo.builder()
                    .entityId(po.getId())
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
                    .operationType(operationType)
                    .snapshotTime(new Date())
                    .operator(po.getModifyBy())
                    .createBy(po.getModifyBy())
                    .createTime(new Date())
                    .modifyBy(po.getModifyBy())
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            supplierHistoryMapper.insert(historyPo);
        }
        return supplierConverter.toDomain(po);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        SupplierPo po = supplierMapper.selectById(id);
        return Optional.ofNullable(supplierConverter.toDomain(po));
    }

    @Override
    public Optional<Supplier> findByCode(String code) {
        LambdaQueryWrapper<SupplierPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierPo::getCode, code);
        wrapper.eq(SupplierPo::getRowValid, true);
        SupplierPo po = supplierMapper.selectOne(wrapper);
        return Optional.ofNullable(supplierConverter.toDomain(po));
    }

    @Override
    public Optional<Supplier> findBySourceSystemAndSourceId(String sourceSystem, String sourceId) {
        if (sourceSystem == null || sourceId == null) {
            return Optional.empty();
        }
        LambdaQueryWrapper<SupplierPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierPo::getSourceSystem, sourceSystem);
        wrapper.eq(SupplierPo::getSourceId, sourceId);
        wrapper.eq(SupplierPo::getRowValid, true);
        wrapper.last("LIMIT 1");
        SupplierPo po = supplierMapper.selectOne(wrapper);
        return Optional.ofNullable(supplierConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<SupplierPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierPo::getCode, code);
        wrapper.eq(SupplierPo::getRowValid, true);
        return supplierMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Supplier> findAll(int page, int size, String supplierType, boolean includeInactive) {
        Page<SupplierPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SupplierPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierPo::getRowValid, true);
        if (supplierType != null && !supplierType.isBlank()) {
            wrapper.eq(SupplierPo::getSupplierType, supplierType);
        }
        if (!includeInactive) {
            wrapper.eq(SupplierPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(SupplierPo::getCreateTime);
        Page<SupplierPo> result = supplierMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(supplierConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive) {
        LambdaQueryWrapper<SupplierPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(SupplierPo::getStatus, "ACTIVE");
        }
        return supplierMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Supplier supplier) {
        SupplierPo po = supplierConverter.toPo(supplier);
        supplierMapper.updateById(po);
    }

    @Override
    public List<SupplierHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<SupplierHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SupplierHistoryPo::getCode, code);
        wrapper.eq(SupplierHistoryPo::getRowValid, true);
        wrapper.orderByDesc(SupplierHistoryPo::getVersion);
        List<SupplierHistoryPo> poList = supplierHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(supplierHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
