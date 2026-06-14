package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.DeviceCategoryHistory;

import java.util.List;
import java.util.Optional;

/**
 * 设备类别仓储接口（EEAD 子域）
 *
 * @author hwyz_leo
 */
public interface DeviceCategoryRepository {

    DeviceCategory save(DeviceCategory category, String operationType);

    Optional<DeviceCategory> findByCode(String code);

    boolean existsByCode(String code);

    void delete(DeviceCategory category, String operator);

    List<DeviceCategory> list(String status, int page, int size);

    long count(String status);

    List<DeviceCategory> listAllActive();

    List<DeviceCategoryHistory> findHistoryByCode(String code);
}
