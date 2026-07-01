package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionDuplicateSwinCodeException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSchemeNotActiveException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionSingleSwinConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinSchemeNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 创建SWIN定义
     *
     * @param swinCode    SWIN代码
     * @param schemeCode  编码方案代码
     * @param typeRefType 引用类型（VARIANT/MODEL）
     * @param typeRefCode 引用代码
     * @param name        名称
     * @param nameLocal   本地化名称
     * @param description 描述
     * @param createBy    创建人
     * @return 创建的SWIN定义
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinition createSwinDefinition(String swinCode, String schemeCode, String typeRefType, String typeRefCode,
                                                String name, String nameLocal, String description, String createBy) {
        log.info("创建SWIN定义: {}, 编码方案: {}, 引用类型: {}, 引用代码: {}", swinCode, schemeCode, typeRefType, typeRefCode);
        if (swinDefinitionRepository.existsBySwinCode(swinCode)) {
            throw new SwinDefinitionDuplicateSwinCodeException(swinCode, "ACTIVE");
        }
        SwinScheme swinScheme = swinSchemeRepository.findByCode(schemeCode)
                .orElseThrow(() -> new SwinSchemeNotExistException(schemeCode));
        if (swinScheme.getStatus() != SwinSchemeStatus.ACTIVE) {
            throw new SwinDefinitionSchemeNotActiveException(schemeCode, swinScheme.getStatus().name());
        }
        if (swinScheme.getRoute() == SwinRoute.SINGLE_SWIN) {
            long existingCount = swinDefinitionRepository.countActiveByTypeRef(typeRefType, typeRefCode);
            if (existingCount > 0) {
                throw new SwinDefinitionSingleSwinConflictException(schemeCode, typeRefType, typeRefCode);
            }
        }
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        SwinDefinition swinDefinition = SwinDefinition.create(swinCode, schemeCode, typeRefType, typeRefCode,
                name, nameLocal, description, createBy);
        swinDefinitionRepository.save(swinDefinition);
        return swinDefinition;
    }

    /**
     * 更新SWIN定义
     *
     * @param swinCode    SWIN代码
     * @param name        名称
     * @param nameLocal   本地化名称
     * @param description 描述
     * @param modifyBy    修改人
     * @return 更新后的SWIN定义
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinition updateSwinDefinition(String swinCode, String name, String nameLocal, String description, String modifyBy) {
        log.info("更新SWIN定义: {}", swinCode);
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        swinDefinition.update(name, nameLocal, description, modifyBy);
        swinDefinitionRepository.save(swinDefinition);
        return swinDefinition;
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
        swinDefinition.markAsDeleting(operator);
        swinDefinitionRepository.deleteBySwinCode(swinCode);
    }

    /**
     * 使SWIN定义失效
     *
     * @param swinCode SWIN代码
     * @param modifyBy 修改人
     * @return 更新后的SWIN定义
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinDefinition deactivateSwinDefinition(String swinCode, String modifyBy) {
        log.info("使SWIN定义失效: {}", swinCode);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        swinDefinition.deactivate(modifyBy);
        swinDefinitionRepository.save(swinDefinition);
        return swinDefinition;
    }

    /**
     * 根据SWIN代码获取SWIN定义
     *
     * @param swinCode SWIN代码
     * @return SWIN定义
     */
    public SwinDefinition getSwinDefinitionBySwinCode(String swinCode) {
        return swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
    }

    /**
     * 分页获取SWIN定义列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效的
     * @return SWIN定义列表
     */
    public List<SwinDefinition> getSwinDefinitions(int page, int size, boolean includeInactive) {
        return swinDefinitionRepository.findPaginated(page, size, includeInactive);
    }

    /**
     * 获取所有有效的SWIN定义
     *
     * @return SWIN定义列表
     */
    public List<SwinDefinition> getAllActiveSwinDefinitions() {
        return swinDefinitionRepository.findAllActive();
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
}
