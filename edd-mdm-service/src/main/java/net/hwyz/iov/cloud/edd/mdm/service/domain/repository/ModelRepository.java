package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ModelHistory;

import java.util.List;
import java.util.Optional;

/**
 * 车型仓储接口
 *
 * @author hwyz_leo
 */
public interface ModelRepository {

    Model save(Model model, String operationType);

    Optional<Model> findById(Long id);

    Optional<Model> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCarLineCode(String carLineCode);

    /**
     * 检查是否存在指定车系下的活跃车型
     *
     * @param carLineCode 车系code
     * @return 是否存在ACTIVE状态的车型
     */
    boolean existsByCarLineCodeAndStatusActive(String carLineCode);

    /**
     * 检查是否存在指定平台下的活跃车型
     *
     * @param platformCode 平台code
     * @return 是否存在ACTIVE状态的车型
     */
    boolean existsByPlatformCodeAndStatusActive(String platformCode);

    List<Model> findAll(int page, int size, String carLineCode, String platformCode, boolean includeInactive);

    long count(String carLineCode, String platformCode, boolean includeInactive);

    void delete(Model model);

    List<ModelHistory> findHistoryByCode(String code);
}
