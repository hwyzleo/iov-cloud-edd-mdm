package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.TypeApprovalBaselineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 型式批准基线服务降级工厂
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class TypeApprovalBaselineServiceFallbackFactory implements FallbackFactory<TypeApprovalBaselineService> {

    @Override
    public TypeApprovalBaselineService create(Throwable cause) {
        log.error("TypeApprovalBaselineService 调用降级", cause);
        return new TypeApprovalBaselineService() {
            @Override
            public TypeApprovalBaselineResponse getByCode(String code) {
                return null;
            }

            @Override
            public List<TypeApprovalBaselineResponse> listBySwinCode(String swinCode) {
                return Collections.emptyList();
            }
        };
    }
}
