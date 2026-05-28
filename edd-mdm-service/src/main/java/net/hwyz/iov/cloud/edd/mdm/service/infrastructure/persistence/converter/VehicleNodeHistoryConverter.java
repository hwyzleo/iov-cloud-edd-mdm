package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VehicleNodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VehicleNodeHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 车载节点历史版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class VehicleNodeHistoryConverter {

    public VehicleNodeHistory toDomain(VehicleNodeHistoryPo po) {
        if (po == null) {
            return null;
        }
        return VehicleNodeHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .nodeCode(po.getNodeCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .nodeType(po.getNodeType())
                .functionalDomain(po.getFunctionalDomain())
                .deviceCategory(po.getDeviceCategory())
                .isCoreNode(po.getIsCoreNode())
                .otaSupportType(po.getOtaSupportType())
                .hsmCapability(po.getHsmCapability())
                .securityLevel(po.getSecurityLevel())
                .source(po.getSource())
                .externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion())
                .lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(po.getStatus())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .forceDelete(po.getForceDelete())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }
}
