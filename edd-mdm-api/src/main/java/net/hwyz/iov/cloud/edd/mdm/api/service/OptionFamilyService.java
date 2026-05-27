package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.OptionFamilyServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 选项族相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "optionFamilyService", value = ServiceNameConstants.EDD_MDM, path = "/service/optionFamily/v1", fallbackFactory = OptionFamilyServiceFallbackFactory.class)
public interface OptionFamilyService {

    @GetMapping("/listAll")
    OptionFamilyPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "100") Integer size,
                                     @RequestParam(required = false) Boolean includeInactive);

    @GetMapping("/{code}")
    OptionFamilyResponse getByCode(@PathVariable String code);
}
