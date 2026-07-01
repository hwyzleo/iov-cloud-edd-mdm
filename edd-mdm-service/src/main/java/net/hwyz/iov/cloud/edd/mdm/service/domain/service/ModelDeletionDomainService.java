package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.ModelHasSwinRefException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import org.springframework.stereotype.Service;

/**
 * 车型删除领域服务
 *
 * @author hwyz_leo
 */
@Service
@RequiredArgsConstructor
public class ModelDeletionDomainService {

    private final SwinDefinitionRepository swinDefinitionRepository;

    /**
     * 检查车型是否可以删除
     *
     * @param modelCode 车型代码
     * @throws ModelHasSwinRefException 如果存在SWIN引用
     */
    public void checkCanDelete(String modelCode) {
        long referenceCount = swinDefinitionRepository.countActiveByTypeRef("MODEL", modelCode);
        if (referenceCount > 0) {
            throw new ModelHasSwinRefException(modelCode, referenceCount);
        }
    }
}
