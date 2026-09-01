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

    /**
     * 查询全部品类（row_valid=1，含 DRAFT/ACTIVE/INACTIVE，供名称防重/层级/叶子与存量审计使用）
     *
     * @return 品类列表
     */
    List<MaterialCategory> findAll();

    /**
     * 统计 ACTIVE 且 row_valid=1 的子节点数量（叶子判定依据）
     *
     * @param parentCode 父品类 code
     * @return ACTIVE 子节点数量
     */
    long countActiveChildren(String parentCode);

    /**
     * 统计引用指定品类 code 的 Part 数量（含所有状态的存量记录）
     *
     * @param categoryCode 品类 code
     * @return 引用数量
     */
    long countParts(String categoryCode);

    void delete(MaterialCategory category, String operator);

    List<MaterialCategory> list(String parentCode, String status, int page, int size);

    long count(String parentCode, String status);

    List<MaterialCategory> listAllActive();

    List<MaterialCategory> tree();

    List<MaterialCategoryHistory> findHistoryByCode(String code);
}
