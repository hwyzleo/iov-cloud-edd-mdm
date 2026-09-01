package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionCodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionCodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationOptionCodeBindingRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.policy.OptionCodeCodePolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmBaseException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.MdmErrorCode;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项码应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptionCodeAppService {

    private final OptionCodeRepository optionCodeRepository;
    private final OptionFamilyRepository optionFamilyRepository;
    private final VariantOptionCodeBindingRepository variantOptionCodeBindingRepository;
    private final ConfigurationOptionCodeBindingRepository configurationOptionCodeBindingRepository;
    private final OptionCodeCodePolicy optionCodeCodePolicy;
    private final OutboxService outboxService;

    @Transactional(rollbackFor = Exception.class)
    public OptionCodeDto createOptionCode(OptionCodeCreateCmd cmd) {
        log.info("创建选项码: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // CR-040：处理顺序遵循设计 §5：
        // 1. Option Family 不存在或非 ACTIVE → 沿用既有引用完整性错误
        OptionFamily optionFamily = optionFamilyRepository.findByCode(cmd.getOptionFamilyCode())
                .orElseThrow(() -> new IllegalArgumentException("选项族不存在: " + cmd.getOptionFamilyCode()));
        if (optionFamily.getStatus() != OptionFamilyStatus.ACTIVE) {
            throw new IllegalArgumentException("选项族状态不是ACTIVE: " + cmd.getOptionFamilyCode());
        }

        // 2. Option Code 基础格式非法（正则/字符集/VALUE/长度）→ 812127
        // 3. 格式合法但所属族派生主干不一致 → 812128
        optionCodeCodePolicy.validateCreate(cmd.getCode(), cmd.getOptionFamilyCode());

        // 4. 主干一致但 code 已存在 → 沿用 812101
        if (optionCodeRepository.existsByCode(cmd.getCode())) {
            throw new DuplicateCodeException("选项码code已存在: " + cmd.getCode());
        }

        OptionCode optionCode = OptionCode.create(cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getOptionFamilyCode(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);

        optionCode = optionCodeRepository.save(optionCode, "CREATE");
        // CR-040：新建同步写主表、history 与 outbox（事件与快照保留完整新 code）
        outboxService.publishOptionCodeCreatedEvent(optionCode);
        return convertToDto(optionCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionCodeDto updateOptionCode(OptionCodeUpdateCmd cmd) {
        log.info("更新选项码: {}", cmd.getCode());

        // CR-040：code 创建后不可变；optionFamilyCode 不允许跨族迁移（update 不触碰这两个字段）。
        // legacy code 仅执行名称/描述/状态/生效期等非 code 字段更新，不追溯执行新格式拦截。
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        OptionCode optionCode = optionCodeRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("选项码不存在: " + cmd.getCode()));

        optionCode.update(cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);

        optionCode = optionCodeRepository.save(optionCode, "UPDATE");
        outboxService.publishOptionCodeUpdatedEvent(optionCode);
        return convertToDto(optionCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionCodeDto deactivateOptionCode(String code, String modifyBy) {
        log.info("失效选项码: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        OptionCode optionCode = optionCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("选项码不存在: " + code));

        optionCode.deactivate(modifyBy);
        optionCode = optionCodeRepository.save(optionCode, "DEACTIVATE");
        outboxService.publishOptionCodeDeactivatedEvent(optionCode);
        return convertToDto(optionCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOptionCode(String code, String modifyBy) {
        log.info("删除选项码: {}", code);

        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        OptionCode optionCode = optionCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("选项码不存在: " + code));

        // 删除前置依赖检查：被 Variant / Configuration 绑定则拒绝删除（812108），避免悬空引用
        boolean variantBound = variantOptionCodeBindingRepository.existsByOptionCodeCode(code);
        boolean configurationBound = configurationOptionCodeBindingRepository.existsByOptionCodeCode(code);
        if (variantBound || configurationBound) {
            throw new MdmBaseException(MdmErrorCode.HAS_CHILDREN_REFERENCE,
                    String.format("选项码 %s 已被 Variant 或 Configuration 绑定，删除被拒绝", code));
        }

        optionCode.delete(modifyBy);
        // 物理删除（写 DELETE 历史快照 + 硬删主表）
        optionCodeRepository.delete(optionCode);
    }

    public OptionCodeDto getOptionCodeByCode(String code) {
        OptionCode optionCode = optionCodeRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("选项码不存在: " + code));
        return convertToDto(optionCode);
    }

    public List<OptionCodeDto> listOptionCode(OptionCodeQuery query) {
        List<OptionCode> list = optionCodeRepository.findAll(
                query.getPage(), query.getSize(),
                query.getOptionFamilyCode(), query.isIncludeInactive());
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public long countOptionCode(String optionFamilyCode, boolean includeInactive) {
        return optionCodeRepository.count(optionFamilyCode, includeInactive);
    }

    public List<OptionCodeHistoryDto> listOptionCodeHistory(String code) {
        if (!optionCodeRepository.existsByCode(code)) {
            throw new IllegalArgumentException("选项码不存在: " + code);
        }
        List<OptionCodeHistory> historyList = optionCodeRepository.findHistoryByCode(code);
        return historyList.stream().map(this::convertHistoryToDto).collect(Collectors.toList());
    }

    private OptionCodeDto convertToDto(OptionCode optionCode) {
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

    private OptionCodeHistoryDto convertHistoryToDto(OptionCodeHistory history) {
        return OptionCodeHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .optionFamilyCode(history.getOptionFamilyCode())
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
