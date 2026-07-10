package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselineHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselineItemPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselinePo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 软件基线转换器
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SoftwareBaselineConverter {

    private final ObjectMapper objectMapper;

    public SoftwareBaseline toDomain(SoftwareBaselinePo po) {
        if (po == null) {
            return null;
        }
        return SoftwareBaseline.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .anchorType(po.getAnchorType() != null ? AnchorType.valueOf(po.getAnchorType()) : null)
                .anchorCode(po.getAnchorCode())
                .baselineVersion(po.getBaselineVersion())
                .baselineStatus(po.getBaselineStatus() != null ? BaselineStatus.valueOf(po.getBaselineStatus()) : null)
                .releasedAt(po.getReleasedAt())
                .releasedBy(po.getReleasedBy())
                .supersededByCode(po.getSupersededByCode())
                .description(po.getDescription())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(po.getStatus())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .items(new ArrayList<>())
                .build();
    }

    public SoftwareBaselinePo toPo(SoftwareBaseline domain) {
        if (domain == null) {
            return null;
        }
        return SoftwareBaselinePo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .anchorType(domain.getAnchorType() != null ? domain.getAnchorType().name() : null)
                .anchorCode(domain.getAnchorCode())
                .baselineVersion(domain.getBaselineVersion())
                .baselineStatus(domain.getBaselineStatus() != null ? domain.getBaselineStatus().name() : null)
                .releasedAt(domain.getReleasedAt())
                .releasedBy(domain.getReleasedBy())
                .supersededByCode(domain.getSupersededByCode())
                .description(domain.getDescription())
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }

    public SoftwareBaselineItem toDomain(SoftwareBaselineItemPo po) {
        if (po == null) {
            return null;
        }
        return SoftwareBaselineItem.builder()
                .id(po.getId())
                .baselineCode(po.getBaselineCode())
                .partCode(po.getPartCode())
                .vehicleNodeCode(po.getVehicleNodeCode())
                .remark(po.getRemark())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public SoftwareBaselineItemPo toPo(SoftwareBaselineItem domain) {
        if (domain == null) {
            return null;
        }
        return SoftwareBaselineItemPo.builder()
                .id(domain.getId())
                .baselineCode(domain.getBaselineCode())
                .partCode(domain.getPartCode())
                .vehicleNodeCode(domain.getVehicleNodeCode())
                .remark(domain.getRemark())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }

    public SoftwareBaselineHistory toHistoryDomain(SoftwareBaselineHistoryPo po) {
        if (po == null) {
            return null;
        }
        List<SoftwareBaselineItem> items = Collections.emptyList();
        if (po.getItemsSnapshot() != null) {
            try {
                items = objectMapper.readValue(po.getItemsSnapshot(), new TypeReference<List<SoftwareBaselineItemPo>>() {})
                        .stream()
                        .map(this::toDomain)
                        .collect(Collectors.toList());
            } catch (JsonProcessingException e) {
                log.warn("解析基线项快照 JSON 失败: code={}", po.getCode(), e);
            }
        }
        return SoftwareBaselineHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .code(po.getCode())
                .name(po.getName())
                .anchorType(po.getAnchorType() != null ? AnchorType.valueOf(po.getAnchorType()) : null)
                .anchorCode(po.getAnchorCode())
                .baselineVersion(po.getBaselineVersion())
                .baselineStatus(po.getBaselineStatus() != null ? BaselineStatus.valueOf(po.getBaselineStatus()) : null)
                .releasedAt(po.getReleasedAt())
                .releasedBy(po.getReleasedBy())
                .supersededByCode(po.getSupersededByCode())
                .description(po.getDescription())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(po.getStatus())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .forceDelete(po.getForceDelete())
                .itemsSnapshot(items)
                .build();
    }

    public SoftwareBaselineHistoryPo toHistoryPo(SoftwareBaseline baseline, String operationType,
                                                   String operator, boolean forceDelete) {
        if (baseline == null) {
            return null;
        }
        String itemsJson = null;
        if (baseline.getItems() != null && !baseline.getItems().isEmpty()) {
            try {
                List<SoftwareBaselineItemPo> itemPos = baseline.getItems().stream()
                        .filter(i -> i.getRowValid() == null || i.getRowValid())
                        .map(this::toPo)
                        .collect(Collectors.toList());
                itemsJson = objectMapper.writeValueAsString(itemPos);
            } catch (JsonProcessingException e) {
                log.warn("序列化基线项快照 JSON 失败: code={}", baseline.getCode(), e);
            }
        }
        return SoftwareBaselineHistoryPo.builder()
                .entityId(baseline.getId())
                .operationType(operationType)
                .snapshotTime(new java.util.Date())
                .operator(operator)
                .code(baseline.getCode())
                .name(baseline.getName())
                .anchorType(baseline.getAnchorType() != null ? baseline.getAnchorType().name() : null)
                .anchorCode(baseline.getAnchorCode())
                .baselineVersion(baseline.getBaselineVersion())
                .baselineStatus(baseline.getBaselineStatus() != null ? baseline.getBaselineStatus().name() : null)
                .releasedAt(baseline.getReleasedAt())
                .releasedBy(baseline.getReleasedBy())
                .supersededByCode(baseline.getSupersededByCode())
                .description(baseline.getDescription())
                .sourceSystem(baseline.getSourceSystem())
                .sourceId(baseline.getSourceId())
                .sourceVersion(baseline.getSourceVersion())
                .ingestionChannel(baseline.getIngestionChannel())
                .ingestionTime(baseline.getIngestionTime())
                .sourcePayloadHash(baseline.getSourcePayloadHash())
                .version(baseline.getVersion())
                .effectiveFrom(baseline.getEffectiveFrom())
                .effectiveTo(baseline.getEffectiveTo())
                .status(baseline.getStatus())
                .createBy(baseline.getCreateBy())
                .createTime(baseline.getCreateTime())
                .modifyBy(baseline.getModifyBy())
                .modifyTime(baseline.getModifyTime())
                .rowVersion(baseline.getRowVersion())
                .rowValid(baseline.getRowValid())
                .forceDelete(forceDelete)
                .itemsSnapshot(itemsJson)
                .build();
    }
}
