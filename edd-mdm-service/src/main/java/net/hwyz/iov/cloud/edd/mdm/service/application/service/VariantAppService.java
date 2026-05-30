package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantBindOptionCodeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VariantQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VariantHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantOptionCodeBindingPo;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版本应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VariantAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;
    private final VariantOptionCodeBindingRepository variantOptionCodeBindingRepository;
    private final OptionCodeRepository optionCodeRepository;

    @Transactional(rollbackFor = Exception.class)
    public VariantDto createVariant(VariantCreateCmd cmd) {
        log.info("创建版本: {}", cmd.getCode());
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        Variant variant = productDomainService.createVariant(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getModelCode(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        outboxService.publishVariantCreatedEvent(variant);
        return convertToDto(variant);
    }

    @Transactional(rollbackFor = Exception.class)
    public VariantDto updateVariant(VariantUpdateCmd cmd) {
        log.info("更新版本: {}", cmd.getCode());
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Variant variant = productDomainService.updateVariant(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getDescription(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        outboxService.publishVariantUpdatedEvent(variant);
        return convertToDto(variant);
    }

    @Transactional(rollbackFor = Exception.class)
    public VariantDto deactivateVariant(String code, String modifyBy) {
        log.info("失效版本: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Variant variant = productDomainService.deactivateVariant(code, modifyBy);
        outboxService.publishVariantDeactivatedEvent(variant);
        return convertToDto(variant);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteVariant(String code, String modifyBy) {
        log.info("删除版本: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        productDomainService.deleteVariant(code, modifyBy);
    }

    public VariantDto getVariantByCode(String code) {
        Variant variant = productDomainService.getVariantByCode(code);
        return convertToDto(variant);
    }

    public List<VariantDto> listVariants(VariantQuery query) {
        List<Variant> variantList = productDomainService.listVariants(
                query.getPage(), query.getSize(),
                query.getModelCode(), query.getCarLineCode(), query.getPlatformCode(),
                query.isIncludeInactive());
        return variantList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countVariants(String modelCode, String carLineCode, String platformCode, boolean includeInactive) {
        return productDomainService.countVariants(modelCode, carLineCode, platformCode, includeInactive);
    }

    public List<VariantHistoryDto> listVariantHistory(String code) {
        List<VariantHistory> historyList = productDomainService.listVariantHistory(code);
        return historyList.stream().map(this::convertHistoryToDto).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindOptionCode(VariantBindOptionCodeCmd cmd) {
        log.info("版本[{}]绑定选项码[{}]", cmd.getVariantCode(), cmd.getOptionCodeCode());
        String operator = cmd.getOperator();
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        productDomainService.bindVariantOptionCode(
                cmd.getVariantCode(), cmd.getOptionCodeCode(), cmd.getOptionFamilyCode(), operator);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unbindOptionCode(String variantCode, String optionCodeCode, String operator) {
        log.info("版本[{}]解绑选项码[{}]", variantCode, optionCodeCode);
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        productDomainService.unbindVariantOptionCode(variantCode, optionCodeCode, operator);
    }

    public List<VariantOptionCodeBindingPo> listOptionCodes(String variantCode) {
        return variantOptionCodeBindingRepository.findByVariantCode(variantCode);
    }

    public List<OptionCodeDto> listOptionCodeDetails(String variantCode) {
        List<VariantOptionCodeBindingPo> bindings = variantOptionCodeBindingRepository.findByVariantCode(variantCode);
        if (bindings.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> optionCodeCodes = bindings.stream()
                .map(VariantOptionCodeBindingPo::getOptionCodeCode)
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

    private VariantDto convertToDto(Variant variant) {
        return VariantDto.builder()
                .id(variant.getId())
                .code(variant.getCode())
                .name(variant.getName())
                .nameLocal(variant.getNameLocal())
                .modelCode(variant.getModelCode())
                .description(variant.getDescription())
                .sourceSystem(variant.getSourceSystem())
                .sourceId(variant.getSourceId())
                .sourceVersion(variant.getSourceVersion())
                .ingestionChannel(variant.getIngestionChannel())
                .ingestionTime(variant.getIngestionTime())
                .sourcePayloadHash(variant.getSourcePayloadHash())
                .version(variant.getVersion())
                .effectiveFrom(variant.getEffectiveFrom())
                .effectiveTo(variant.getEffectiveTo())
                .status(variant.getStatus().name())
                .createBy(variant.getCreateBy())
                .createTime(variant.getCreateTime())
                .modifyBy(variant.getModifyBy())
                .modifyTime(variant.getModifyTime())
                .build();
    }

    private VariantHistoryDto convertHistoryToDto(VariantHistory history) {
        return VariantHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .modelCode(history.getModelCode())
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
