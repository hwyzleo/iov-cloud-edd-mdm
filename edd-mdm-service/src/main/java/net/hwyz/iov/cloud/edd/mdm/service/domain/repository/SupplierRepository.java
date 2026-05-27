package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SupplierHistory;

import java.util.List;
import java.util.Optional;

/**
 * 供应商仓储接口
 *
 * @author hwyz_leo
 */
public interface SupplierRepository {

    Supplier save(Supplier supplier, String operationType);

    Optional<Supplier> findById(Long id);

    Optional<Supplier> findByCode(String code);

    Optional<Supplier> findBySourceSystemAndSourceId(String sourceSystem, String sourceId);

    boolean existsByCode(String code);

    List<Supplier> findAll(int page, int size, String supplierType, boolean includeInactive);

    long count(boolean includeInactive);

    void delete(Supplier supplier);

    List<SupplierHistory> findHistoryByCode(String code);
}
