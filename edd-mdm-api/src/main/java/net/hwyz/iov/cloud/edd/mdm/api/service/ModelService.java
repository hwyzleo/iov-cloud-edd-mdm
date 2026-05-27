package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.ModelServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 车型相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "modelService", value = ServiceNameConstants.EDD_MDM, path = "/service/model/v1", fallbackFactory = ModelServiceFallbackFactory.class)
public interface ModelService {

    @GetMapping("/listAll")
    ModelPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "100") Integer size,
                             @RequestParam(required = false) String carLineCode,
                             @RequestParam(required = false) String platformCode,
                             @RequestParam(required = false) Boolean includeInactive);

    @GetMapping("/{code}")
    ModelResponse getByCode(@PathVariable String code);
}
