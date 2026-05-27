package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.OptionFamilyService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 选项族相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class OptionFamilyServiceFallbackFactory implements FallbackFactory<OptionFamilyService> {

    @Override
    public OptionFamilyService create(Throwable throwable) {
        return new OptionFamilyService() {
            @Override
            public OptionFamilyPageResponse listAll(Integer page, Integer size, Boolean includeInactive) {
                log.error("选项族服务获取选项族全量快照调用失败", throwable);
                return OptionFamilyPageResponse.empty();
            }

            @Override
            public OptionFamilyResponse getByCode(String code) {
                log.error("选项族服务根据code[{}]获取选项族信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
