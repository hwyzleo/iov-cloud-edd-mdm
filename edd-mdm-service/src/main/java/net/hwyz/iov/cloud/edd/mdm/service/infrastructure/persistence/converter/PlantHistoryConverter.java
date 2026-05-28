package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlantHistoryPo;
import org.springframework.stereotype.Component;

/**
 * 工厂历史快照转换器
 *
 * @author hwyz_leo
 */
@Component
public class PlantHistoryConverter {

    public PlantHistory toDomain(PlantHistoryPo po) {
        if (po == null) {
            return null;
        }
        return PlantHistory.builder()
                .snapshotId(po.getSnapshotId())
                .entityId(po.getEntityId())
                .operationType(po.getOperationType())
                .snapshotTime(po.getSnapshotTime())
                .operator(po.getOperator())
                .code(po.getCode())
                .name(po.getName())
                .nameEn(po.getNameEn())
                .shortName(po.getShortName())
                .description(po.getDescription())
                .plantType(po.getPlantType() != null ? PlantType.valueOf(po.getPlantType()) : null)
                .legalEntityCode(po.getLegalEntityCode())
                .costCenterCode(po.getCostCenterCode())
                .country(po.getCountry())
                .province(po.getProvince())
                .city(po.getCity())
                .address(po.getAddress())
                .longitude(po.getLongitude())
                .latitude(po.getLatitude())
                .timezone(po.getTimezone())
                .annualCapacity(po.getAnnualCapacity())
                .productionLines(po.getProductionLines())
                .operationalStartDate(po.getOperationalStartDate())
                .mesInstance(po.getMesInstance())
                .sourceSystem(po.getSourceSystem())
                .sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .ingestionChannel(po.getIngestionChannel())
                .ingestionTime(po.getIngestionTime())
                .sourcePayloadHash(po.getSourcePayloadHash())
                .version(po.getVersion())
                .effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(PlantStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy())
                .modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }
}
