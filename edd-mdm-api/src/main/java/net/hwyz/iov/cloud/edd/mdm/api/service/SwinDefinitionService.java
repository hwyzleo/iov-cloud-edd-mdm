package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.SwinDefinitionServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * SWIN定义服务Feign接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "swinDefinitionService", value = ServiceNameConstants.EDD_MDM,
        path = "/api/service/mdm/eead/v1/swinDefinition",
        fallbackFactory = SwinDefinitionServiceFallbackFactory.class)
public interface SwinDefinitionService {

    /**
     * 获取SWIN定义快照（用于下游系统启动引导）
     *
     * @param includeInactive 是否包含失效的
     * @param page            页码
     * @param size            每页大小
     * @return SWIN定义分页响应
     */
    @GetMapping("/snapshot")
    SwinDefinitionPageResponse getSnapshot(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int size);

    /**
     * 根据SWIN代码获取SWIN定义
     *
     * @param swinCode SWIN代码
     * @return SWIN定义
     */
    @GetMapping("/{swinCode}")
    SwinDefinitionResponse getSwinDefinitionBySwinCode(@PathVariable String swinCode);

    /**
     * 获取所有有效的SWIN定义
     *
     * @return SWIN定义列表
     */
    @GetMapping("/listAll")
    List<SwinDefinitionResponse> listAllActiveSwinDefinitions();

    /**
     * 根据编码方案代码获取SWIN定义列表
     *
     * @param schemeCode 编码方案代码
     * @return SWIN定义列表
     */
    @GetMapping("/byScheme/{schemeCode}")
    List<SwinDefinitionResponse> getSwinDefinitionsBySchemeCode(@PathVariable String schemeCode);

    /**
     * 根据引用类型和引用代码获取SWIN定义
     *
     * @param typeRefType 引用类型
     * @param typeRefCode 引用代码
     * @return SWIN定义列表
     */
    @GetMapping("/byTypeRef")
    List<SwinDefinitionResponse> getSwinDefinitionsByTypeRef(
            @RequestParam String typeRefType,
            @RequestParam String typeRefCode);

    /**
     * 根据车载节点代码获取SWIN定义列表
     *
     * @param vehicleNodeCode 车载节点代码
     * @return SWIN定义列表
     */
    @GetMapping("/listByNode/{vehicleNodeCode}")
    List<SwinDefinitionResponse> listByNode(@PathVariable String vehicleNodeCode);
}
