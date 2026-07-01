package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Date;
import java.util.List;

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

    /**
     * 创建SWIN编码方案
     *
     * @param code          代码
     * @param name          名称
     * @param nameLocal     本地化名称
     * @param description   描述
     * @param route         路由类型
     * @param sortOrder     排序序号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy      创建人
     * @return 创建的SWIN编码方案
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinScheme createSwinScheme(String code, String name, String nameLocal, String description,
                                        String route, Integer sortOrder, Date effectiveFrom, Date effectiveTo, String createBy) {
        log.info("创建SWIN编码方案: {}", code);
        if (swinSchemeRepository.existsByCode(code)) {
            throw new SwinSchemeDuplicateCodeException(code, "ACTIVE");
        }
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }
        SwinRoute swinRoute = SwinRoute.fromValue(route);
        SwinScheme swinScheme = SwinScheme.create(code, name, nameLocal, description, swinRoute, sortOrder, effectiveFrom, effectiveTo, createBy);
        swinSchemeRepository.save(swinScheme);
        return swinScheme;
    }

    /**
     * 更新SWIN编码方案
     *
     * @param code          代码
     * @param name          名称
     * @param nameLocal     本地化名称
     * @param description   描述
     * @param route         路由类型
     * @param sortOrder     排序序号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy      修改人
     * @return 更新后的SWIN编码方案
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinScheme updateSwinScheme(String code, String name, String nameLocal, String description,
                                        String route, Integer sortOrder, Date effectiveFrom, Date effectiveTo, String modifyBy) {
        log.info("更新SWIN编码方案: {}", code);
        SwinScheme swinScheme = swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinRoute swinRoute = SwinRoute.fromValue(route);
        swinScheme.update(name, nameLocal, description, swinRoute, sortOrder, effectiveFrom, effectiveTo, modifyBy);
        swinSchemeRepository.save(swinScheme);
        return swinScheme;
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
        swinSchemeRepository.deleteByCode(code);
    }

    /**
     * 使SWIN编码方案失效
     *
     * @param code     代码
     * @param modifyBy 修改人
     * @return 更新后的SWIN编码方案
     */
    @Transactional(rollbackFor = Exception.class)
    public SwinScheme deactivateSwinScheme(String code, String modifyBy) {
        log.info("使SWIN编码方案失效: {}", code);
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }
        SwinScheme swinScheme = swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
        swinScheme.deactivate(modifyBy);
        swinSchemeRepository.save(swinScheme);
        return swinScheme;
    }

    /**
     * 根据代码获取SWIN编码方案
     *
     * @param code 代码
     * @return SWIN编码方案
     */
    public SwinScheme getSwinSchemeByCode(String code) {
        return swinSchemeRepository.findByCode(code)
                .orElseThrow(() -> new SwinSchemeNotExistException(code));
    }

    /**
     * 分页获取SWIN编码方案列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效的
     * @return SWIN编码方案列表
     */
    public List<SwinScheme> getSwinSchemes(int page, int size, boolean includeInactive) {
        return swinSchemeRepository.findPaginated(page, size, includeInactive);
    }

    /**
     * 获取所有有效的SWIN编码方案
     *
     * @return SWIN编码方案列表
     */
    public List<SwinScheme> getAllActiveSwinSchemes() {
        return swinSchemeRepository.findAllActive();
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
}
