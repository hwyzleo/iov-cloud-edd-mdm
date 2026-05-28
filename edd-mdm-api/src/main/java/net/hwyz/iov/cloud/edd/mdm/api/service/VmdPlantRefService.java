package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.VmdPlantRefServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefCountResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantRefSampleResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * VMD Plant 引用反查 Feign 客户端
 * <p>
 * MDM 调用 VMD 服务，用于 Plant 删除前置依赖检查（design §4 F13 / §5.6）。
 * VMD-CR-014 落地前，VMD 侧无此接口，FallbackFactory 返回 referenceCount=-1 触发 fail-safe。
 *
 * @author hwyz_leo
 */
@FeignClient(
        contextId = "vmdPlantRefService",
        value = ServiceNameConstants.EDD_VMD,
        path = "/service/manufacturer/v1",
        fallbackFactory = VmdPlantRefServiceFallbackFactory.class
)
public interface VmdPlantRefService {

    /**
     * 统计 VMD 中引用该 plantCode 的本地副本记录数
     *
     * @param plantCode 工厂 code
     * @return 计数响应
     */
    @GetMapping("/countByPlantCode/{plantCode}")
    PlantRefCountResponse countByPlantCode(@PathVariable("plantCode") String plantCode);

    /**
     * 拉取前 N 条引用样本
     *
     * @param plantCode 工厂 code
     * @param limit     样本上限
     * @return 样本列表响应
     */
    @GetMapping("/listByPlantCode/{plantCode}")
    PlantRefSampleResponse listByPlantCode(@PathVariable("plantCode") String plantCode,
                                           @RequestParam(value = "limit", defaultValue = "10") Integer limit);
}
