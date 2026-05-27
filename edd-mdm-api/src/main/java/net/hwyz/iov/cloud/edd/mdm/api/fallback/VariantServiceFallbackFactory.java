package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VariantService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 版本相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class VariantServiceFallbackFactory implements FallbackFactory<VariantService> {

    @Override
    public VariantService create(Throwable throwable) {
        return new VariantService() {
            @Override
            public VariantPageResponse listAll(Integer page, Integer size, String modelCode, String carLineCode, String platformCode, Boolean includeInactive) {
                log.error("版本服务获取版本全量快照调用失败", throwable);
                return VariantPageResponse.empty();
            }

            @Override
            public VariantResponse getByCode(String code) {
                log.error("版本服务根据code[{}]获取版本信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
