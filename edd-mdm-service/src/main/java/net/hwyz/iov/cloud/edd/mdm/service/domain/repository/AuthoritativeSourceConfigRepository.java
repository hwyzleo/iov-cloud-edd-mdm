package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;

import java.util.Map;

/**
 * 权威数据源配置仓储接口
 *
 * @author hwyz_leo
 */
public interface AuthoritativeSourceConfigRepository {

    /**
     * 根据实体类型和编码查找配置
     *
     * @param entityType 实体类型
     * @param code       编码
     * @return 配置映射
     */
    Map<String, String> findConfig(EntityType entityType, String code);

    /**
     * 根据实体类型查找默认配置
     *
     * @param entityType 实体类型
     * @return 配置映射
     */
    Map<String, String> findDefaultConfig(EntityType entityType);
}
