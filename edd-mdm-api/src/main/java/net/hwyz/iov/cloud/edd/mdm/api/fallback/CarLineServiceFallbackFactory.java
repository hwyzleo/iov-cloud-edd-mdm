package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.CarLineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 车系相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class CarLineServiceFallbackFactory implements FallbackFactory<CarLineService> {

    @Override
    public CarLineService create(Throwable throwable) {
        return new CarLineService() {
            @Override
            public CarLinePageResponse listAll(Integer page, Integer size, String brandCode, Boolean includeInactive) {
                log.error("车系服务获取车系全量快照调用失败", throwable);
                return CarLinePageResponse.empty();
            }

            @Override
            public CarLineResponse getByCode(String code) {
                log.error("车系服务根据code[{}]获取车系信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
