package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SeriesHistory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SeriesHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 车系历史版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class SeriesHistoryConverter {

    /**
     * 持久化对象转换为领域实体
     *
     * @param po 持久化对象
     * @return 领域实体
     */
    public SeriesHistory toDomain(SeriesHistoryPo po) {
        if (po == null) {
            return null;
        }
        return SeriesHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .brandCode(po.getBrandCode())
                .seriesType(po.getSeriesType())
                .lifecycleStatus(po.getLifecycleStatus())
                .targetMarket(po.getTargetMarket())
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
