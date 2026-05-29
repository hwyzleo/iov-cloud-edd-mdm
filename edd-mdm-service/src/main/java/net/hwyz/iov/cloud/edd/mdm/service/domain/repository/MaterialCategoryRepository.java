package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.MaterialCategoryHistory;

import java.util.List;
import java.util.Optional;

/**
 * 物料品类仓储接口（Material 子域）
 *
 * @author hwyz_leo
 */
public interface MaterialCategoryRepository {

    MaterialCategory save(MaterialCategory category, String operationType);

    Optional<MaterialCategory> findByCode(String code);

    boolean existsByCode(String code);

    boolean hasChildren(String parentCode);

    boolean hasParts(String categoryCode);

    void delete(MaterialCategory category, String operator);

    List<MaterialCategory> list(String parentCode, String status, int page, int size);

    long count(String parentCode, String status);

    List<MaterialCategory> listAllActive();

    List<MaterialCategory> tree();

    List<MaterialCategoryHistory> findHistoryByCode(String code);
}
