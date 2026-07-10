package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.SoftwareBaselineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class SoftwareBaselineServiceFallbackFactory implements FallbackFactory<SoftwareBaselineService> {

    @Override
    public SoftwareBaselineService create(Throwable cause) {
        log.error("SoftwareBaselineService 调用降级", cause);
        return new SoftwareBaselineService() {
            @Override
            public SoftwareBaselinePageResponse snapshot(Boolean includeDraft, Boolean includeSuperseded, Integer page, Integer size) {
                return SoftwareBaselinePageResponse.empty();
            }

            @Override
            public SoftwareBaselineResponse getByCode(String code) {
                return null;
            }

            @Override
            public java.util.List<SoftwareBaselineResponse> listByAnchor(String anchorType, String anchorCode) {
                return Collections.emptyList();
            }

            @Override
            public java.util.List<SoftwareBaselineResponse> listByPart(String partCode) {
                return Collections.emptyList();
            }
        };
    }
}
