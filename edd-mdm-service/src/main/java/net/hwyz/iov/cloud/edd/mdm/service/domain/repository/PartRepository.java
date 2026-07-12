package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PartHistory;

import java.util.List;
import java.util.Optional;

/**
 * 零件仓储接口（Material 子域）
 *
 * @author hwyz_leo
 */
public interface PartRepository {

    Part save(Part part, String operationType);

    Optional<Part> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsBySubstitutePartCode(String substitutePartCode);

    void delete(Part part, String operator, boolean forceDelete);

    List<Part> list(String keyword, String categoryCode, String partType, String vehicleNodeCode,
                    String supplierCode, String lifecycleStage, Boolean isSoftware, String status, int page, int size);

    long count(String keyword, String categoryCode, String partType, String vehicleNodeCode,
               String supplierCode, String lifecycleStage, Boolean isSoftware, String status);

    List<Part> listAllActive();

    List<Part> snapshot(boolean includeInactive, int page, int size);

    long snapshotCount(boolean includeInactive);

    List<Part> listByCategory(String categoryCode);

    List<Part> listByVehicleNode(String vehicleNodeCode);

    List<Part> listBySupplier(String supplierCode);

    List<PartHistory> findHistoryByCode(String code);

    /**
     * 按base_no查询最新代次记录
     * @param baseNo 零件基础号
     * @return 最新代次的Part
     */
    Optional<Part> findLatestByBaseNo(String baseNo);
}
