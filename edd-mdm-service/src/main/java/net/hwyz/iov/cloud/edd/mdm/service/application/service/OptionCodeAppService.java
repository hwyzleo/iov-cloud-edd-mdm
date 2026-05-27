package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionCodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionCodeHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionCodeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.DuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionCodeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OptionFamilyRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OptionFamilyStatus;
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

    @Transactional(rollbackFor = Exception.class)
    public OptionCodeDto createOptionCode(OptionCodeCreateCmd cmd) {
        log.info("创建选项码: {}", cmd.getCode());

        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 检查code唯一性
        if (optionCodeRepository.existsByCode(cmd.getCode())) {
            throw new DuplicateCodeException("选项码code已存在: " + cmd.getCode());
        }

        // 检查OptionFamily是否存在且ACTIVE
        OptionFamily optionFamily = optionFamilyRepository.findByCode(cmd.getOptionFamilyCode())
                .orElseThrow(() -> new IllegalArgumentException("选项族不存在: " + cmd.getOptionFamilyCode()));
        if (optionFamily.getStatus() != OptionFamilyStatus.ACTIVE) {
            throw new IllegalArgumentException("选项族状态不是ACTIVE: " + cmd.getOptionFamilyCode());
        }

        OptionCode optionCode = OptionCode.create(cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getOptionFamilyCode(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);

        optionCode = optionCodeRepository.save(optionCode);
        return convertToDto(optionCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public OptionCodeDto updateOptionCode(OptionCodeUpdateCmd cmd) {
        log.info("更新选项码: {}", cmd.getCode());

        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        OptionCode optionCode = optionCodeRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new IllegalArgumentException("选项码不存在: " + cmd.getCode()));

        optionCode.update(cmd.getName(), cmd.getNameLocal(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);

        optionCode = optionCodeRepository.save(optionCode);
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
        optionCode = optionCodeRepository.save(optionCode);
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

        optionCode.delete(modifyBy);
        optionCodeRepository.save(optionCode);
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
