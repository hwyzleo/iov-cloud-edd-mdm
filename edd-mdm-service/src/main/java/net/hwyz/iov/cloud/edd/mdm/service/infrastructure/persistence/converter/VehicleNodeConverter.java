package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SecurityLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehicleNodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VehicleNodePo;
import org.springframework.stereotype.Component;

/**
 * 车载节点转换器
 *
 * @author hwyz_leo
 */
@Component
public class VehicleNodeConverter {

    public VehicleNode toDomain(VehicleNodePo po) {
        if (po == null) {
            return null;
        }
        return VehicleNode.builder()
                .id(po.getId())
                .nodeCode(po.getNodeCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .nodeType(NodeType.valueOf(po.getNodeType()))
                .functionalDomain(FunctionalDomain.valueOf(po.getFunctionalDomain()))
                .deviceCategory(po.getDeviceCategory())
                .isCoreNode(po.getIsCoreNode())
                .otaSupportType(OtaSupportType.valueOf(po.getOtaSupportType()))
                .hsmCapability(po.getHsmCapability() != null ? HsmCapability.valueOf(po.getHsmCapability()) : null)
                .securityLevel(po.getSecurityLevel() != null ? SecurityLevel.valueOf(po.getSecurityLevel()) : null)
                .source(po.getSource())
                .externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion())
                .lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(VehicleNodeStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }

    public VehicleNodePo toPo(VehicleNode domain) {
        if (domain == null) {
            return null;
        }
        return VehicleNodePo.builder()
                .id(domain.getId())
                .nodeCode(domain.getNodeCode())
                .name(domain.getName())
                .nameLocal(domain.getNameLocal())
                .description(domain.getDescription())
                .nodeType(domain.getNodeType().name())
                .functionalDomain(domain.getFunctionalDomain().name())
                .deviceCategory(domain.getDeviceCategory())
                .isCoreNode(domain.getIsCoreNode())
                .otaSupportType(domain.getOtaSupportType().name())
                .hsmCapability(domain.getHsmCapability() != null ? domain.getHsmCapability().name() : null)
                .securityLevel(domain.getSecurityLevel() != null ? domain.getSecurityLevel().name() : null)
                .source(domain.getSource())
                .externalRefId(domain.getExternalRefId())
                .externalVersion(domain.getExternalVersion())
                .lastSyncTime(domain.getLastSyncTime())
                .version(domain.getVersion())
                .effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo())
                .status(domain.getStatus().name())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy())
                .modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }
}
