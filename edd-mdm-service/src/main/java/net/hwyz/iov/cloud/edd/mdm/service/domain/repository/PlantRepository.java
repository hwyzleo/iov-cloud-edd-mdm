package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlantHistory;

import java.util.List;
import java.util.Optional;

/**
 * 工厂仓储接口（Org 子域）
 *
 * @author hwyz_leo
 */
public interface PlantRepository {

    Plant save(Plant plant, String operationType);

    Optional<Plant> findById(Long id);

    Optional<Plant> findByCode(String code);

    boolean existsByCode(String code);

    void delete(Plant plant, String operator, boolean forceDelete);

    List<Plant> list(String plantType, String country, String status, int page, int size);

    long count(String plantType, String country, String status);

    List<Plant> listAllActive();

    List<Plant> snapshot(boolean includeInactive, int page, int size);

    long snapshotCount(boolean includeInactive);

    List<Plant> listByPlantType(String plantType);

    List<PlantHistory> findHistoryByCode(String code);
}
