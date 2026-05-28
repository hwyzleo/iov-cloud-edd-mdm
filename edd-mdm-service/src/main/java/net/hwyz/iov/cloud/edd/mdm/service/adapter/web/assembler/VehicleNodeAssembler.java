package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import org.springframework.stereotype.Component;

/**
 * 车载节点Assembler
 *
 * @author hwyz_leo
 */
@Component
public class VehicleNodeAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 车载节点DTO
     * @return 车载节点响应对象
     */
    public VehicleNodeResponse toResponse(VehicleNodeDto dto) {
        if (dto == null) {
            return null;
        }
        return VehicleNodeResponse.builder()
                .nodeCode(dto.getNodeCode())
                .name(dto.getName())
                .nameLocal(dto.getNameLocal())
                .description(dto.getDescription())
                .nodeType(dto.getNodeType())
                .functionalDomain(dto.getFunctionalDomain())
                .deviceCategory(dto.getDeviceCategory())
                .isCoreNode(dto.getIsCoreNode())
                .otaSupportType(dto.getOtaSupportType())
                .hsmCapability(dto.getHsmCapability())
                .securityLevel(dto.getSecurityLevel())
                .source(dto.getSource())
                .externalRefId(dto.getExternalRefId())
                .externalVersion(dto.getExternalVersion())
                .lastSyncTime(dto.getLastSyncTime())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }
}
