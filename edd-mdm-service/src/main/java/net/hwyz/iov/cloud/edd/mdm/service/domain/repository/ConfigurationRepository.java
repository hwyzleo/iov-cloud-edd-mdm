package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ConfigurationHistory;

import java.util.List;
import java.util.Optional;

/**
 * 配置仓储接口
 *
 * @author hwyz_leo
 */
public interface ConfigurationRepository {

    Configuration save(Configuration configuration, String operationType);

    Optional<Configuration> findById(Long id);

    Optional<Configuration> findByCode(String code);

    /**
     * 按 (source_system, source_id) 查找上游推送的本地记录。
     * 用于 CR-005 上游 ingest 第 1 层幂等更新判定。
     *
     * @param sourceSystem 来源系统编码
     * @param sourceId     上游业务主键
     * @return 命中的本地 Configuration（若存在）
     */
    Optional<Configuration> findBySourceSystemAndSourceId(String sourceSystem, String sourceId);

    boolean existsByCode(String code);

    boolean existsByVariantCode(String variantCode);

    List<Configuration> findAll(int page, int size, String variantCode, boolean includeInactive);

    long count(String variantCode, boolean includeInactive);

    void delete(Configuration configuration);

    List<ConfigurationHistory> findHistoryByCode(String code);

    List<Configuration> findByCodes(List<String> codes, boolean onlyActive);
}
