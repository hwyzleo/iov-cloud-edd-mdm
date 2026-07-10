package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinSchemeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinSchemeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeDuplicateCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN编码方案应用服务（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwinSchemeAppService {

    private final SwinSchemeRepository swinSchemeRepository;
    private final SwinDefinitionRepository swinDefinitionRepository;
    private final OutboxService outboxService;

    /**
     * 创建SWIN编码方案
     *
     * @param cmd 创建命令
     * @return 创建的SWIN编码方案DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinSchemeDto createSwinScheme(SwinSchemeCreateCmd cmd) {
        log.info("创建SWIN编码方案: {}", cmd.getCode());
        if (swinSchemeRepository.existsByCode(cmd.getCode())) {
            throw new SwinSchemeDuplicateCodeException(cmd.getCode(), "ACTIVE");
        }
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        SwinRoute swinRoute = SwinRoute.fromValue(cmd.getRoute());
        SwinScheme swinScheme = SwinScheme.create(cmd.getCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getDescription(), swinRoute, cmd.getStructurePattern(), cmd.getVersionFormat(),
                cmd.getSortOrder(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), createBy);
        swinSchemeRepository.save(swinScheme);
        outboxService.publishSwinSchemeCreatedEvent(swinScheme);
        return toDto(swinScheme);
    }

    /**
     * 更新SWIN编码方案
     *
     * @param cmd 更新命令
     * @return 更新后的SWIN编码方案DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinSchemeDto updateSwinScheme(SwinSchemeUpdateCmd cmd) {
        log.info("更新SWIN编码方案: {}", cmd.getCode());
        SwinScheme swinScheme = swinSchemeRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new SwinSchemeNotExistException(cmd.getCode()));
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinRoute swinRoute = SwinRoute.fromValue(cmd.getRoute());
        swinScheme.update(cmd.getName(), cmd.getNameLocal(), cmd.getDescription(), swinRoute,
                cmd.getStructurePattern(), cmd.getVersionFormat(), cmd.getSortOrder(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), modifyBy);
        swinSchemeRepository.save(swinScheme);
        outboxService.publishSwinSchemeUpdatedEvent(swinScheme);
        return toDto(swinScheme);
    }

    /**
     * 删除SWIN编码方案
     *
     * @param code     代码
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSwinScheme(String code, String operator) {
        log.info("删除SWIN编码方案: {}", code);
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        SwinScheme swinScheme = swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
        long referenceCount = swinDefinitionRepository.countBySchemeCode(code);
        if (referenceCount > 0) {
            throw new SwinSchemeHasReferenceException(code, referenceCount);
        }
        swinScheme.markAsDeleting(operator);
        outboxService.publishSwinSchemeDeletedEvent(swinScheme);
        swinSchemeRepository.deleteByCode(code);
    }

    /**
     * 使SWIN编码方案失效
     *
     * @param code     代码
     * @param modifyBy 修改人
     * @return 更新后的SWIN编码方案DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinSchemeDto deactivateSwinScheme(String code, String modifyBy) {
        log.info("使SWIN编码方案失效: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinScheme swinScheme = swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
        swinScheme.deactivate(modifyBy);
        swinSchemeRepository.save(swinScheme);
        outboxService.publishSwinSchemeUpdatedEvent(swinScheme);
        return toDto(swinScheme);
    }

    /**
     * 根据代码获取SWIN编码方案
     *
     * @param code 代码
     * @return SWIN编码方案DTO
     */
    public SwinSchemeDto getSwinSchemeByCode(String code) {
        SwinScheme swinScheme = swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
        return toDto(swinScheme);
    }

    /**
     * 分页获取SWIN编码方案列表
     *
     * @param query 查询对象
     * @return SWIN编码方案DTO列表
     */
    public List<SwinSchemeDto> listSwinSchemes(SwinSchemeQuery query) {
        boolean includeInactive = Boolean.TRUE.equals(query.getIncludeInactive());
        return swinSchemeRepository.findPaginated(query.getPage(), query.getSize(), includeInactive)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有有效的SWIN编码方案
     *
     * @return SWIN编码方案DTO列表
     */
    public List<SwinSchemeDto> listAllActiveSwinSchemes() {
        return swinSchemeRepository.findAllActive()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 统计SWIN编码方案总数
     *
     * @param includeInactive 是否包含失效的
     * @return 总数
     */
    public long countSwinSchemes(boolean includeInactive) {
        return swinSchemeRepository.count(includeInactive);
    }

    /**
     * 领域对象转DTO
     *
     * @param swinScheme 领域对象
     * @return DTO
     */
    private SwinSchemeDto toDto(SwinScheme swinScheme) {
        if (swinScheme == null) {
            return null;
        }
        return SwinSchemeDto.builder()
                .id(swinScheme.getId())
                .code(swinScheme.getCode())
                .name(swinScheme.getName())
                .nameLocal(swinScheme.getNameLocal())
                .description(swinScheme.getDescription())
                .route(swinScheme.getRoute() != null ? swinScheme.getRoute().name() : null)
                .structurePattern(swinScheme.getStructurePattern())
                .versionFormat(swinScheme.getVersionFormat())
                .sortOrder(swinScheme.getSortOrder())
                .source(swinScheme.getSource())
                .externalRefId(swinScheme.getExternalRefId())
                .externalVersion(swinScheme.getExternalVersion())
                .lastSyncTime(swinScheme.getLastSyncTime())
                .version(swinScheme.getVersion())
                .effectiveFrom(swinScheme.getEffectiveFrom())
                .effectiveTo(swinScheme.getEffectiveTo())
                .status(swinScheme.getStatus() != null ? swinScheme.getStatus().name() : null)
                .createBy(swinScheme.getCreateBy())
                .createTime(swinScheme.getCreateTime())
                .modifyBy(swinScheme.getModifyBy())
                .modifyTime(swinScheme.getModifyTime())
                .build();
    }
}
