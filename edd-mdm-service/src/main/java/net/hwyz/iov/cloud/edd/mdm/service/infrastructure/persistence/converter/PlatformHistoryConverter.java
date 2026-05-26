package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlatformHistory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlatformHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 平台历史版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class PlatformHistoryConverter {

    /**
     * 持久化对象转换为领域实体
     *
     * @param po 持久化对象
     * @return 领域实体
     */
    public PlatformHistory toDomain(PlatformHistoryPo po) {
        if (po == null) {
            return null;
        }
        return PlatformHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .platformType(po.getPlatformType())
                .architecture(po.getArchitecture())
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
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .build();
    }
}
