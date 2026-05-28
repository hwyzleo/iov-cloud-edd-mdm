package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.VehicleNodeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodeResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 车载节点相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class VehicleNodeServiceFallbackFactory implements FallbackFactory<VehicleNodeService> {

    @Override
    public VehicleNodeService create(Throwable throwable) {
        return new VehicleNodeService() {
            @Override
            public VehicleNodePageResponse snapshot(Integer page, Integer size, Boolean includeInactive) {
                log.error("车载节点服务获取全量快照调用失败", throwable);
                return VehicleNodePageResponse.empty();
            }

            @Override
            public VehicleNodeResponse getByCode(String nodeCode) {
                log.error("车载节点服务根据nodeCode[{}]获取车载节点信息调用失败", nodeCode, throwable);
                return null;
            }

            @Override
            public List<VehicleNodeResponse> listByOtaType(String type) {
                log.error("车载节点服务按OTA类型[{}]查询车载节点列表调用失败", type, throwable);
                return Collections.emptyList();
            }
        };
    }
}
