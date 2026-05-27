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

    Model save(Model model);

    Optional<Model> findById(Long id);

    Optional<Model> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCarLineCode(String carLineCode);

    List<Model> findAll(int page, int size, String carLineCode, String platformCode, boolean includeInactive);

    long count(String carLineCode, String platformCode, boolean includeInactive);

    void delete(Model model);

    List<ModelHistory> findHistoryByCode(String code);
}
