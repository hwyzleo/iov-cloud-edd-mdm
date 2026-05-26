package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.PlatformServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 平台相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "platformService", value = ServiceNameConstants.EDD_MDM, path = "/service/platform/v1", fallbackFactory = PlatformServiceFallbackFactory.class)
public interface PlatformService {

    /**
     * 获取平台全量快照
     *
     * @param page             页码
     * @param size             每页大小
     * @param includeInactive  是否包含失效记录
     * @return 平台分页响应
     */
    @GetMapping("/listAll")
    PlatformPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "100") Integer size,
                                 @RequestParam(required = false) Boolean includeInactive);

    /**
     * 根据code获取平台信息
     *
     * @param code 平台code
     * @return 平台响应
     */
    @GetMapping("/{code}")
    PlatformResponse getByCode(@PathVariable String code);
}
