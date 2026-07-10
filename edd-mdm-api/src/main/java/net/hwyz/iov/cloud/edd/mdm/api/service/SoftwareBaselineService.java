package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.SoftwareBaselineServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "softwareBaselineService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/mdm/material/v1/softwareBaseline", fallbackFactory = SoftwareBaselineServiceFallbackFactory.class)
public interface SoftwareBaselineService {

    @GetMapping("/snapshot")
    SoftwareBaselinePageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeDraft,
                                          @RequestParam(defaultValue = "false") Boolean includeSuperseded,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/{code}")
    SoftwareBaselineResponse getByCode(@PathVariable("code") String code);

    @GetMapping("/listByAnchor")
    List<SoftwareBaselineResponse> listByAnchor(@RequestParam("anchorType") String anchorType,
                                                  @RequestParam("anchorCode") String anchorCode);

    @GetMapping("/listByPart")
    List<SoftwareBaselineResponse> listByPart(@RequestParam("partCode") String partCode);
}
