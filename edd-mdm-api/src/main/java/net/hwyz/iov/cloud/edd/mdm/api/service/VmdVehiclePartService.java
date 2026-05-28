package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.VmdVehiclePartServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehiclePartListResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * VMD VehiclePart 反向查询 Feign 客户端
 * <p>
 * MDM 调用 VMD 服务，用于 VehicleNode 删除前置依赖检查（design §4 F11 / §5.6）。
 * 调用失败时由 {@link VmdVehiclePartServiceFallbackFactory} 兜底降级（fail-safe）。
 *
 * @author hwyz_leo
 */
@FeignClient(
        contextId = "vmdVehiclePartService",
        value = ServiceNameConstants.EDD_VMD,
        path = "/service/vehiclePart/v1",
        fallbackFactory = VmdVehiclePartServiceFallbackFactory.class
)
public interface VmdVehiclePartService {

    /**
     * 统计 VMD 中 source=MDM 且引用该 nodeCode 的 VehiclePart 记录数
     *
     * @param nodeCode 车载节点 nodeCode
     * @return 计数响应
     */
    @GetMapping("/countByNodeCode/{nodeCode}")
    VehiclePartCountResponse countByNodeCode(@PathVariable("nodeCode") String nodeCode);

    /**
     * 拉取前 N 条引用样本
     *
     * @param nodeCode 车载节点 nodeCode
     * @param limit    样本上限
     * @return 样本列表响应
     */
    @GetMapping("/listByNodeCode/{nodeCode}")
    VehiclePartListResponse listByNodeCode(@PathVariable("nodeCode") String nodeCode,
                                           @RequestParam(value = "limit", defaultValue = "10") Integer limit);
}
