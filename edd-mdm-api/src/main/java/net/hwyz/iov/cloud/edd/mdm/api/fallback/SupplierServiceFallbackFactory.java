package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.SupplierService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 供应商相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class SupplierServiceFallbackFactory implements FallbackFactory<SupplierService> {

    @Override
    public SupplierService create(Throwable throwable) {
        return new SupplierService() {
            @Override
            public SupplierPageResponse listAll(Integer page, Integer size, Boolean includeInactive) {
                log.error("供应商服务获取供应商全量快照调用失败", throwable);
                return SupplierPageResponse.empty();
            }

            @Override
            public SupplierResponse getByCode(String code) {
                log.error("供应商服务根据code[{}]获取供应商信息调用失败", code, throwable);
                return null;
            }
        };
    }
}
