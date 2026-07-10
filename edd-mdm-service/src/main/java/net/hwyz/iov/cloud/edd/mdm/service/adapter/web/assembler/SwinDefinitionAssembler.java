package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinManagedSystemResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinDefinitionDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinManagedSystemDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN定义 Assembler（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Component
public class SwinDefinitionAssembler {

    public SwinDefinitionResponse toResponse(SwinDefinitionDto dto) {
        if (dto == null) {
            return null;
        }
        List<SwinManagedSystemResponse> managedSystems = null;
        if (dto.getManagedSystems() != null) {
            managedSystems = dto.getManagedSystems().stream()
                    .map(this::toManagedSystemResponse)
                    .collect(Collectors.toList());
        }
        return SwinDefinitionResponse.builder()
                .id(dto.getId()).swinCode(dto.getSwinCode()).schemeCode(dto.getSchemeCode())
                .typeRefType(dto.getTypeRefType()).typeRefCode(dto.getTypeRefCode())
                .name(dto.getName()).nameLocal(dto.getNameLocal()).description(dto.getDescription())
                .version(dto.getVersion()).status(dto.getStatus())
                .createBy(dto.getCreateBy()).createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy()).modifyTime(dto.getModifyTime())
                .managedSystems(managedSystems)
                .build();
    }

    private SwinManagedSystemResponse toManagedSystemResponse(SwinManagedSystemDto dto) {
        if (dto == null) {
            return null;
        }
        return SwinManagedSystemResponse.builder()
                .id(dto.getId()).swinCode(dto.getSwinCode()).vehicleNodeCode(dto.getVehicleNodeCode())
                .isTypeApprovalRelevant(dto.getIsTypeApprovalRelevant())
                .approvedSoftwareBaseline(dto.getApprovedSoftwareBaseline())
                .createBy(dto.getCreateBy()).createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy()).modifyTime(dto.getModifyTime())
                .build();
    }
}
