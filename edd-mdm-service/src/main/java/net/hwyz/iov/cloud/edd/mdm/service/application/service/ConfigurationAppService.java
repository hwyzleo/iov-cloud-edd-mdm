package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationBindOptionCodeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ConfigurationQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ConfigurationHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;
    private final ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationDto createConfiguration(ConfigurationCreateCmd cmd) {
        log.info("创建配置: {}", cmd.getCode());
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        Configuration configuration = productDomainService.createConfiguration(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getVariantCode(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        outboxService.publishConfigurationCreatedEvent(configuration);
        return convertToDto(configuration);
    }

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationDto updateConfiguration(ConfigurationUpdateCmd cmd) {
        log.info("更新配置: {}", cmd.getCode());
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Configuration configuration = productDomainService.updateConfiguration(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getDescription(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        outboxService.publishConfigurationUpdatedEvent(configuration);
        return convertToDto(configuration);
    }

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationDto deactivateConfiguration(String code, String modifyBy) {
        log.info("失效配置: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Configuration configuration = productDomainService.deactivateConfiguration(code, modifyBy);
        outboxService.publishConfigurationDeactivatedEvent(configuration);
        return convertToDto(configuration);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteConfiguration(String code, String modifyBy) {
        log.info("删除配置: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        productDomainService.deleteConfiguration(code, modifyBy);
    }

    public ConfigurationDto getConfigurationByCode(String code) {
        Configuration configuration = productDomainService.getConfigurationByCode(code);
        return convertToDto(configuration);
    }

    public List<ConfigurationDto> listConfigurations(ConfigurationQuery query) {
        List<Configuration> list = productDomainService.listConfigurations(
                query.getPage(), query.getSize(), query.getVariantCode(), query.isIncludeInactive());
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countConfigurations(String variantCode, boolean includeInactive) {
        return productDomainService.countConfigurations(variantCode, includeInactive);
    }

    public List<ConfigurationHistoryDto> listConfigurationHistory(String code) {
        List<ConfigurationHistory> historyList = productDomainService.listConfigurationHistory(code);
        return historyList.stream().map(this::convertHistoryToDto).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindOptionCode(ConfigurationBindOptionCodeCmd cmd) {
        log.info("配置[{}]绑定选项码[{}]", cmd.getConfigurationCode(), cmd.getOptionCodeCode());
        String operator = cmd.getOperator();
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        productDomainService.bindConfigurationOptionCode(
                cmd.getConfigurationCode(), cmd.getOptionCodeCode(), cmd.getOptionFamilyCode(), operator);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unbindOptionCode(String configurationCode, String optionCodeCode, String operator) {
        log.info("配置[{}]解绑选项码[{}]", configurationCode, optionCodeCode);
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        productDomainService.unbindConfigurationOptionCode(configurationCode, optionCodeCode, operator);
    }

    public List<ConfigurationOptionCodeBindingPo> listOptionCodes(String configurationCode) {
        return configurationOptionCodeBindingRepository.findByConfigurationCode(configurationCode);
    }

    public List<ConfigurationDto> findByOptionCodes(List<String> optionCodes) {
        List<Configuration> list = productDomainService.findConfigurationsByOptionCodes(optionCodes);
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ConfigurationDto convertToDto(Configuration configuration) {
        return ConfigurationDto.builder()
                .id(configuration.getId())
                .code(configuration.getCode())
                .name(configuration.getName())
                .nameLocal(configuration.getNameLocal())
                .variantCode(configuration.getVariantCode())
                .description(configuration.getDescription())
                .sourceSystem(configuration.getSourceSystem())
                .sourceId(configuration.getSourceId())
                .sourceVersion(configuration.getSourceVersion())
                .ingestionChannel(configuration.getIngestionChannel())
                .ingestionTime(configuration.getIngestionTime())
                .sourcePayloadHash(configuration.getSourcePayloadHash())
                .version(configuration.getVersion())
                .effectiveFrom(configuration.getEffectiveFrom())
                .effectiveTo(configuration.getEffectiveTo())
                .status(configuration.getStatus().name())
                .createBy(configuration.getCreateBy())
                .createTime(configuration.getCreateTime())
                .modifyBy(configuration.getModifyBy())
                .modifyTime(configuration.getModifyTime())
                .build();
    }

    private ConfigurationHistoryDto convertHistoryToDto(ConfigurationHistory history) {
        return ConfigurationHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .variantCode(history.getVariantCode())
                .description(history.getDescription())
                .sourceSystem(history.getSourceSystem())
                .sourceId(history.getSourceId())
                .sourceVersion(history.getSourceVersion())
                .ingestionChannel(history.getIngestionChannel())
                .ingestionTime(history.getIngestionTime())
                .sourcePayloadHash(history.getSourcePayloadHash())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus())
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
