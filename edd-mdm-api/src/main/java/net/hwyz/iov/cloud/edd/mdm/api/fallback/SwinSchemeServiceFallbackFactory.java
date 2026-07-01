package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.SwinSchemeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemeResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * SWIN编码方案服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class SwinSchemeServiceFallbackFactory implements FallbackFactory<SwinSchemeService> {

    @Override
    public SwinSchemeService create(Throwable throwable) {
        return new SwinSchemeService() {
            @Override
            public SwinSchemePageResponse getSnapshot(boolean includeInactive, int page, int size) {
                log.error("SWIN编码方案服务获取全量快照调用失败", throwable);
                return SwinSchemePageResponse.empty();
            }

            @Override
            public SwinSchemeResponse getSwinSchemeByCode(String code) {
                log.error("SWIN编码方案服务根据code[{}]获取SWIN编码方案信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<SwinSchemeResponse> listAllActiveSwinSchemes() {
                log.error("SWIN编码方案服务获取全量列表调用失败", throwable);
                return Collections.emptyList();
            }
        };
    }
}
