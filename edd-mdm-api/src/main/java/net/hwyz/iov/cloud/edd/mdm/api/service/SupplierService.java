package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.SupplierServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 供应商相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "supplierService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/supplier/v1", fallbackFactory = SupplierServiceFallbackFactory.class)
public interface SupplierService {

    /**
     * 获取供应商全量快照
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 供应商分页响应
     */
    @GetMapping("/listAll")
    SupplierPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "100") Integer size,
                                 @RequestParam(required = false) Boolean includeInactive);

    /**
     * 根据code获取供应商信息
     *
     * @param code 供应商code
     * @return 供应商响应
     */
    @GetMapping("/{code}")
    SupplierResponse getByCode(@PathVariable String code);
}
