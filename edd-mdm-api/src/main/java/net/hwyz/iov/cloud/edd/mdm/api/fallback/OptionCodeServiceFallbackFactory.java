package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.OptionCodeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 选项码相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class OptionCodeServiceFallbackFactory implements FallbackFactory<OptionCodeService> {

    @Override
    public OptionCodeService create(Throwable throwable) {
        return new OptionCodeService() {
            @Override
            public OptionCodePageResponse listAll(Integer page, Integer size, String optionFamilyCode, Boolean includeInactive) {
                log.error("选项码服务获取选项码全量快照调用失败", throwable);
                return OptionCodePageResponse.empty();
            }

            @Override
            public OptionCodeResponse getByCode(String code) {
                log.error("选项码服务根据code[{}]获取选项码信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
