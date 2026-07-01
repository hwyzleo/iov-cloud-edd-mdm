package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;

import java.util.List;
import java.util.Optional;

/**
 * SWIN编码方案仓储接口
 *
 * @author hwyz_leo
 */
public interface SwinSchemeRepository {

    /**
     * 保存SWIN编码方案
     *
     * @param swinScheme SWIN编码方案
     */
    void save(SwinScheme swinScheme);

    /**
     * 根据代码查找SWIN编码方案
     *
     * @param code 代码
     * @return SWIN编码方案
     */
    Optional<SwinScheme> findByCode(String code);

    /**
     * 检查代码是否存在
     *
     * @param code 代码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 查找所有有效的SWIN编码方案
     *
     * @return SWIN编码方案列表
     */
    List<SwinScheme> findAllActive();

    /**
     * 分页查找SWIN编码方案
     *
     * @param page             页码
     * @param size             每页大小
     * @param includeInactive  是否包含失效的
     * @return SWIN编码方案列表
     */
    List<SwinScheme> findPaginated(int page, int size, boolean includeInactive);

    /**
     * 统计总数
     *
     * @param includeInactive 是否包含失效的
     * @return 总数
     */
    long count(boolean includeInactive);

    /**
     * 删除SWIN编码方案
     *
     * @param code 代码
     */
    void deleteByCode(String code);
}
