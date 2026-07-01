package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.VariantHasSwinRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import org.springframework.stereotype.Service;

/**
 * 版本删除领域服务
 *
 * @author hwyz_leo
 */
@Service
@RequiredArgsConstructor
public class VariantDeletionDomainService {

    private final SwinDefinitionRepository swinDefinitionRepository;

    /**
     * 检查版本是否可以删除
     *
     * @param variantCode 版本代码
     * @throws VariantHasSwinRefException 如果存在SWIN引用
     */
    public void checkCanDelete(String variantCode) {
        long referenceCount = swinDefinitionRepository.countActiveByTypeRef("VARIANT", variantCode);
        if (referenceCount > 0) {
            throw new VariantHasSwinRefException(variantCode, referenceCount);
        }
    }
}
