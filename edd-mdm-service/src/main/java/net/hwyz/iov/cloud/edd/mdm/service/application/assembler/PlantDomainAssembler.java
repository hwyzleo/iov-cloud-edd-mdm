package net.hwyz.iov.cloud.edd.mdm.service.application.assembler;

import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;

/**
 * 工厂领域Assembler
 *
 * @author hwyz_leo
 */
public class PlantDomainAssembler {

    private PlantDomainAssembler() {
    }

    /**
     * 创建命令转换为领域对象
     *
     * @param cmd      创建命令
     * @param createBy 创建人
     * @return 工厂聚合根
     */
    public static Plant toDomain(PlantCreateCmd cmd, String createBy) {
        return Plant.create(
                cmd.getCode(), cmd.getName(), cmd.getNameEn(), cmd.getShortName(), cmd.getDescription(),
                PlantType.valueOf(cmd.getPlantType()), cmd.getLegalEntityCode(), cmd.getCostCenterCode(),
                cmd.getCountry(), cmd.getProvince(), cmd.getCity(), cmd.getAddress(),
                cmd.getLongitude(), cmd.getLatitude(), cmd.getTimezone(),
                cmd.getAnnualCapacity(), cmd.getProductionLines(), cmd.getOperationalStartDate(), cmd.getMesInstance(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );
    }

    /**
     * 领域对象转换为DTO
     *
     * @param plant 工厂聚合根
     * @return 工厂DTO
     */
    public static PlantDto toDto(Plant plant) {
        return PlantDto.builder()
                .id(plant.getId())
                .code(plant.getCode())
                .name(plant.getName())
                .nameEn(plant.getNameEn())
                .shortName(plant.getShortName())
                .description(plant.getDescription())
                .plantType(plant.getPlantType() != null ? plant.getPlantType().name() : null)
                .legalEntityCode(plant.getLegalEntityCode())
                .costCenterCode(plant.getCostCenterCode())
                .country(plant.getCountry())
                .province(plant.getProvince())
                .city(plant.getCity())
                .address(plant.getAddress())
                .longitude(plant.getLongitude())
                .latitude(plant.getLatitude())
                .timezone(plant.getTimezone())
                .annualCapacity(plant.getAnnualCapacity())
                .productionLines(plant.getProductionLines())
                .operationalStartDate(plant.getOperationalStartDate())
                .mesInstance(plant.getMesInstance())
                .sourceSystem(plant.getSourceSystem())
                .sourceId(plant.getSourceId())
                .sourceVersion(plant.getSourceVersion())
                .ingestionChannel(plant.getIngestionChannel())
                .ingestionTime(plant.getIngestionTime())
                .sourcePayloadHash(plant.getSourcePayloadHash())
                .version(plant.getVersion())
                .effectiveFrom(plant.getEffectiveFrom())
                .effectiveTo(plant.getEffectiveTo())
                .status(plant.getStatus().name())
                .createBy(plant.getCreateBy())
                .createTime(plant.getCreateTime())
                .modifyBy(plant.getModifyBy())
                .modifyTime(plant.getModifyTime())
                .build();
    }
}
