package net.hwyz.iov.cloud.edd.mdm.api.service;

import net.hwyz.iov.cloud.edd.mdm.api.fallback.MdmRxswinRegistryServiceFallbackFactory;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.RxswinRegisterRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.RxswinRegistryResponse;
import net.hwyz.iov.cloud.framework.common.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * RXSWIN登记服务Feign接口
 *
 * @author hwyz_leo
 */
@FeignClient(contextId = "mdmRxswinRegistryService", value = ServiceNameConstants.EDD_MDM,
        path = "/api/service/rxswin/v1",
        fallbackFactory = MdmRxswinRegistryServiceFallbackFactory.class)
public interface MdmRxswinRegistryService {

    /**
     * RXSWIN幂等登记
     *
     * @param request 登记请求
     * @return 登记响应
     */
    @PostMapping("/register")
    RxswinRegistryResponse register(@RequestBody RxswinRegisterRequest request);

    /**
     * 根据manifestCode查询登记结果
     *
     * @param manifestCode manifest业务键
     * @return 登记响应
     */
    @GetMapping("/byManifestCode/{manifestCode}")
    RxswinRegistryResponse getByManifestCode(@PathVariable("manifestCode") String manifestCode);
}
