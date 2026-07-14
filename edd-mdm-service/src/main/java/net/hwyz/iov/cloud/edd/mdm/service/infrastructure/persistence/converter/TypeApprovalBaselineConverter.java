package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TaBaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineItemPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TypeApprovalBaselinePo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 型式批准基线转换器
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TypeApprovalBaselineConverter {

    private final ObjectMapper objectMapper;

    public TypeApprovalBaseline toDomain(TypeApprovalBaselinePo po) {
        if (po == null) {
            return null;
        }
        return TypeApprovalBaseline.builder()
                .id(po.getId())
                .taBaselineCode(po.getTaBaselineCode())
                .swinCode(po.getSwinCode())
                .anchorType(po.getAnchorType() != null ? AnchorType.valueOf(po.getAnchorType()) : null)
                .anchorCode(po.getAnchorCode())
                .status(po.getStatus() != null ? TaBaselineStatus.valueOf(po.getStatus()) : null)
                .projectionDigest(po.getProjectionDigest())
                .sourceBaselineScope(po.getSourceBaselineScope())
                .effectiveFrom(po.getEffectiveFrom())
                .remark(po.getRemark())
                .version(po.getVersion())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .items(new ArrayList<>())
                .build();
    }

    public TypeApprovalBaselinePo toPo(TypeApprovalBaseline domain) {
        if (domain == null) {
            return null;
        }
        return TypeApprovalBaselinePo.builder()
                .id(domain.getId())
                .taBaselineCode(domain.getTaBaselineCode())
                .swinCode(domain.getSwinCode())
                .anchorType(domain.getAnchorType() != null ? domain.getAnchorType().name() : null)
                .anchorCode(domain.getAnchorCode())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .projectionDigest(domain.getProjectionDigest())
                .sourceBaselineScope(domain.getSourceBaselineScope())
                .effectiveFrom(domain.getEffectiveFrom())
                .remark(domain.getRemark())
                .version(domain.getVersion())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }

    public TaBaselineItem toDomain(TaBaselineItemPo po) {
        if (po == null) {
            return null;
        }
        return TaBaselineItem.builder()
                .id(po.getId())
                .taBaselineId(po.getTaBaselineId())
                .vehicleNodeCode(po.getVehicleNodeCode())
                .partCode(po.getPartCode())
                .approvedVersion(po.getApprovedVersion())
                .sourceBaselineCode(po.getSourceBaselineCode())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public TaBaselineItemPo toPo(TaBaselineItem domain) {
        if (domain == null) {
            return null;
        }
        return TaBaselineItemPo.builder()
                .id(domain.getId())
                .taBaselineId(domain.getTaBaselineId())
                .vehicleNodeCode(domain.getVehicleNodeCode())
                .partCode(domain.getPartCode())
                .approvedVersion(domain.getApprovedVersion())
                .sourceBaselineCode(domain.getSourceBaselineCode())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }

    public TaBaselineHistory toHistoryDomain(TaBaselineHistoryPo po) {
        if (po == null) {
            return null;
        }
        List<TaBaselineItem> items = Collections.emptyList();
        if (po.getItemsSnapshot() != null) {
            try {
                items = objectMapper.readValue(po.getItemsSnapshot(), new TypeReference<List<TaBaselineItemPo>>() {})
                        .stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                log.warn("解析TA基线项快照 JSON 失败: code={}", po.getTaBaselineCode(), e);
            }
        }
        return TaBaselineHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .taBaselineCode(po.getTaBaselineCode())
                .swinCode(po.getSwinCode())
                .anchorType(po.getAnchorType() != null ? AnchorType.valueOf(po.getAnchorType()) : null)
                .anchorCode(po.getAnchorCode())
                .status(po.getStatus() != null ? TaBaselineStatus.valueOf(po.getStatus()) : null)
                .projectionDigest(po.getProjectionDigest())
                .sourceBaselineScope(po.getSourceBaselineScope())
                .effectiveFrom(po.getEffectiveFrom())
                .remark(po.getRemark())
                .version(po.getVersion())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .itemsSnapshot(items)
                .build();
    }

    public TaBaselineHistoryPo toHistoryPo(TypeApprovalBaseline baseline, String operationType, String operator) {
        if (baseline == null) {
            return null;
        }
        String itemsJson = null;
        if (baseline.getItems() != null && !baseline.getItems().isEmpty()) {
            try {
                List<TaBaselineItemPo> itemPos = baseline.getItems().stream()
                        .filter(i -> i.getRowValid() == null || i.getRowValid())
                        .map(this::toPo)
                        .collect(Collectors.toList());
                itemsJson = objectMapper.writeValueAsString(itemPos);
            } catch (JsonProcessingException e) {
                log.warn("序列化TA基线项快照 JSON 失败: code={}", baseline.getTaBaselineCode(), e);
            }
        }
        return TaBaselineHistoryPo.builder()
                .entityId(baseline.getId())
                .operationType(operationType)
                .snapshotTime(new java.util.Date())
                .operator(operator)
                .taBaselineCode(baseline.getTaBaselineCode())
                .swinCode(baseline.getSwinCode())
                .anchorType(baseline.getAnchorType() != null ? baseline.getAnchorType().name() : null)
                .anchorCode(baseline.getAnchorCode())
                .status(baseline.getStatus() != null ? baseline.getStatus().name() : null)
                .projectionDigest(baseline.getProjectionDigest())
                .sourceBaselineScope(baseline.getSourceBaselineScope())
                .effectiveFrom(baseline.getEffectiveFrom())
                .remark(baseline.getRemark())
                .version(baseline.getVersion())
                .createBy(baseline.getCreateBy())
                .createTime(baseline.getCreateTime())
                .modifyBy(baseline.getModifyBy())
                .modifyTime(baseline.getModifyTime())
                .rowVersion(baseline.getRowVersion())
                .rowValid(baseline.getRowValid())
                .itemsSnapshot(itemsJson)
                .build();
    }
}
