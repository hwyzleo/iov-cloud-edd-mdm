package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.BrandServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 品牌相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "brandService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/brand/v1", fallbackFactory = BrandServiceFallbackFactory.class)
public interface BrandService {

    /**
     * 获取品牌全量快照
     *
     * @param page             页码
     * @param size             每页大小
     * @param includeInactive  是否包含失效记录
     * @return 品牌分页响应
     */
    @GetMapping("/listAll")
    BrandPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "100") Integer size,
                              @RequestParam(required = false) Boolean includeInactive);

    /**
     * 根据code获取品牌信息
     *
     * @param code 品牌code
     * @return 品牌响应
     */
    @GetMapping("/{code}")
    BrandResponse getByCode(@PathVariable String code);
}
