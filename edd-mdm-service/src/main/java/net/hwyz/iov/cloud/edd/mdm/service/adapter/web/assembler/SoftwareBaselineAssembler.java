package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineItemResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineItemDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 软件基线组装器
 *
 * @author hwyz_leo
 */
@Component
public class SoftwareBaselineAssembler {

    public SoftwareBaselineResponse toResponse(SoftwareBaselineDto dto) {
        if (dto == null) {
            return null;
        }
        return SoftwareBaselineResponse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .anchorType(dto.getAnchorType())
                .anchorCode(dto.getAnchorCode())
                .baselineVersion(dto.getBaselineVersion())
                .baselineStatus(dto.getBaselineStatus())
                .releasedAt(dto.getReleasedAt())
                .releasedBy(dto.getReleasedBy())
                .supersededByCode(dto.getSupersededByCode())
                .description(dto.getDescription())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .items(dto.getItems() != null ? dto.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public SoftwareBaselinePageResponse toPageResponse(List<SoftwareBaselineDto> dtos, long total) {
        return SoftwareBaselinePageResponse.builder()
                .list(dtos.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList()))
                .total(total)
                .build();
    }

    public SoftwareBaselineHistoryResponse toHistoryResponse(SoftwareBaselineHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return SoftwareBaselineHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .operationType(dto.getOperationType())
                .snapshotTime(dto.getSnapshotTime())
                .operator(dto.getOperator())
                .code(dto.getCode())
                .name(dto.getName())
                .anchorType(dto.getAnchorType())
                .anchorCode(dto.getAnchorCode())
                .baselineVersion(dto.getBaselineVersion())
                .baselineStatus(dto.getBaselineStatus())
                .releasedAt(dto.getReleasedAt())
                .releasedBy(dto.getReleasedBy())
                .supersededByCode(dto.getSupersededByCode())
                .description(dto.getDescription())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .forceDelete(dto.getForceDelete())
                .itemsSnapshot(dto.getItemsSnapshot() != null ? dto.getItemsSnapshot().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public SoftwareBaselineItemResponse toItemResponse(SoftwareBaselineItemDto dto) {
        if (dto == null) {
            return null;
        }
        return SoftwareBaselineItemResponse.builder()
                .partCode(dto.getPartCode())
                .vehicleNodeCode(dto.getVehicleNodeCode())
                .remark(dto.getRemark())
                .build();
    }
}
