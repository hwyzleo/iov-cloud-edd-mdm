package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import org.springframework.stereotype.Service;

/**
 * SWIN定义删除领域服务
 *
 * @author hwyz_leo
 */
@Service
@RequiredArgsConstructor
public class SwinDefinitionDeletionDomainService {

    private final SwinManagedSystemRepository swinManagedSystemRepository;

    /**
     * 检查SWIN定义是否可以删除
     *
     * @param swinCode SWIN代码
     * @throws SwinDefinitionHasReferenceException 如果存在引用
     */
    public void checkCanDelete(String swinCode) {
        long referenceCount = swinManagedSystemRepository.countBySwinCode(swinCode);
        if (referenceCount > 0) {
            throw new SwinDefinitionHasReferenceException(swinCode, referenceCount);
        }
    }
}
