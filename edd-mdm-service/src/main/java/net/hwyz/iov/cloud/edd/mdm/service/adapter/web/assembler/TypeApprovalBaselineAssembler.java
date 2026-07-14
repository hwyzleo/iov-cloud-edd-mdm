package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TaBaselineItemResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TaBaselineItemDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TypeApprovalBaselineDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 型式批准基线响应组装器
 *
 * @author hwyz_leo
 */
@Component
public class TypeApprovalBaselineAssembler {

    public TypeApprovalBaselineResponse toResponse(TypeApprovalBaselineDto dto) {
        if (dto == null) {
            return null;
        }
        return TypeApprovalBaselineResponse.builder()
                .id(dto.getId())
                .taBaselineCode(dto.getTaBaselineCode())
                .swinCode(dto.getSwinCode())
                .anchorType(dto.getAnchorType())
                .anchorCode(dto.getAnchorCode())
                .status(dto.getStatus())
                .projectionDigest(dto.getProjectionDigest())
                .sourceBaselineScope(dto.getSourceBaselineScope())
                .effectiveFrom(dto.getEffectiveFrom())
                .remark(dto.getRemark())
                .version(dto.getVersion())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .items(dto.getItems() != null ? dto.getItems().stream()
                        .map(this::toItemResponse)
                        .collect(Collectors.toList()) : null)
                .build();
    }

    public TaBaselineItemResponse toItemResponse(TaBaselineItemDto dto) {
        if (dto == null) {
            return null;
        }
        return TaBaselineItemResponse.builder()
                .id(dto.getId())
                .taBaselineId(dto.getTaBaselineId())
                .vehicleNodeCode(dto.getVehicleNodeCode())
                .partCode(dto.getPartCode())
                .approvedVersion(dto.getApprovedVersion())
                .sourceBaselineCode(dto.getSourceBaselineCode())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .build();
    }

    public List<TypeApprovalBaselineResponse> toResponseList(List<TypeApprovalBaselineDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TypeApprovalBaselinePageResponse toPageResponse(List<TypeApprovalBaselineDto> dtos, long total) {
        List<TypeApprovalBaselineResponse> list = dtos != null ? dtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()) : List.of();
        return TypeApprovalBaselinePageResponse.builder()
                .list(list)
                .total(total)
                .build();
    }
}
