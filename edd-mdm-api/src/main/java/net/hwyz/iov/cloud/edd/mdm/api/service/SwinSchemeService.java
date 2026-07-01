package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.SwinSchemeServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemeResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * SWIN编码方案服务Feign接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "swinSchemeService", value = ServiceNameConstants.EDD_MDM,
        path = "/api/service/mdm/eead/v1/swinScheme",
        fallbackFactory = SwinSchemeServiceFallbackFactory.class)
public interface SwinSchemeService {

    /**
     * 获取SWIN编码方案快照（用于下游系统启动引导）
     *
     * @param includeInactive 是否包含失效的
     * @param page            页码
     * @param size            每页大小
     * @return SWIN编码方案分页响应
     */
    @GetMapping("/snapshot")
    SwinSchemePageResponse getSnapshot(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size);

    /**
     * 根据代码获取SWIN编码方案
     *
     * @param code 代码
     * @return SWIN编码方案
     */
    @GetMapping("/{code}")
    SwinSchemeResponse getSwinSchemeByCode(@PathVariable String code);

    /**
     * 获取所有有效的SWIN编码方案
     *
     * @return SWIN编码方案列表
     */
    @GetMapping("/listAll")
    List<SwinSchemeResponse> listAllActiveSwinSchemes();
}
