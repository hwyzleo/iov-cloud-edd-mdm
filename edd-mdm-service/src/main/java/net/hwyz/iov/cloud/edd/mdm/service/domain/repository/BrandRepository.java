package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.BrandHistory;

import java.util.List;
import java.util.Optional;

/**
 * 品牌仓储接口
 *
 * @author hwyz_leo
 */
public interface BrandRepository {

    /**
     * 保存品牌
     *
     * @param brand 品牌聚合根
     * @return 保存后的品牌
     */
    Brand save(Brand brand);

    /**
     * 根据ID查找品牌
     *
     * @param id 主键ID
     * @return 品牌
     */
    Optional<Brand> findById(Long id);

    /**
     * 根据code查找品牌
     *
     * @param code 业务主键
     * @return 品牌
     */
    Optional<Brand> findByCode(String code);

    /**
     * 检查code是否存在
     *
     * @param code 业务主键
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 分页查询品牌列表
     *
     * @param page           页码
     * @param size           每页大小
     * @param includeInactive 是否包含失效记录
     * @return 品牌列表
     */
    List<Brand> findAll(int page, int size, boolean includeInactive);

    /**
     * 查询品牌总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    long count(boolean includeInactive);

    /**
     * 删除品牌
     *
     * @param brand 品牌聚合根
     */
    void delete(Brand brand);

    /**
     * 查询品牌历史版本列表
     *
     * @param code 品牌code
     * @return 历史版本列表
     */
    List<BrandHistory> findHistoryByCode(String code);
}
