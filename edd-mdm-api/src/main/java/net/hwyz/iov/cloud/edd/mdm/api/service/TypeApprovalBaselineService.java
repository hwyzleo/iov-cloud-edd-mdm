package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.TypeApprovalBaselineServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 型式批准基线服务接口（Service 层，供其他服务调用）
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "typeApprovalBaselineService", value = ServiceNameConstants.EDD_MDM,
        path = "/api/service/mdm/eead/v1/typeApprovalBaseline",
        fallbackFactory = TypeApprovalBaselineServiceFallbackFactory.class)
public interface TypeApprovalBaselineService {

    /**
     * 根据编码查询TA基线
     *
     * @param code TA基线编码
     * @return TA基线响应
     */
    @GetMapping("/byCode/{code}")
    TypeApprovalBaselineResponse getByCode(@PathVariable("code") String code);

    /**
     * 根据SWIN代码查询TA基线列表
     *
     * @param swinCode SWIN代码
     * @return TA基线响应列表
     */
    @GetMapping("/bySwin/{swinCode}")
    List<TypeApprovalBaselineResponse> listBySwinCode(@PathVariable("swinCode") String swinCode);
}
