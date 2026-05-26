package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;

import java.util.List;
import java.util.Optional;

/**
 * 平台仓储接口
 *
 * @author hwyz_leo
 */
public interface PlatformRepository {

    /**
     * 保存平台
     *
     * @param platform 平台聚合根
     * @return 保存后的平台
     */
    Platform save(Platform platform);

    /**
     * 根据ID查找平台
     *
     * @param id 主键ID
     * @return 平台
     */
    Optional<Platform> findById(Long id);

    /**
     * 根据code查找平台
     *
     * @param code 业务主键
     * @return 平台
     */
    Optional<Platform> findByCode(String code);

    /**
     * 检查code是否存在
     *
     * @param code 业务主键
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 分页查询平台列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 平台列表
     */
    List<Platform> findAll(int page, int size, boolean includeInactive);

    /**
     * 查询平台总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    long count(boolean includeInactive);

    /**
     * 删除平台
     *
     * @param platform 平台聚合根
     */
    void delete(Platform platform);
}
