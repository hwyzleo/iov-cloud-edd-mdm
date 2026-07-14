package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 软件基线补发领域服务（Material 子域）
 * <p>
 * 负责按 baseline_status 映射 eventType、构建当前全量快照 payload。
 * 设计参考：MDM-DSN-CR-029 §2 DDD 类清单 / §3 F25/F26 流程。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoftwareBaselineRepublishDomainService {

    private final SoftwareBaselineRepository softwareBaselineRepository;

    /**
     * 根据基线状态解析事件类型
     * <p>
     * DRAFT → SoftwareBaselineUpdated
     * RELEASED → SoftwareBaselineReleased
     * SUPERSEDED → SoftwareBaselineSuperseded
     *
     * @param baselineStatus 基线状态
     * @return 事件类型
     */
    public String resolveEventTypeByStatus(String baselineStatus) {
        if (baselineStatus == null) {
            return "SoftwareBaselineUpdated";
        }
        return switch (baselineStatus) {
            case "DRAFT" -> "SoftwareBaselineUpdated";
            case "RELEASED" -> "SoftwareBaselineReleased";
            case "SUPERSEDED" -> "SoftwareBaselineSuperseded";
            default -> "SoftwareBaselineUpdated";
        };
    }

    /**
     * 按过滤条件统计基线数量
     *
     * @param anchorType     锚定层级类型
     * @param anchorCode     锚点编码
     * @param baselineStatus 基线状态
     * @param codes          指定基线编码列表
     * @return 命中数量
     */
    public long countByFilter(String anchorType, String anchorCode, String baselineStatus, List<String> codes) {
        return softwareBaselineRepository.countByFilter(anchorType, anchorCode, baselineStatus, codes);
    }

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
    public List<String> listCodesByFilter(String anchorType, String anchorCode, String baselineStatus,
                                           List<String> codes, int page, int size) {
        return softwareBaselineRepository.listCodesByFilter(anchorType, anchorCode, baselineStatus, codes, page, size);
    }
}
