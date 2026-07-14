package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineAnchorMismatchException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineNoSourceException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineNodePartMappingException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineProjectionConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineSourceNotReleasedException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineSwinNotActiveException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinManagedSystem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TA基线投影领域服务
 * <p>
 * 负责卷积算法：从锚点范围内 RELEASED SoftwareBaseline 经型批相关过滤 + node↔part 归一，
 * 生成型式级型批版本组合。
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaBaselineProjectionDomainService {

    private final SoftwareBaselineRepository softwareBaselineRepository;
    private final SwinManagedSystemRepository swinManagedSystemRepository;
    private final SwinDefinitionRepository swinDefinitionRepository;

    /**
     * 执行卷积投影
     *
     * @param swinCode SWIN 定义代码
     * @return 投影结果
     */
    public ProjectionResult project(String swinCode) {
        log.info("执行TA基线卷积投影: swinCode={}", swinCode);

        // 1. 校验 SWIN 存在且 ACTIVE
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new TaBaselineSwinNotActiveException(swinCode));
        if (swinDefinition.getStatus() != SwinDefinitionStatus.ACTIVE) {
            throw new TaBaselineSwinNotActiveException(swinCode);
        }

        // 2. 获取锚点信息
        AnchorType anchorType = resolveAnchorType(swinDefinition);
        String anchorCode = resolveAnchorCode(swinDefinition);

        // 3. 获取型批相关受管节点集
        List<SwinManagedSystem> allManagedSystems = swinManagedSystemRepository.findBySwinCode(swinCode);
        List<SwinManagedSystem> taNodes = allManagedSystems.stream()
                .filter(ms -> ms.getIsTypeApprovalRelevant() != null && ms.getIsTypeApprovalRelevant())
                .collect(Collectors.toList());

        if (taNodes.isEmpty()) {
            log.warn("SWIN {} 无型批相关受管节点", swinCode);
            // 返回空投影
            return ProjectionResult.builder()
                    .swinCode(swinCode)
                    .anchorType(anchorType)
                    .anchorCode(anchorCode)
                    .items(new ArrayList<>())
                    .digest(computeDigest(new ArrayList<>()))
                    .build();
        }

        // 4. 获取锚点范围内 RELEASED SoftwareBaseline
        List<SoftwareBaseline> baselines = softwareBaselineRepository.findActiveByAnchor(anchorType, anchorCode);
        List<SoftwareBaseline> releasedBaselines = baselines.stream()
                .filter(b -> b.getBaselineStatus() == BaselineStatus.RELEASED)
                .collect(Collectors.toList());

        if (releasedBaselines.isEmpty()) {
            throw new TaBaselineNoSourceException(anchorType.name(), anchorCode);
        }

        // 5. 卷积：按型批相关节点过滤 items；node↔part 归一
        Map<String, Map<String, String>> projections = new HashMap<>();
        List<String> sourceBaselineCodes = new ArrayList<>();

        for (SoftwareBaseline baseline : releasedBaselines) {
            sourceBaselineCodes.add(baseline.getCode());
            Map<String, String> proj = new HashMap<>();

            List<SoftwareBaselineItem> items = baseline.getActiveItems();

            for (SwinManagedSystem node : taNodes) {
                String nodeCode = node.getVehicleNodeCode();

                // 过滤该节点的零件
                List<SoftwareBaselineItem> nodeItems = items.stream()
                        .filter(item -> nodeCode.equals(item.getVehicleNodeCode()))
                        .collect(Collectors.toList());

                if (nodeItems.isEmpty()) {
                    throw new TaBaselineNodePartMappingException(nodeCode, null, "节点无零件映射");
                }

                if (nodeItems.size() > 1) {
                    // 允许一个 node 映射多个 part（高低配 / 多供应商），但每个 (node, part) 版本须确定
                    // 这里需要检查是否有歧义
                    long distinctVersions = nodeItems.stream()
                            .map(SoftwareBaselineItem::getPartCode)
                            .distinct()
                            .count();
                    if (distinctVersions != nodeItems.size()) {
                        throw new TaBaselineNodePartMappingException(nodeCode, null, "节点零件映射歧义");
                    }
                }

                for (SoftwareBaselineItem item : nodeItems) {
                    String key = nodeCode + "|" + item.getPartCode();
                    proj.put(key, item.getPartCode()); // 使用零件代码作为版本标识
                }
            }

            projections.put(baseline.getCode(), proj);
        }

        // 6. 同一型式判定：各基线型批投影比对
        if (projections.size() > 1) {
            Map<String, String> firstProjection = projections.values().iterator().next();
            for (Map.Entry<String, Map<String, String>> entry : projections.entrySet()) {
                if (!firstProjection.equals(entry.getValue())) {
                    String diff = computeProjectionDiff(firstProjection, entry.getValue());
                    throw new TaBaselineProjectionConflictException(swinCode, diff);
                }
            }
        }

        // 7. 校验锚点层级与 SWIN 一致
        validateAnchorConsistency(swinDefinition, anchorType);

        // 8. 计算投影摘要
        Map<String, String> finalProjection = projections.isEmpty() ? new HashMap<>() : projections.values().iterator().next();
        List<ProjectionItem> projectionItems = finalProjection.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("\\|");
                    return ProjectionItem.builder()
                            .vehicleNodeCode(parts[0])
                            .partCode(parts[1])
                            .approvedVersion(e.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        String digest = computeDigest(projectionItems);

        return ProjectionResult.builder()
                .swinCode(swinCode)
                .anchorType(anchorType)
                .anchorCode(anchorCode)
                .items(projectionItems)
                .sourceBaselineCodes(sourceBaselineCodes)
                .digest(digest)
                .build();
    }

    /**
     * 解析锚点类型
     */
    private AnchorType resolveAnchorType(SwinDefinition swinDefinition) {
        // 根据 SWIN 定义的 typeRefType 确定锚点类型
        if ("VARIANT".equals(swinDefinition.getTypeRefType())) {
            return AnchorType.VARIANT;
        } else if ("MODEL".equals(swinDefinition.getTypeRefType())) {
            return AnchorType.MODEL;
        }
        throw new IllegalArgumentException("不支持的SWIN类型引用: " + swinDefinition.getTypeRefType());
    }

    /**
     * 解析锚点代码
     */
    private String resolveAnchorCode(SwinDefinition swinDefinition) {
        return swinDefinition.getTypeRefCode();
    }

    /**
     * 校验锚点一致性
     */
    private void validateAnchorConsistency(SwinDefinition swinDefinition, AnchorType anchorType) {
        String expectedLevel = anchorType.name();
        String actualLevel = swinDefinition.getTypeRefType();
        if (!expectedLevel.equals(actualLevel)) {
            throw new TaBaselineAnchorMismatchException(swinDefinition.getSwinCode(), expectedLevel, actualLevel);
        }
    }

    /**
     * 计算投影摘要
     */
    private String computeDigest(List<ProjectionItem> items) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            items.stream()
                    .sorted((a, b) -> {
                        int cmp = a.getVehicleNodeCode().compareTo(b.getVehicleNodeCode());
                        return cmp != 0 ? cmp : a.getPartCode().compareTo(b.getPartCode());
                    })
                    .forEach(item -> {
                        sb.append(item.getVehicleNodeCode())
                                .append("|")
                                .append(item.getPartCode())
                                .append("|")
                                .append(item.getApprovedVersion())
                                .append(";");
                    });
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return "sha256:" + bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不可用", e);
        }
    }

    /**
     * 计算投影差异
     */
    private String computeProjectionDiff(Map<String, String> proj1, Map<String, String> proj2) {
        StringBuilder diff = new StringBuilder();
        for (Map.Entry<String, String> entry : proj1.entrySet()) {
            String key = entry.getKey();
            String val1 = entry.getValue();
            String val2 = proj2.get(key);
            if (!val1.equals(val2)) {
                diff.append(key).append(": ").append(val1).append(" vs ").append(val2).append("; ");
            }
        }
        for (Map.Entry<String, String> entry : proj2.entrySet()) {
            if (!proj1.containsKey(entry.getKey())) {
                diff.append(entry.getKey()).append(": missing vs ").append(entry.getValue()).append("; ");
            }
        }
        return diff.toString();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 投影结果
     */
    @lombok.Data
    @lombok.Builder
    public static class ProjectionResult {
        private String swinCode;
        private AnchorType anchorType;
        private String anchorCode;
        private List<ProjectionItem> items;
        private List<String> sourceBaselineCodes;
        private String digest;
    }

    /**
     * 投影项
     */
    @lombok.Data
    @lombok.Builder
    public static class ProjectionItem {
        private String vehicleNodeCode;
        private String partCode;
        private String approvedVersion;
    }
}
