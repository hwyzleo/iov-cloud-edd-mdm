package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.PlatformService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 平台相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class PlatformServiceFallbackFactory implements FallbackFactory<PlatformService> {

    @Override
    public PlatformService create(Throwable throwable) {
        return new PlatformService() {
            @Override
            public PlatformPageResponse listAll(Integer page, Integer size, Boolean includeInactive) {
                log.error("平台服务获取平台全量快照调用失败", throwable);
                return PlatformPageResponse.empty();
            }

            @Override
            public PlatformResponse getByCode(String code) {
                log.error("平台服务根据code[{}]获取平台信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
