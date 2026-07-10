package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.RxswinRegistryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.RxswinRegisterRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.RxswinRegistryResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * RXSWIN登记服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class RxswinRegistryServiceFallbackFactory implements FallbackFactory<RxswinRegistryService> {

    @Override
    public RxswinRegistryService create(Throwable throwable) {
        return new RxswinRegistryService() {
            @Override
            public RxswinRegistryResponse register(RxswinRegisterRequest request) {
                log.error("RXSWIN登记服务登记调用失败: manifestCode={}", request.getManifestCode(), throwable);
                return null;
            }

            @Override
            public RxswinRegistryResponse getByManifestCode(String manifestCode) {
                log.error("RXSWIN登记服务查询调用失败: manifestCode={}", manifestCode, throwable);
                return null;
            }
        };
    }
}
