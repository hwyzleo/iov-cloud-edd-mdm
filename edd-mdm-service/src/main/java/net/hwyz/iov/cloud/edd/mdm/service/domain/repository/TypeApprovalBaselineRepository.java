package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;

import java.util.List;
import java.util.Optional;

/**
 * 型式批准基线仓储接口（EEAD 子域）
 *
 * @author hwyz_leo
 */
public interface TypeApprovalBaselineRepository {

    /**
     * 保存基线（创建或更新）
     *
     * @param baseline      基线聚合根
     * @param operationType 操作类型
     * @return 保存后的基线
     */
    TypeApprovalBaseline save(TypeApprovalBaseline baseline, String operationType);

    /**
     * 根据业务键查找基线
     *
     * @param taBaselineCode 业务键
     * @return 基线
     */
    Optional<TypeApprovalBaseline> findByCode(String taBaselineCode);

    /**
     * 根据锚点和投影摘要查找基线（幂等检查）
     *
     * @param swinCode         SWIN 代码
     * @param anchorType       锚定层级类型
     * @param anchorCode       锚点编码
     * @param projectionDigest 投影摘要
     * @return 基线
     */
    Optional<TypeApprovalBaseline> findByAnchorAndDigest(String swinCode, String anchorType,
                                                          String anchorCode, String projectionDigest);

    /**
     * 检查业务键是否存在
     *
     * @param taBaselineCode 业务键
     * @return 是否存在
     */
    boolean existsByCode(String taBaselineCode);

    /**
     * 根据 SWIN 代码统计基线数量（供删除保护）
     *
     * @param swinCode SWIN 代码
     * @return 基线数量
     */
    long countBySwinCode(String swinCode);

    /**
     * 根据 SWIN 代码查找基线列表（供删除保护）
     *
     * @param swinCode SWIN 代码
     * @return 基线列表
     */
    List<TypeApprovalBaseline> listBySwinCode(String swinCode);

    /**
     * 分页查询基线列表
     *
     * @param swinCode   SWIN 代码（可选）
     * @param anchorType 锚定层级类型（可选）
     * @param anchorCode 锚点编码（可选）
     * @param status     状态（可选）
     * @param code       业务键（可选）
     * @param page       页码
     * @param size       每页大小
     * @return 基线列表
     */
    List<TypeApprovalBaseline> list(String swinCode, String anchorType, String anchorCode,
                                     String status, String code, int page, int size);

    /**
     * 统计基线数量
     *
     * @param swinCode   SWIN 代码（可选）
     * @param anchorType 锚定层级类型（可选）
     * @param anchorCode 锚点编码（可选）
     * @param status     状态（可选）
     * @param code       业务键（可选）
     * @return 基线数量
     */
    long count(String swinCode, String anchorType, String anchorCode, String status, String code);

    /**
     * 删除基线（逻辑删除）
     *
     * @param baseline 基线
     * @param operator 操作人
     */
    void delete(TypeApprovalBaseline baseline, String operator);

    /**
     * 保存基线历史
     *
     * @param history 历史记录
     */
    void saveHistory(TaBaselineHistory history);

    /**
     * 根据业务键查找历史记录
     *
     * @param taBaselineCode 业务键
     * @return 历史记录列表
     */
    List<TaBaselineHistory> findHistoryByCode(String taBaselineCode);

    /**
     * 保存基线项
     *
     * @param item 基线项
     */
    void saveItem(TaBaselineItem item);

    /**
     * 根据基线 ID 删除基线项
     *
     * @param taBaselineId 基线 ID
     */
    void deleteItemsByBaselineId(Long taBaselineId);

    /**
     * 根据基线 ID 查找基线项列表
     *
     * @param taBaselineId 基线 ID
     * @return 基线项列表
     */
    List<TaBaselineItem> findItemsByBaselineId(Long taBaselineId);
}
