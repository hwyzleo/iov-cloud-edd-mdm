package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.ConfigurationServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.ConfigurationByVariantAndOptionCodesRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 配置相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "configurationService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/configuration/v1", fallbackFactory = ConfigurationServiceFallbackFactory.class)
public interface ConfigurationService {

    @GetMapping("/listAll")
    ConfigurationPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "100") Integer size,
                                     @RequestParam(required = false) String variantCode,
                                     @RequestParam(required = false) Boolean includeInactive);

    @GetMapping("/{code}")
    ConfigurationResponse getByCode(@PathVariable String code);

    @GetMapping("/{code}/optionCodes")
    List<OptionCodeResponse> getOptionCodes(@PathVariable String code);

    @PostMapping("/findByOptionCodes")
    List<ConfigurationResponse> findByOptionCodes(@RequestBody List<String> optionCodes);

    @PostMapping("/resolveConfiguration")
    String resolveConfiguration(@RequestBody ConfigurationByVariantAndOptionCodesRequest request);
}
