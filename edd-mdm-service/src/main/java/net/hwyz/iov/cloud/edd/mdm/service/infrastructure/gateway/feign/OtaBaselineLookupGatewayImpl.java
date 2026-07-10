package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.gateway.feign;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.gateway.OtaBaselineLookupGateway;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaBaselineReferenceCheckResult;
import org.springframework.stereotype.Component;

/**
 * OTA 基线投影反向查询网关实现（Material 子域）
 * <p>
 * 时序依赖：OTA 侧反查接口属 IOV-OTA-REQ-CR-002 交付，尚未上线。
 * 当前阶段 Fallback 默认 referenceCount=0 以免阻塞删除，上线后切换为 fail-safe 拒绝。
 * <p>
 * TODO: OTA 上线后替换为 Feign Client + FallbackFactory + Resilience4j 熔断实现。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class OtaBaselineLookupGatewayImpl implements OtaBaselineLookupGateway {

    @Override
    public OtaBaselineReferenceCheckResult checkReferences(String baselineCode, int sampleLimit) {
        log.info("OTA 基线投影反查：OTA 服务尚未上线，默认 referenceCount=0 baselineCode={}", baselineCode);
        return OtaBaselineReferenceCheckResult.noReference();
    }
}
