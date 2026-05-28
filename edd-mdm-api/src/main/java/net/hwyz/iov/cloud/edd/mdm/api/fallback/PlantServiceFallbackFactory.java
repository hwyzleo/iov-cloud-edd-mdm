package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.PlantService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 工厂相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class PlantServiceFallbackFactory implements FallbackFactory<PlantService> {

    @Override
    public PlantService create(Throwable throwable) {
        return new PlantService() {
            @Override
            public PlantPageResponse snapshot(Boolean includeInactive, Integer page, Integer size) {
                log.error("工厂服务获取全量快照调用失败", throwable);
                return PlantPageResponse.empty();
            }

            @Override
            public PlantResponse getByCode(String code) {
                log.error("工厂服务根据code[{}]获取工厂信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<PlantBriefResponse> listByType(String type) {
                log.error("工厂服务按类型[{}]查询工厂列表调用失败", type, throwable);
                return Collections.emptyList();
            }
        };
    }
}
