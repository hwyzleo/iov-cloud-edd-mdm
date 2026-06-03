package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationBindOptionCodeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ConfigurationQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ConfigurationHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private final OptionCodeRepository optionCodeRepository;

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationDto createConfiguration(ConfigurationCreateCmd cmd) {
        log.info("创建配置: variantCode={}, name={}", cmd.getVariantCode(), cmd.getName());
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        // CR-005：code 由系统按 {variantCode}+7位零填充自增序号生成，不再接受外部传入
        Configuration configuration = productDomainService.createConfiguration(
                cmd.getName(), cmd.getNameLocal(),
                cmd.getVariantCode(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        outboxService.publishConfigurationCreatedEvent(configuration);
        return convertToDto(configuration);
    }

    @Transactional(rollbackFor = Exception.class)
    public ConfigurationDto updateConfiguration(String code, ConfigurationUpdateCmd cmd) {
        log.info("更新配置: {}", code);
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        // CR-005：code 来自 path 参数，不可变；cmd 中不再含 code 字段
        Configuration configuration = productDomainService.updateConfiguration(
                code, cmd.getName(), cmd.getNameLocal(),
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

    public List<OptionCodeDto> listOptionCodeDetails(String configurationCode) {
        List<ConfigurationOptionCodeBindingPo> bindings = configurationOptionCodeBindingRepository.findByConfigurationCode(configurationCode);
        if (bindings.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> optionCodeCodes = bindings.stream()
                .map(ConfigurationOptionCodeBindingPo::getOptionCodeCode)
                .collect(Collectors.toList());
        List<OptionCode> optionCodes = optionCodeRepository.findByCodes(optionCodeCodes);
        return optionCodes.stream().map(this::convertOptionCodeToDto).collect(Collectors.toList());
    }

    private OptionCodeDto convertOptionCodeToDto(OptionCode optionCode) {
        return OptionCodeDto.builder()
                .id(optionCode.getId())
                .code(optionCode.getCode())
                .name(optionCode.getName())
                .nameLocal(optionCode.getNameLocal())
                .optionFamilyCode(optionCode.getOptionFamilyCode())
                .description(optionCode.getDescription())
                .sourceSystem(optionCode.getSourceSystem())
                .sourceId(optionCode.getSourceId())
                .sourceVersion(optionCode.getSourceVersion())
                .ingestionChannel(optionCode.getIngestionChannel())
                .ingestionTime(optionCode.getIngestionTime())
                .sourcePayloadHash(optionCode.getSourcePayloadHash())
                .version(optionCode.getVersion())
                .effectiveFrom(optionCode.getEffectiveFrom())
                .effectiveTo(optionCode.getEffectiveTo())
                .status(optionCode.getStatus().name())
                .createBy(optionCode.getCreateBy())
                .createTime(optionCode.getCreateTime())
                .modifyBy(optionCode.getModifyBy())
                .modifyTime(optionCode.getModifyTime())
                .build();
    }

    public List<ConfigurationDto> findByOptionCodes(List<String> optionCodes) {
        List<Configuration> list = productDomainService.findConfigurationsByOptionCodes(optionCodes);
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * 根据版本和选项码组合反查配置code
     *
     * @param variantCode 版本code
     * @param optionCodes 选项码列表
     * @return 匹配的配置code，如果无匹配返回null
     */
    public String resolveConfigurationCode(String variantCode, List<String> optionCodes) {
        return productDomainService.findConfigurationCodeByVariantAndOptionCodes(variantCode, optionCodes);
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
