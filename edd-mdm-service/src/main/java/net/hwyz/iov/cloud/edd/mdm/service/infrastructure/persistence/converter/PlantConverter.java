package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PlantPo;
import org.springframework.stereotype.Component;

/**
 * 工厂转换器
 *
 * @author hwyz_leo
 */
@Component
public class PlantConverter {

    public Plant toDomain(PlantPo po) {
        if (po == null) {
            return null;
        }
        return Plant.builder()
                .id(po.getId())
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

    public PlantPo toPo(Plant domain) {
        if (domain == null) {
            return null;
        }
        return PlantPo.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .name(domain.getName())
                .nameEn(domain.getNameEn())
                .shortName(domain.getShortName())
                .description(domain.getDescription())
                .plantType(domain.getPlantType() != null ? domain.getPlantType().name() : null)
                .legalEntityCode(domain.getLegalEntityCode())
                .costCenterCode(domain.getCostCenterCode())
                .country(domain.getCountry())
                .province(domain.getProvince())
                .city(domain.getCity())
                .address(domain.getAddress())
                .longitude(domain.getLongitude())
                .latitude(domain.getLatitude())
                .timezone(domain.getTimezone())
                .annualCapacity(domain.getAnnualCapacity())
                .productionLines(domain.getProductionLines())
                .operationalStartDate(domain.getOperationalStartDate())
                .mesInstance(domain.getMesInstance())
                .sourceSystem(domain.getSourceSystem())
                .sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .ingestionChannel(domain.getIngestionChannel())
                .ingestionTime(domain.getIngestionTime())
                .sourcePayloadHash(domain.getSourcePayloadHash())
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
