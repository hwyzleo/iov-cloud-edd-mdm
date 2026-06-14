package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.DeviceCategoryServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 设备类别相关服务接口（EEAD 子域）
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "deviceCategoryService", value = ServiceNameConstants.EDD_MDM,
        path = "/api/service/mdm/eead/v1/deviceCategory",
        fallbackFactory = DeviceCategoryServiceFallbackFactory.class)
public interface DeviceCategoryService {

    @GetMapping("/snapshot")
    DeviceCategoryPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/{code}")
    DeviceCategoryResponse getByCode(@PathVariable("code") String code);

    @GetMapping("/listAll")
    List<DeviceCategoryResponse> listAll();
}
