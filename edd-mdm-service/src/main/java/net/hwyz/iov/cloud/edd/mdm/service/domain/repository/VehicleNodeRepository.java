package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VehicleNodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;

import java.util.List;
import java.util.Optional;

/**
 * 车载节点仓储接口（EEAD 子域）
 * <p>
 * 持久化实现负责：保存时同步写 history 快照、UK 冲突映射为 VehicleNodeDuplicateCodeException、
 * 查询不存在映射为 VehicleNodeNotExistException。
 *
 * @author hwyz_leo
 */
public interface VehicleNodeRepository {

    /**
     * 保存（创建或更新）车载节点，同时写一条 history 快照。
     *
     * @param vehicleNode   聚合根
     * @param operationType 操作类型：CREATE / UPDATE / DEACTIVATE / DELETE
     * @return 持久化后的聚合根（含主键 id）
     */
    VehicleNode save(VehicleNode vehicleNode, String operationType);

    /**
     * 根据主键查询。
     */
    Optional<VehicleNode> findById(Long id);

    /**
     * 根据业务主键 nodeCode 查询。
     */
    Optional<VehicleNode> findByCode(String nodeCode);

    /**
     * 校验 nodeCode 是否已存在（用于创建前置校验，与 DB UK 形成双重保险）。
     */
    boolean existsByCode(String nodeCode);

    /**
     * 物理删除车载节点（先写 DELETE 快照、再 DELETE 主表）。
     *
     * @param vehicleNode 待删除聚合根
     * @param operator    操作人
     * @param forceDelete 是否 force 旁路删除（仅记录到历史快照的 forceDelete 字段）
     */
    void delete(VehicleNode vehicleNode, String operator, boolean forceDelete);

    /**
     * 列表查询（支持组合过滤 + 分页）。
     *
     * @param nodeType         节点类型（可空）
     * @param functionalDomain 功能域（可空）
     * @param otaSupportType   OTA 支持类型（可空）
     * @param isCoreNode       是否核心节点（可空）
     * @param status           状态（可空，传 null 默认仅 ACTIVE）
     * @param page             页码（从 1 起）
     * @param size             每页大小
     */
    List<VehicleNode> list(String nodeType, String functionalDomain, String otaSupportType,
                           Boolean isCoreNode, String status, int page, int size);

    /**
     * 列表查询总数（与 list 同条件）。
     */
    long count(String nodeType, String functionalDomain, String otaSupportType,
               Boolean isCoreNode, String status);

    /**
     * 列出所有 ACTIVE 车载节点（不分页，供下拉选择）。
     */
    List<VehicleNode> listAllActive();

    /**
     * 全量快照（供下游 Bootstrap 与对账）。
     *
     * @param includeInactive true 返回所有状态，false 仅返回 ACTIVE
     * @param page            页码（从 1 起）
     * @param size            每页大小
     */
    List<VehicleNode> snapshot(boolean includeInactive, int page, int size);

    /**
     * 全量快照总数（与 snapshot 同条件）。
     */
    long snapshotCount(boolean includeInactive);

    /**
     * 按 OTA 支持类型批量查询（仅 status=ACTIVE）。
     */
    List<VehicleNode> listByOtaType(OtaSupportType otaSupportType);

    /**
     * 查询历史版本（按 version 降序）。
     */
    List<VehicleNodeHistory> findHistoryByCode(String nodeCode);

    /**
     * 统计引用指定设备类别的车载节点数量（用于删除前置依赖检查）。
     *
     * @param deviceCategoryCode 设备类别 code
     * @return 引用数量
     */
    long countByDeviceCategory(String deviceCategoryCode);
}
