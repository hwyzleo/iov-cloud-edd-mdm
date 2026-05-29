package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.MaterialCategoryServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 物料分类相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "materialCategoryService", value = ServiceNameConstants.EDD_MDM, path = "/api/service/mdm/material/v1/category", fallbackFactory = MaterialCategoryServiceFallbackFactory.class)
public interface MaterialCategoryService {

    /**
     * 获取物料分类全量快照
     *
     * @param includeInactive 是否包含失效记录
     * @param page            页码
     * @param size            每页大小
     * @return 物料分类分页响应
     */
    @GetMapping("/snapshot")
    MaterialCategoryPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size);

    /**
     * 根据code获取物料分类信息
     *
     * @param code 物料分类code
     * @return 物料分类响应
     */
    @GetMapping("/{code}")
    MaterialCategoryResponse getByCode(@PathVariable("code") String code);

    /**
     * 获取物料分类树形结构
     *
     * @return 物料分类响应列表
     */
    @GetMapping("/tree")
    List<MaterialCategoryResponse> tree();
}
