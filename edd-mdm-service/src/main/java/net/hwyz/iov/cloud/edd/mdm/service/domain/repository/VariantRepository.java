package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VariantHistory;

import java.util.List;
import java.util.Optional;

/**
 * 版本仓储接口
 *
 * @author hwyz_leo
 */
public interface VariantRepository {

    Variant save(Variant variant, String operationType);

    Optional<Variant> findById(Long id);

    Optional<Variant> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByModelCode(String modelCode);

    List<Variant> findAll(int page, int size, String modelCode, String carLineCode, String platformCode, boolean includeInactive);

    long count(String modelCode, String carLineCode, String platformCode, boolean includeInactive);

    void delete(Variant variant);

    List<VariantHistory> findHistoryByCode(String code);
}
