package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinDefinitionQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinDefinitionDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinManagedSystemDto;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionDuplicateSwinCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSchemeNotActiveException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSingleSwinConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinManagedSystem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SwinDefinitionDeletionDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN定义应用服务（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SwinDefinitionAppService {

    private final SwinDefinitionRepository swinDefinitionRepository;
    private final SwinSchemeRepository swinSchemeRepository;
    private final SwinDefinitionDeletionDomainService swinDefinitionDeletionDomainService;

    /**
     * 创建SWIN定义
     *
     * @param cmd 创建命令
     * @return 创建的SWIN定义DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinitionDto createSwinDefinition(SwinDefinitionCreateCmd cmd) {
        log.info("创建SWIN定义: {}, 编码方案: {}, 引用类型: {}, 引用代码: {}",
                cmd.getSwinCode(), cmd.getSchemeCode(), cmd.getTypeRefType(), cmd.getTypeRefCode());
        if (swinDefinitionRepository.existsBySwinCode(cmd.getSwinCode())) {
            throw new SwinDefinitionDuplicateSwinCodeException(cmd.getSwinCode(), "ACTIVE");
        }
        SwinScheme swinScheme = swinSchemeRepository.findByCode(cmd.getSchemeCode())
                .orElseThrow(() -> new SwinSchemeNotExistException(cmd.getSchemeCode()));
        if (swinScheme.getStatus() != SwinSchemeStatus.ACTIVE) {
            throw new SwinDefinitionSchemeNotActiveException(cmd.getSchemeCode(), swinScheme.getStatus().name());
        }
        if (swinScheme.getRoute() == SwinRoute.SINGLE_SWIN) {
            long existingCount = swinDefinitionRepository.countActiveByTypeRef(cmd.getTypeRefType(), cmd.getTypeRefCode());
            if (existingCount > 0) {
                throw new SwinDefinitionSingleSwinConflictException(cmd.getSchemeCode(), cmd.getTypeRefType(), cmd.getTypeRefCode());
            }
        }
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        SwinDefinition swinDefinition = SwinDefinition.create(cmd.getSwinCode(), cmd.getSchemeCode(),
                cmd.getTypeRefType(), cmd.getTypeRefCode(), cmd.getName(), cmd.getNameLocal(),
                cmd.getDescription(), createBy);
        swinDefinitionRepository.save(swinDefinition);
        return toDto(swinDefinition);
    }

    /**
     * 更新SWIN定义
     *
     * @param cmd 更新命令
     * @return 更新后的SWIN定义DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinitionDto updateSwinDefinition(SwinDefinitionUpdateCmd cmd) {
        log.info("更新SWIN定义: {}", cmd.getSwinCode());
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(cmd.getSwinCode())
                .orElseThrow(() -> new SwinDefinitionNotExistException(cmd.getSwinCode()));
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        swinDefinition.update(cmd.getName(), cmd.getNameLocal(), cmd.getDescription(), modifyBy);
        swinDefinitionRepository.save(swinDefinition);
        return toDto(swinDefinition);
    }

    /**
     * 删除SWIN定义
     *
     * @param swinCode SWIN代码
     * @param operator 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSwinDefinition(String swinCode, String operator) {
        log.info("删除SWIN定义: {}", swinCode);
        if (operator == null || operator.isBlank()) {
            operator = SecurityUtils.getUsername();
        }
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        swinDefinitionDeletionDomainService.checkCanDelete(swinCode);
        swinDefinition.markAsDeleting(operator);
        swinDefinitionRepository.deleteBySwinCode(swinCode);
    }

    /**
     * 使SWIN定义失效
     *
     * @param swinCode SWIN代码
     * @param modifyBy 修改人
     * @return 更新后的SWIN定义DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinitionDto deactivateSwinDefinition(String swinCode, String modifyBy) {
        log.info("使SWIN定义失效: {}", swinCode);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        swinDefinition.deactivate(modifyBy);
        swinDefinitionRepository.save(swinDefinition);
        return toDto(swinDefinition);
    }

    /**
     * 根据SWIN代码获取SWIN定义
     *
     * @param swinCode SWIN代码
     * @return SWIN定义DTO
     */
    public SwinDefinitionDto getSwinDefinitionBySwinCode(String swinCode) {
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        return toDto(swinDefinition);
    }

    /**
     * 分页获取SWIN定义列表
     *
     * @param query 查询对象
     * @return SWIN定义DTO列表
     */
    public List<SwinDefinitionDto> listSwinDefinitions(SwinDefinitionQuery query) {
        boolean includeInactive = Boolean.TRUE.equals(query.getIncludeInactive());
        return swinDefinitionRepository.findPaginated(query.getPage(), query.getSize(), includeInactive)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有有效的SWIN定义
     *
     * @return SWIN定义DTO列表
     */
    public List<SwinDefinitionDto> listAllActiveSwinDefinitions() {
        return swinDefinitionRepository.findAllActive()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 统计SWIN定义总数
     *
     * @param includeInactive 是否包含失效的
     * @return 总数
     */
    public long countSwinDefinitions(boolean includeInactive) {
        return swinDefinitionRepository.count(includeInactive);
    }

    /**
     * 根据编码方案代码获取所有有效的SWIN定义
     *
     * @param schemeCode 编码方案代码
     * @return SWIN定义DTO列表
     */
    public List<SwinDefinitionDto> listSwinDefinitionsBySchemeCode(String schemeCode) {
        return swinDefinitionRepository.findAllActiveBySchemeCode(schemeCode)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据引用类型和引用代码获取所有有效的SWIN定义
     *
     * @param typeRefType 引用类型
     * @param typeRefCode 引用代码
     * @return SWIN定义DTO列表
     */
    public List<SwinDefinitionDto> listSwinDefinitionsByTypeRef(String typeRefType, String typeRefCode) {
        return swinDefinitionRepository.findAllActiveByTypeRef(typeRefType, typeRefCode)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 领域对象转DTO
     *
     * @param swinDefinition 领域对象
     * @return DTO
     */
    private SwinDefinitionDto toDto(SwinDefinition swinDefinition) {
        if (swinDefinition == null) {
            return null;
        }
        List<SwinManagedSystemDto> managedSystems = null;
        if (swinDefinition.getManagedSystems() != null) {
            managedSystems = swinDefinition.getManagedSystems().stream()
                    .map(this::toManagedSystemDto)
                    .collect(Collectors.toList());
        }
        return SwinDefinitionDto.builder()
                .id(swinDefinition.getId())
                .swinCode(swinDefinition.getSwinCode())
                .schemeCode(swinDefinition.getSchemeCode())
                .typeRefType(swinDefinition.getTypeRefType())
                .typeRefCode(swinDefinition.getTypeRefCode())
                .name(swinDefinition.getName())
                .nameLocal(swinDefinition.getNameLocal())
                .description(swinDefinition.getDescription())
                .version(swinDefinition.getVersion())
                .status(swinDefinition.getStatus() != null ? swinDefinition.getStatus().name() : null)
                .createBy(swinDefinition.getCreateBy())
                .createTime(swinDefinition.getCreateTime())
                .modifyBy(swinDefinition.getModifyBy())
                .modifyTime(swinDefinition.getModifyTime())
                .managedSystems(managedSystems)
                .build();
    }

    /**
     * 管理软件系统领域对象转DTO
     *
     * @param swinManagedSystem 领域对象
     * @return DTO
     */
    private SwinManagedSystemDto toManagedSystemDto(SwinManagedSystem swinManagedSystem) {
        if (swinManagedSystem == null) {
            return null;
        }
        return SwinManagedSystemDto.builder()
                .id(swinManagedSystem.getId())
                .swinCode(swinManagedSystem.getSwinCode())
                .vehicleNodeCode(swinManagedSystem.getVehicleNodeCode())
                .createBy(swinManagedSystem.getCreateBy())
                .createTime(swinManagedSystem.getCreateTime())
                .modifyBy(swinManagedSystem.getModifyBy())
                .modifyTime(swinManagedSystem.getModifyTime())
                .build();
    }
}
