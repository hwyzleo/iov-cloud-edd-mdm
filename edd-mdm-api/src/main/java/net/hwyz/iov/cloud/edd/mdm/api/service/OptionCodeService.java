package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.OptionCodeServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 选项码相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "optionCodeService", value = ServiceNameConstants.EDD_MDM, path = "/service/optionCode/v1", fallbackFactory = OptionCodeServiceFallbackFactory.class)
public interface OptionCodeService {

    @GetMapping("/listAll")
    OptionCodePageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "100") Integer size,
                                   @RequestParam(required = false) String optionFamilyCode,
                                   @RequestParam(required = false) Boolean includeInactive);

    @GetMapping("/{code}")
    OptionCodeResponse getByCode(@PathVariable String code);
}
