package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.assembler.DeviceCategoryDomainAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.DeviceCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.DeviceCategoryHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.DeviceCategoryHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.DeviceCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VehicleNodeRepository;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类别应用服务（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceCategoryAppService {

    private final DeviceCategoryRepository deviceCategoryRepository;
    private final VehicleNodeRepository vehicleNodeRepository;
    private final OutboxService outboxService;

    @Transactional(rollbackFor = Exception.class)
    public DeviceCategoryDto createDeviceCategory(DeviceCategoryCreateCmd cmd) {
        log.info("创建设备类别: {}", cmd.getCode());
        if (deviceCategoryRepository.existsByCode(cmd.getCode())) {
            throw new DeviceCategoryDuplicateCodeException(cmd.getCode(), "ACTIVE");
        }
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        DeviceCategory category = DeviceCategory.create(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getSortOrder(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy
        );
        category = deviceCategoryRepository.save(category, "CREATE");
        outboxService.publishDeviceCategoryCreatedEvent(category);
        return DeviceCategoryDomainAssembler.toDto(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceCategoryDto updateDeviceCategory(DeviceCategoryUpdateCmd cmd) {
        log.info("更新设备类别: {}", cmd.getCode());
        DeviceCategory category = deviceCategoryRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new DeviceCategoryNotExistException(cmd.getCode()));
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        category.update(cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getSortOrder(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        category = deviceCategoryRepository.save(category, "UPDATE");
        outboxService.publishDeviceCategoryUpdatedEvent(category);
        return DeviceCategoryDomainAssembler.toDto(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public DeviceCategoryDto deactivateDeviceCategory(String code, String modifyBy) {
        log.info("失效设备类别: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        DeviceCategory category = deviceCategoryRepository.findByCode(code)
                .orElseThrow(() -> new DeviceCategoryNotExistException(code));
        category.deactivate(modifyBy);
        category = deviceCategoryRepository.save(category, "DEACTIVATE");
        outboxService.publishDeviceCategoryUpdatedEvent(category);
        return DeviceCategoryDomainAssembler.toDto(category);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceCategory(String code, String operator) {
        log.info("删除设备类别: {}", code);
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        DeviceCategory category = deviceCategoryRepository.findByCode(code)
                .orElseThrow(() -> new DeviceCategoryNotExistException(code));
        long referenceCount = vehicleNodeRepository.countByDeviceCategory(code);
        if (referenceCount > 0) {
            throw new DeviceCategoryHasReferenceException(code, referenceCount);
        }
        deviceCategoryRepository.delete(category, operator);
        outboxService.publishDeviceCategoryDeletedEvent(category, false);
    }

    public DeviceCategoryDto getDeviceCategoryByCode(String code) {
        DeviceCategory category = deviceCategoryRepository.findByCode(code)
                .orElseThrow(() -> new DeviceCategoryNotExistException(code));
        return DeviceCategoryDomainAssembler.toDto(category);
    }

    public List<DeviceCategoryDto> listDeviceCategories(DeviceCategoryQuery query) {
        String status = query.isIncludeInactive() ? null : "ACTIVE";
        List<DeviceCategory> categories = deviceCategoryRepository.list(status, query.getPage(), query.getSize());
        return categories.stream().map(DeviceCategoryDomainAssembler::toDto).collect(Collectors.toList());
    }

    public long countDeviceCategories(boolean includeInactive) {
        String status = includeInactive ? null : "ACTIVE";
        return deviceCategoryRepository.count(status);
    }

    public List<DeviceCategoryDto> listAllActiveDeviceCategories() {
        List<DeviceCategory> categories = deviceCategoryRepository.listAllActive();
        return categories.stream().map(DeviceCategoryDomainAssembler::toDto).collect(Collectors.toList());
    }

    public List<DeviceCategoryHistoryDto> listDeviceCategoryHistory(String code) {
        List<DeviceCategoryHistory> historyList = deviceCategoryRepository.findHistoryByCode(code);
        return historyList.stream().map(DeviceCategoryDomainAssembler::toHistoryDto).collect(Collectors.toList());
    }
}
