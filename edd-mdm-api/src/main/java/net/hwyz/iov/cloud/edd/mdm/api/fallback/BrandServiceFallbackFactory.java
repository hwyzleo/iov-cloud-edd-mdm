package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.BrandService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 品牌相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class BrandServiceFallbackFactory implements FallbackFactory<BrandService> {

    @Override
    public BrandService create(Throwable throwable) {
        return new BrandService() {
            @Override
            public BrandPageResponse listAll(Integer page, Integer size, Boolean includeInactive) {
                log.error("品牌服务获取品牌全量快照调用失败", throwable);
                return BrandPageResponse.empty();
            }

            @Override
            public BrandResponse getByCode(String code) {
                log.error("品牌服务根据code[{}]获取品牌信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
