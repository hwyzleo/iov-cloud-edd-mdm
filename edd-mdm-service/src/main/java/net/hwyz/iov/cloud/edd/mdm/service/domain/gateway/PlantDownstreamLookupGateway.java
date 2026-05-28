package net.hwyz.iov.cloud.edd.mdm.service.domain.gateway;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlantReferenceCheckResult;

/**
 * Plant 下游引用反查网关（Org 子域）
 * <p>
 * 领域层抽象"反查下游是否引用了 plantCode"的能力，由 infrastructure 层基于 Feign 实现。
 *
 * @author hwyz_leo
 */
public interface PlantDownstreamLookupGateway {

    /**
     * 反查指定 plantCode 的下游引用情况。
     *
     * @param plantCode   待删除的工厂 code
     * @param sampleLimit 引用样本上限（建议 10）
     * @return 反查结果
     */
    PlantReferenceCheckResult checkReferences(String plantCode, int sampleLimit);
}
