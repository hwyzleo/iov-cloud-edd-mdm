package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.PlantServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 工厂相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "plantService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/plant/v1", fallbackFactory = PlantServiceFallbackFactory.class)
public interface PlantService {

    /**
     * 获取工厂全量快照
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 工厂分页响应
     */
    @GetMapping("/snapshot")
    PlantPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                               @RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size);

    /**
     * 根据code获取工厂信息
     *
     * @param code 工厂code
     * @return 工厂响应
     */
    @GetMapping("/{code}")
    PlantResponse getByCode(@PathVariable("code") String code);

    /**
     * 按工厂类型查询工厂列表
     *
     * @param type 工厂类型
     * @return 工厂简要响应列表
     */
    @GetMapping("/listByType")
    List<PlantBriefResponse> listByType(@RequestParam("type") String type);
}
