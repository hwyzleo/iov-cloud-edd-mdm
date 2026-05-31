package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionFamilyQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.OptionFamilyCategoryInvalidException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionFamilyHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项族应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptionFamilyAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    @Transactional(rollbackFor = Exception.class)
    public OptionFamilyDto createOptionFamily(OptionFamilyCreateCmd cmd) {
        log.info("创建选项族: {}", cmd.getCode());
        OptionFamilyCategory category = parseAndValidateCategory(cmd.getCategory());
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        OptionFamily optionFamily = productDomainService.createOptionFamily(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                category, cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        outboxService.publishOptionFamilyCreatedEvent(optionFamily);
        return convertToDto(optionFamily);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionFamilyDto updateOptionFamily(OptionFamilyUpdateCmd cmd) {
        log.info("更新选项族: {}", cmd.getCode());
        OptionFamilyCategory category = parseAndValidateCategory(cmd.getCategory());
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        OptionFamily optionFamily = productDomainService.updateOptionFamily(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                category, cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        outboxService.publishOptionFamilyUpdatedEvent(optionFamily);
        return convertToDto(optionFamily);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionFamilyDto deactivateOptionFamily(String code, String modifyBy) {
        log.info("失效选项族: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        OptionFamily optionFamily = productDomainService.deactivateOptionFamily(code, modifyBy);
        outboxService.publishOptionFamilyDeactivatedEvent(optionFamily);
        return convertToDto(optionFamily);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOptionFamily(String code, String modifyBy) {
        log.info("删除选项族: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        productDomainService.deleteOptionFamily(code, modifyBy);
    }

    public OptionFamilyDto getOptionFamilyByCode(String code) {
        OptionFamily optionFamily = productDomainService.getOptionFamilyByCode(code);
        return convertToDto(optionFamily);
    }

    public List<OptionFamilyDto> listOptionFamily(OptionFamilyQuery query) {
        List<OptionFamily> list = productDomainService.listOptionFamilies(
                query.getPage(), query.getSize(), query.isIncludeInactive(), query.getCategory());
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countOptionFamily(boolean includeInactive, String category) {
        return productDomainService.countOptionFamilies(includeInactive, category);
    }

    public List<OptionFamilyHistoryDto> listOptionFamilyHistory(String code) {
        List<OptionFamilyHistory> historyList = productDomainService.listOptionFamilyHistory(code);
        return historyList.stream().map(this::convertHistoryToDto).collect(Collectors.toList());
    }

    private OptionFamilyDto convertToDto(OptionFamily optionFamily) {
        return OptionFamilyDto.builder()
                .id(optionFamily.getId())
                .code(optionFamily.getCode())
                .name(optionFamily.getName())
                .nameLocal(optionFamily.getNameLocal())
                .description(optionFamily.getDescription())
                .category(optionFamily.getCategory() != null ? optionFamily.getCategory().name() : null)
                .sourceSystem(optionFamily.getSourceSystem())
                .sourceId(optionFamily.getSourceId())
                .sourceVersion(optionFamily.getSourceVersion())
                .ingestionChannel(optionFamily.getIngestionChannel())
                .ingestionTime(optionFamily.getIngestionTime())
                .sourcePayloadHash(optionFamily.getSourcePayloadHash())
                .version(optionFamily.getVersion())
                .effectiveFrom(optionFamily.getEffectiveFrom())
                .effectiveTo(optionFamily.getEffectiveTo())
                .status(optionFamily.getStatus().name())
                .createBy(optionFamily.getCreateBy())
                .createTime(optionFamily.getCreateTime())
                .modifyBy(optionFamily.getModifyBy())
                .modifyTime(optionFamily.getModifyTime())
                .build();
    }

    private OptionFamilyHistoryDto convertHistoryToDto(OptionFamilyHistory history) {
        return OptionFamilyHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .description(history.getDescription())
                .category(history.getCategory())
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

    /**
     * 解析并校验 category 枚举值
     *
     * @param categoryStr category 字符串
     * @return OptionFamilyCategory 枚举值
     * @throws OptionFamilyCategoryInvalidException 当 category 为空或取值不在枚举范围时
     */
    private OptionFamilyCategory parseAndValidateCategory(String categoryStr) {
        if (categoryStr == null || categoryStr.isBlank()) {
            throw new OptionFamilyCategoryInvalidException(categoryStr);
        }
        try {
            return OptionFamilyCategory.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            throw new OptionFamilyCategoryInvalidException(categoryStr);
        }
    }
}
