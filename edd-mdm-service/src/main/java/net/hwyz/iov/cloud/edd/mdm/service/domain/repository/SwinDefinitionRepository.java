package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;

import java.util.List;
import java.util.Optional;

/**
 * SWIN定义仓储接口
 *
 * @author hwyz_leo
 */
public interface SwinDefinitionRepository {

    /**
     * 保存SWIN定义
     *
     * @param swinDefinition SWIN定义
     */
    void save(SwinDefinition swinDefinition);

    /**
     * 根据SWIN代码查找SWIN定义
     *
     * @param swinCode SWIN代码
     * @return SWIN定义
     */
    Optional<SwinDefinition> findBySwinCode(String swinCode);

    /**
     * 检查SWIN代码是否存在
     *
     * @param swinCode SWIN代码
     * @return 是否存在
     */
    boolean existsBySwinCode(String swinCode);

    /**
     * 根据编码方案代码统计引用数
     *
     * @param schemeCode 编码方案代码
     * @return 引用数
     */
    long countBySchemeCode(String schemeCode);

    /**
     * 根据类型引用统计ACTIVE状态的SWIN定义数
     *
     * @param typeRefType 引用类型
     * @param typeRefCode 引用代码
     * @return ACTIVE状态的SWIN定义数
     */
    long countActiveByTypeRef(String typeRefType, String typeRefCode);

    /**
     * 根据编码方案代码查找所有有效的SWIN定义
     *
     * @param schemeCode 编码方案代码
     * @return SWIN定义列表
     */
    List<SwinDefinition> findAllActiveBySchemeCode(String schemeCode);

    /**
     * 根据引用类型和引用代码查找所有有效的SWIN定义
     *
     * @param typeRefType 引用类型
     * @param typeRefCode 引用代码
     * @return SWIN定义列表
     */
    List<SwinDefinition> findAllActiveByTypeRef(String typeRefType, String typeRefCode);

    /**
     * 查找所有有效的SWIN定义
     *
     * @return SWIN定义列表
     */
    List<SwinDefinition> findAllActive();

    /**
     * 分页查找SWIN定义
     *
     * @param page             页码
     * @param size             每页大小
     * @param includeInactive  是否包含失效的
     * @return SWIN定义列表
     */
    List<SwinDefinition> findPaginated(int page, int size, boolean includeInactive);

    /**
     * 统计总数
     *
     * @param includeInactive 是否包含失效的
     * @return 总数
     */
    long count(boolean includeInactive);

    /**
     * 删除SWIN定义
     *
     * @param swinCode SWIN代码
     */
    void deleteBySwinCode(String swinCode);
}
