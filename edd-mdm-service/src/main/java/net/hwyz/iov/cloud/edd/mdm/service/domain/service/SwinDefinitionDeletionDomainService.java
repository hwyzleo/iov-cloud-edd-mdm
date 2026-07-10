package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionHasReferenceException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinRegistryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import org.springframework.stereotype.Service;

/**
 * SWIN定义删除领域服务
 * <p>
 * 删除前引用检查顺序：
 * (a) 同库 RxswinRegistry.swinCode 反查 -- 确定性引用，force 不得旁路
 * (b) 同库 SwinManagedSystem 反查 -- 确定性引用，force 不得旁路
 * (c) VMD 跨服务 per-VIN SWIN 实例反查（若已实现）-- fail-safe，force 可旁路
 *
 * @author hwyz_leo
 */
@Service
@RequiredArgsConstructor
public class SwinDefinitionDeletionDomainService {

    private final SwinManagedSystemRepository swinManagedSystemRepository;
    private final RxswinRegistryRepository rxswinRegistryRepository;

    /**
     * 检查SWIN定义是否可以删除
     *
     * @param swinCode SWIN代码
     * @throws SwinDefinitionHasReferenceException 如果存在引用
     */
    public void checkCanDelete(String swinCode) {
        long rxswinRefCount = rxswinRegistryRepository.countBySwinCode(swinCode);
        if (rxswinRefCount > 0) {
            throw new SwinDefinitionHasReferenceException(swinCode, rxswinRefCount);
        }

        long managedSystemCount = swinManagedSystemRepository.countBySwinCode(swinCode);
        if (managedSystemCount > 0) {
            throw new SwinDefinitionHasReferenceException(swinCode, managedSystemCount);
        }
    }
}
