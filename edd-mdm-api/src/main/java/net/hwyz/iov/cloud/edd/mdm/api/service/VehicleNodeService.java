package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.VehicleNodeServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodeResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 车载节点相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "vehicleNodeService", value = ServiceNameConstants.EDD_MDM, path = "/service/mdm/eead/v1/vehicleNode", fallbackFactory = VehicleNodeServiceFallbackFactory.class)
public interface VehicleNodeService {

    /**
     * 获取车载节点全量快照
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 车载节点分页响应
     */
    @GetMapping("/snapshot")
    VehicleNodePageResponse snapshot(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "100") Integer size,
                                     @RequestParam(required = false) Boolean includeInactive);

    /**
     * 根据nodeCode获取车载节点信息
     *
     * @param nodeCode 车载节点nodeCode
     * @return 车载节点响应
     */
    @GetMapping("/{nodeCode}")
    VehicleNodeResponse getByCode(@PathVariable String nodeCode);

    /**
     * 按OTA支持类型查询车载节点列表
     *
     * @param type OTA支持类型
     * @return 车载节点响应列表
     */
    @GetMapping("/listByOtaType")
    List<VehicleNodeResponse> listByOtaType(@RequestParam String type);
}
