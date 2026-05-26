package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.SeriesServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 车系相关服务接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "seriesService", value = ServiceNameConstants.EDD_MDM, path = "/service/series/v1", fallbackFactory = SeriesServiceFallbackFactory.class)
public interface SeriesService {

    /**
     * 获取车系全量快照
     *
     * @param page        页码
     * @param size        每页大小
     * @param brandCode   品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 车系分页响应
     */
    @GetMapping("/listAll")
    SeriesPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "100") Integer size,
                               @RequestParam(required = false) String brandCode,
                               @RequestParam(required = false) Boolean includeInactive);

    /**
     * 根据code获取车系信息
     *
     * @param code 车系code
     * @return 车系响应
     */
    @GetMapping("/{code}")
    SeriesResponse getByCode(@PathVariable String code);
}
