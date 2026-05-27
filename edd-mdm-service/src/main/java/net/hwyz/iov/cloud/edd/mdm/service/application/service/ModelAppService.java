package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ModelCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ModelUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ModelQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.ModelHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车型应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    @Transactional(rollbackFor = Exception.class)
    public ModelDto createModel(ModelCreateCmd cmd) {
        log.info("创建车型: {}", cmd.getCode());
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        Model model = productDomainService.createModel(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getCarLineCode(), cmd.getPlatformCode(),
                cmd.getModelYear(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        outboxService.publishModelCreatedEvent(model);
        return convertToDto(model);
    }

    @Transactional(rollbackFor = Exception.class)
    public ModelDto updateModel(ModelUpdateCmd cmd) {
        log.info("更新车型: {}", cmd.getCode());
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Model model = productDomainService.updateModel(
                cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getModelYear(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        outboxService.publishModelUpdatedEvent(model);
        return convertToDto(model);
    }

    @Transactional(rollbackFor = Exception.class)
    public ModelDto deactivateModel(String code, String modifyBy) {
        log.info("失效车型: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        Model model = productDomainService.deactivateModel(code, modifyBy);
        outboxService.publishModelDeactivatedEvent(model);
        return convertToDto(model);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteModel(String code, String modifyBy) {
        log.info("删除车型: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        productDomainService.deleteModel(code, modifyBy);
    }

    public ModelDto getModelByCode(String code) {
        Model model = productDomainService.getModelByCode(code);
        return convertToDto(model);
    }

    public List<ModelDto> listModels(ModelQuery query) {
        List<Model> modelList = productDomainService.listModels(
                query.getPage(), query.getSize(),
                query.getCarLineCode(), query.getPlatformCode(),
                query.isIncludeInactive());
        return modelList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countModels(String carLineCode, String platformCode, boolean includeInactive) {
        return productDomainService.countModels(carLineCode, platformCode, includeInactive);
    }

    public List<ModelHistoryDto> listModelHistory(String code) {
        List<ModelHistory> historyList = productDomainService.listModelHistory(code);
        return historyList.stream().map(this::convertHistoryToDto).collect(Collectors.toList());
    }

    private ModelDto convertToDto(Model model) {
        return ModelDto.builder()
                .id(model.getId())
                .code(model.getCode())
                .name(model.getName())
                .nameLocal(model.getNameLocal())
                .carLineCode(model.getCarLineCode())
                .platformCode(model.getPlatformCode())
                .modelYear(model.getModelYear())
                .description(model.getDescription())
                .sourceSystem(model.getSourceSystem())
                .sourceId(model.getSourceId())
                .sourceVersion(model.getSourceVersion())
                .ingestionChannel(model.getIngestionChannel())
                .ingestionTime(model.getIngestionTime())
                .sourcePayloadHash(model.getSourcePayloadHash())
                .version(model.getVersion())
                .effectiveFrom(model.getEffectiveFrom())
                .effectiveTo(model.getEffectiveTo())
                .status(model.getStatus().name())
                .createBy(model.getCreateBy())
                .createTime(model.getCreateTime())
                .modifyBy(model.getModifyBy())
                .modifyTime(model.getModifyTime())
                .build();
    }

    private ModelHistoryDto convertHistoryToDto(ModelHistory history) {
        return ModelHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .carLineCode(history.getCarLineCode())
                .platformCode(history.getPlatformCode())
                .modelYear(history.getModelYear())
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
