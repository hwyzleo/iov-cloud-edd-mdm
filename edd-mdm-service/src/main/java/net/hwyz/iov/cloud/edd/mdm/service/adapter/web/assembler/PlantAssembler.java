package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantHistoryDto;
import org.springframework.stereotype.Component;

/**
 * 工厂Assembler
 *
 * @author hwyz_leo
 */
@Component
public class PlantAssembler {

    /**
     * DTO转换为响应对象
     *
     * @param dto 工厂DTO
     * @return 工厂响应对象
     */
    public PlantResponse toResponse(PlantDto dto) {
        if (dto == null) {
            return null;
        }
        return PlantResponse.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameEn(dto.getNameEn())
                .shortName(dto.getShortName())
                .description(dto.getDescription())
                .plantType(dto.getPlantType())
                .legalEntityCode(dto.getLegalEntityCode())
                .costCenterCode(dto.getCostCenterCode())
                .country(dto.getCountry())
                .province(dto.getProvince())
                .city(dto.getCity())
                .address(dto.getAddress())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .timezone(dto.getTimezone())
                .annualCapacity(dto.getAnnualCapacity())
                .productionLines(dto.getProductionLines())
                .operationalStartDate(dto.getOperationalStartDate())
                .mesInstance(dto.getMesInstance())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
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

    /**
     * DTO转换为简要响应对象
     *
     * @param dto 工厂DTO
     * @return 工厂简要响应对象
     */
    public PlantBriefResponse toBriefResponse(PlantDto dto) {
        if (dto == null) {
            return null;
        }
        return PlantBriefResponse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .plantType(dto.getPlantType())
                .country(dto.getCountry())
                .city(dto.getCity())
                .status(dto.getStatus())
                .build();
    }

    /**
     * 历史版本DTO转换为响应对象
     *
     * @param dto 工厂历史版本DTO
     * @return 工厂历史版本响应对象
     */
    public PlantHistoryResponse toHistoryResponse(PlantHistoryDto dto) {
        if (dto == null) {
            return null;
        }
        return PlantHistoryResponse.builder()
                .snapshotId(dto.getSnapshotId())
                .entityId(dto.getEntityId())
                .code(dto.getCode())
                .name(dto.getName())
                .nameEn(dto.getNameEn())
                .shortName(dto.getShortName())
                .description(dto.getDescription())
                .plantType(dto.getPlantType())
                .legalEntityCode(dto.getLegalEntityCode())
                .costCenterCode(dto.getCostCenterCode())
                .country(dto.getCountry())
                .province(dto.getProvince())
                .city(dto.getCity())
                .address(dto.getAddress())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .timezone(dto.getTimezone())
                .annualCapacity(dto.getAnnualCapacity())
                .productionLines(dto.getProductionLines())
                .operationalStartDate(dto.getOperationalStartDate())
                .mesInstance(dto.getMesInstance())
                .sourceSystem(dto.getSourceSystem())
                .sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion())
                .ingestionChannel(dto.getIngestionChannel())
                .ingestionTime(dto.getIngestionTime())
                .sourcePayloadHash(dto.getSourcePayloadHash())
                .version(dto.getVersion())
                .effectiveFrom(dto.getEffectiveFrom())
                .effectiveTo(dto.getEffectiveTo())
                .status(dto.getStatus())
                .operationType(dto.getOperationType())
                .snapshotTime(dto.getSnapshotTime())
                .operator(dto.getOperator())
                .createBy(dto.getCreateBy())
                .createTime(dto.getCreateTime())
                .modifyBy(dto.getModifyBy())
                .modifyTime(dto.getModifyTime())
                .build();
    }
}
