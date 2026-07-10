package net.hwyz.iov.cloud.edd.mdm.service.domain.gateway;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaBaselineReferenceCheckResult;

/**
 * OTA 基线投影反向查询网关（Material 子域）
 * <p>
 * 领域层抽象"反查 OTA 是否有引用该基线 code 的投影/升级活动"的能力，由 infrastructure 层基于 Feign 实现。
 * 设计参考：design §3 F23 流程 / §4.3 跨服务依赖。
 * <p>
 * 时序依赖：OTA 侧接口属 IOV-OTA-REQ-CR-002，上线前 Fallback 默认 referenceCount=0 以免阻塞删除，上线后切换为 fail-safe 拒绝。
 *
 * @author hwyz_leo
 */
public interface OtaBaselineLookupGateway {

    /**
     * 反查指定 baselineCode 的 OTA 投影引用情况。
     * <p>
     * 实现层 SHALL 提供 fail-safe 行为：
     * 当 Feign 调用失败 / 超时 / 降级时，SHALL 返回 available=false 的结果而非抛异常，由调用方决定如何处理。
     *
     * @param baselineCode 待删除的软件基线 code
     * @param sampleLimit  引用样本上限（建议 10）
     * @return 反查结果（含 referenceCount / samples / available 标识）
     */
    OtaBaselineReferenceCheckResult checkReferences(String baselineCode, int sampleLimit);
}
