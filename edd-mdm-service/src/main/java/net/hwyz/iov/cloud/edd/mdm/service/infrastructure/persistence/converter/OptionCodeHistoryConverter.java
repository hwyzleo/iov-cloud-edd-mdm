package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionCodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OptionCodeHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 选项码历史版本转换器
 *
 * @author hwyz_leo
 */
@Component
public class OptionCodeHistoryConverter {

    public OptionCodeHistory toDomain(OptionCodeHistoryPo po) {
        if (po == null) {
            return null;
        }
        return OptionCodeHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .optionFamilyCode(po.getOptionFamilyCode())
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
