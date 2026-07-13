package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.MdmPartServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 零件相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "mdmPartService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/part/v1", fallbackFactory = MdmPartServiceFallbackFactory.class)
public interface MdmPartService {

    /**
     * 获取零件全量快照
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 零件分页响应
     */
    @GetMapping("/snapshot")
    PartPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size);

    /**
     * 根据code获取零件信息
     *
     * @param code 零件code
     * @return 零件响应
     */
    @GetMapping("/{code}")
    PartResponse getByCode(@PathVariable("code") String code);

    /**
     * 按物料分类查询零件列表
     *
     * @param categoryCode 物料分类编码
     * @return 零件简要响应列表
     */
    @GetMapping("/listByCategory")
    List<PartBriefResponse> listByCategory(@RequestParam("categoryCode") String categoryCode);

    /**
     * 按车辆节点查询零件列表
     *
     * @param vehicleNodeCode 车辆节点编码
     * @return 零件简要响应列表
     */
    @GetMapping("/listByVehicleNode")
    List<PartBriefResponse> listByVehicleNode(@RequestParam("vehicleNodeCode") String vehicleNodeCode);

    /**
     * 按供应商查询零件列表
     *
     * @param supplierCode 供应商编码
     * @return 零件简要响应列表
     */
    @GetMapping("/listBySupplier")
    List<PartBriefResponse> listBySupplier(@RequestParam("supplierCode") String supplierCode);
}
