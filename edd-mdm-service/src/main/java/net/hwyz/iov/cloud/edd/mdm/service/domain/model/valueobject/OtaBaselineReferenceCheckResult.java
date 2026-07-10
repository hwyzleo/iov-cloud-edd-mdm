package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * OTA 基线投影下游引用反查结果值对象（Material 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtaBaselineReferenceCheckResult {

    private String referencingService;
    private long referenceCount;
    private List<String> samples;
    private boolean available;

    public boolean hasReference() {
        return available && referenceCount > 0;
    }

    public static OtaBaselineReferenceCheckResult unavailable(String referencingService) {
        return OtaBaselineReferenceCheckResult.builder()
                .referencingService(referencingService)
                .referenceCount(-1L)
                .samples(Collections.emptyList())
                .available(false)
                .build();
    }

    public static OtaBaselineReferenceCheckResult noReference() {
        return OtaBaselineReferenceCheckResult.builder()
                .referencingService("ota:BaselineProjection")
                .referenceCount(0L)
                .samples(Collections.emptyList())
                .available(true)
                .build();
    }
}
