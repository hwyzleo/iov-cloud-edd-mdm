package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.assembler.PlantDomainAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PlantQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.PlantDeletionDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工厂应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlantAppService {

    private final PlantRepository plantRepository;
    private final PlantDeletionDomainService plantDeletionDomainService;
    private final OutboxService outboxService;

    /**
     * 创建工厂
     *
     * @param cmd 创建命令
     * @return 工厂DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlantDto createPlant(PlantCreateCmd cmd) {
        log.info("创建工厂: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        Plant plant = Plant.create(
                cmd.getCode(), cmd.getName(), cmd.getNameEn(), cmd.getShortName(), cmd.getDescription(),
                PlantType.valueOf(cmd.getPlantType()), cmd.getLegalEntityCode(), cmd.getCostCenterCode(),
                cmd.getCountry(), cmd.getProvince(), cmd.getCity(), cmd.getAddress(),
                cmd.getLongitude(), cmd.getLatitude(), cmd.getTimezone(),
                cmd.getAnnualCapacity(), cmd.getProductionLines(), cmd.getOperationalStartDate(), cmd.getMesInstance(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );

        plant = plantRepository.save(plant, "CREATE");

        outboxService.publishPlantCreatedEvent(plant);

        return PlantDomainAssembler.toDto(plant);
    }

    /**
     * 更新工厂
     *
     * @param cmd 更新命令
     * @return 工厂DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlantDto updatePlant(PlantUpdateCmd cmd) {
        log.info("更新工厂: {}", cmd.getCode());

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Plant plant = plantRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("工厂不存在: " + cmd.getCode()));

        plant.update(
                cmd.getName(), cmd.getNameEn(), cmd.getShortName(), cmd.getDescription(),
                PlantType.valueOf(cmd.getPlantType()), cmd.getLegalEntityCode(), cmd.getCostCenterCode(),
                cmd.getCountry(), cmd.getProvince(), cmd.getCity(), cmd.getAddress(),
                cmd.getLongitude(), cmd.getLatitude(), cmd.getTimezone(),
                cmd.getAnnualCapacity(), cmd.getProductionLines(), cmd.getOperationalStartDate(), cmd.getMesInstance(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy
        );

        plant = plantRepository.save(plant, "UPDATE");

        outboxService.publishPlantUpdatedEvent(plant);

        return PlantDomainAssembler.toDto(plant);
    }

    /**
     * 失效工厂
     *
     * @param code     工厂code
     * @param modifyBy 修改人
     * @return 工厂DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlantDto deactivatePlant(String code, String modifyBy) {
        log.info("失效工厂: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        Plant plant = plantRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("工厂不存在: " + code));

        plant.deactivate(modifyBy);

        plant = plantRepository.save(plant, "DEACTIVATE");

        outboxService.publishPlantUpdatedEvent(plant);

        return PlantDomainAssembler.toDto(plant);
    }

    /**
     * 删除工厂
     *
     * @param code        工厂code
     * @param operator    操作人
     * @param forceDelete 是否强制删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePlant(String code, String operator, boolean forceDelete) {
        log.info("删除工厂: {} forceDelete={}", code, forceDelete);

        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }

        Plant plant = plantRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("工厂不存在: " + code));

        plantDeletionDomainService.checkAndDelete(plant, operator, forceDelete);

        outboxService.publishPlantDeletedEvent(plant, forceDelete);
    }

    /**
     * 根据code获取工厂
     *
     * @param code 工厂code
     * @return 工厂DTO
     */
    public PlantDto getPlantByCode(String code) {
        Plant plant = plantRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("工厂不存在: " + code));
        return PlantDomainAssembler.toDto(plant);
    }

    /**
     * 分页查询工厂列表
     *
     * @param query 查询条件
     * @return 工厂DTO列表
     */
    public List<PlantDto> listPlants(PlantQuery query) {
        List<Plant> plants = plantRepository.list(
                query.getPlantType(), query.getCountry(), null, query.getPage(), query.getSize()
        );
        return plants.stream()
                .map(PlantDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询工厂总数
     *
     * @param plantType       工厂类型
     * @param country         国家
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countPlants(String plantType, String country, boolean includeInactive) {
        String status = includeInactive ? null : "ACTIVE";
        return plantRepository.count(plantType, country, status);
    }

    /**
     * 查询所有ACTIVE工厂
     *
     * @return 工厂DTO列表
     */
    public List<PlantDto> listAllActivePlants() {
        List<Plant> plants = plantRepository.listAllActive();
        return plants.stream()
                .map(PlantDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取工厂全量快照
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 工厂DTO列表
     */
    public List<PlantDto> snapshot(boolean includeInactive, int page, int size) {
        List<Plant> plants = plantRepository.snapshot(includeInactive, page, size);
        return plants.stream()
                .map(PlantDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取工厂快照总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long snapshotCount(boolean includeInactive) {
        return plantRepository.snapshotCount(includeInactive);
    }

    /**
     * 按工厂类型查询工厂列表
     *
     * @param plantType 工厂类型
     * @return 工厂DTO列表
     */
    public List<PlantDto> listByPlantType(String plantType) {
        List<Plant> plants = plantRepository.listByPlantType(plantType);
        return plants.stream()
                .map(PlantDomainAssembler::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询工厂历史版本列表
     *
     * @param code 工厂code
     * @return 历史版本DTO列表
     */
    public List<PlantHistoryDto> listPlantHistory(String code) {
        List<PlantHistory> historyList = plantRepository.findHistoryByCode(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 工厂历史版本实体
     * @return 工厂历史版本DTO
     */
    private PlantHistoryDto convertHistoryToDto(PlantHistory history) {
        return PlantHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameEn(history.getNameEn())
                .shortName(history.getShortName())
                .description(history.getDescription())
                .plantType(history.getPlantType() != null ? history.getPlantType().name() : null)
                .legalEntityCode(history.getLegalEntityCode())
                .costCenterCode(history.getCostCenterCode())
                .country(history.getCountry())
                .province(history.getProvince())
                .city(history.getCity())
                .address(history.getAddress())
                .longitude(history.getLongitude())
                .latitude(history.getLatitude())
                .timezone(history.getTimezone())
                .annualCapacity(history.getAnnualCapacity())
                .productionLines(history.getProductionLines())
                .operationalStartDate(history.getOperationalStartDate())
                .mesInstance(history.getMesInstance())
                .sourceSystem(history.getSourceSystem())
                .sourceId(history.getSourceId())
                .sourceVersion(history.getSourceVersion())
                .ingestionChannel(history.getIngestionChannel())
                .ingestionTime(history.getIngestionTime())
                .sourcePayloadHash(history.getSourcePayloadHash())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus() != null ? history.getStatus().name() : null)
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .createBy(history.getCreateBy())
                .createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy())
                .modifyTime(history.getModifyTime())
                .build();
    }
}
