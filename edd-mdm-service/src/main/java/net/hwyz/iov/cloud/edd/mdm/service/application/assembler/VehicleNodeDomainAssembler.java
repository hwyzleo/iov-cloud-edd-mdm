package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SecurityLevel;

/**
 * 车载节点领域组装器
 *
 * @author hwyz_leo
 */
public class VehicleNodeDomainAssembler {

    private VehicleNodeDomainAssembler() {
    }

    /**
     * 创建命令转领域对象
     */
    public static VehicleNode toDomain(VehicleNodeCreateCmd cmd, String createBy) {
        return VehicleNode.create(
                cmd.getNodeCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getNodeType() != null ? NodeType.valueOf(cmd.getNodeType()) : null,
                cmd.getFunctionalDomain() != null ? FunctionalDomain.valueOf(cmd.getFunctionalDomain()) : null,
                cmd.getDeviceCategory(), cmd.getIsCoreNode(),
                cmd.getOtaSupportType() != null ? OtaSupportType.valueOf(cmd.getOtaSupportType()) : null,
                cmd.getHsmCapability() != null ? HsmCapability.valueOf(cmd.getHsmCapability()) : null,
                cmd.getSecurityLevel() != null ? SecurityLevel.valueOf(cmd.getSecurityLevel()) : null,
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );
    }

    /**
     * 领域对象转DTO
     */
    public static VehicleNodeDto toDto(VehicleNode node) {
        return VehicleNodeDto.builder()
                .id(node.getId())
                .nodeCode(node.getNodeCode())
                .name(node.getName())
                .nameLocal(node.getNameLocal())
                .description(node.getDescription())
                .nodeType(node.getNodeType() != null ? node.getNodeType().name() : null)
                .functionalDomain(node.getFunctionalDomain() != null ? node.getFunctionalDomain().name() : null)
                .deviceCategory(node.getDeviceCategory())
                .isCoreNode(node.getIsCoreNode())
                .otaSupportType(node.getOtaSupportType() != null ? node.getOtaSupportType().name() : null)
                .hsmCapability(node.getHsmCapability() != null ? node.getHsmCapability().name() : null)
                .securityLevel(node.getSecurityLevel() != null ? node.getSecurityLevel().name() : null)
                .source(node.getSource())
                .externalRefId(node.getExternalRefId())
                .externalVersion(node.getExternalVersion())
                .lastSyncTime(node.getLastSyncTime())
                .version(node.getVersion())
                .effectiveFrom(node.getEffectiveFrom())
                .effectiveTo(node.getEffectiveTo())
                .status(node.getStatus() != null ? node.getStatus().name() : null)
                .createBy(node.getCreateBy())
                .createTime(node.getCreateTime())
                .modifyBy(node.getModifyBy())
                .modifyTime(node.getModifyTime())
                .build();
    }
}
