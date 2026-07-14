package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;

import java.util.List;
import java.util.Optional;

/**
 * 软件基线仓储接口（Material 子域）
 *
 * @author hwyz_leo
 */
public interface SoftwareBaselineRepository {

    /**
     * 锚点条目，用于多锚点查询
     */
    record AnchorEntry(AnchorType anchorType, String anchorCode) {
    }

    SoftwareBaseline save(SoftwareBaseline baseline, String operationType);

    Optional<SoftwareBaseline> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByAnchorAndVersion(AnchorType anchorType, String anchorCode, String baselineVersion);

    void delete(SoftwareBaseline baseline, String operator, boolean forceDelete);

    List<SoftwareBaseline> list(String anchorType, String anchorCode, String baselineStatus,
                                 String status, int page, int size);

    long count(String anchorType, String anchorCode, String baselineStatus, String status);

    List<SoftwareBaseline> snapshot(boolean includeDraft, boolean includeSuperseded, int page, int size);

    long snapshotCount(boolean includeDraft, boolean includeSuperseded);

    List<SoftwareBaseline> findActiveByAnchor(AnchorType anchorType, String anchorCode);

    /**
     * 按多个锚点查找 RELEASED 状态的软件基线
     * <p>
     * 用于 TA 基线卷积投影时，支持锚点层级继承。
     * 例如：当 SWIN 锚点是 MODEL 时，可查找该 MODEL 下所有 VARIANT 或 CONFIGURATION 级别的软件基线。
     *
     * @param anchorEntries 锚点列表，每个元素为 [anchorType, anchorCode]
     * @return 匹配的 RELEASED 软件基线列表
     */
    List<SoftwareBaseline> findActiveByAnchors(List<AnchorEntry> anchorEntries);

    List<SoftwareBaseline> listAllActive();

    long countByAnchor(AnchorType anchorType, String anchorCode);

    long countByPartCode(String partCode);

    List<SoftwareBaseline> listByPartCode(String partCode);

    List<SoftwareBaselineHistory> findHistoryByCode(String code);

    void saveItem(SoftwareBaselineItem item);

    void deleteItem(SoftwareBaselineItem item);

    void deleteItemByBaselineAndPart(String baselineCode, String partCode);

    List<SoftwareBaselineItem> findItemsByBaselineCode(String baselineCode);

    /**
     * 按过滤条件统计基线数量
     *
     * @param anchorType     锚定层级类型
     * @param anchorCode     锚点编码
     * @param baselineStatus 基线状态
     * @param codes          指定基线编码列表
     * @return 命中数量
     */
    long countByFilter(String anchorType, String anchorCode, String baselineStatus, List<String> codes);

    /**
     * 按过滤条件分页查询基线编码列表
     *
     * @param anchorType     锚定层级类型
     * @param anchorCode     锚点编码
     * @param baselineStatus 基线状态
     * @param codes          指定基线编码列表
     * @param page           页码
     * @param size           每页大小
     * @return 基线编码列表
     */
    List<String> listCodesByFilter(String anchorType, String anchorCode, String baselineStatus, List<String> codes, int page, int size);
}
