package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.VariantServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 版本相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "variantService", value = ServiceNameConstants.EDD_MDM, path = "/service/variant/v1", fallbackFactory = VariantServiceFallbackFactory.class)
public interface VariantService {

    @GetMapping("/listAll")
    VariantPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "100") Integer size,
                               @RequestParam(required = false) String modelCode,
                               @RequestParam(required = false) String carLineCode,
                               @RequestParam(required = false) String platformCode,
                               @RequestParam(required = false) Boolean includeInactive);

    @GetMapping("/{code}")
    VariantResponse getByCode(@PathVariable String code);

    @GetMapping("/{code}/optionCodes")
    List<OptionCodeResponse> getOptionCodes(@PathVariable String code);
}
