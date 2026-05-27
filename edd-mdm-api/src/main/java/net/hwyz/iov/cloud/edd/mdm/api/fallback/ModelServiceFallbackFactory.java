package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.ModelService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 车型相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class ModelServiceFallbackFactory implements FallbackFactory<ModelService> {

    @Override
    public ModelService create(Throwable throwable) {
        return new ModelService() {
            @Override
            public ModelPageResponse listAll(Integer page, Integer size, String carLineCode, String platformCode, Boolean includeInactive) {
                log.error("车型服务获取车型全量快照调用失败", throwable);
                return ModelPageResponse.empty();
            }

            @Override
            public ModelResponse getByCode(String code) {
                log.error("车型服务根据code[{}]获取车型信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
