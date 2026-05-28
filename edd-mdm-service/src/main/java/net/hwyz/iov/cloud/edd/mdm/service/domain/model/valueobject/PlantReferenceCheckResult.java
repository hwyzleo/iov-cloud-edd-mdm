package net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Plant 下游引用反查结果值对象（Org 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantReferenceCheckResult {

    private String referencingService;
    private long referenceCount;
    private List<String> samples;
    private boolean available;

    public boolean hasReference() {
        return available && referenceCount > 0;
    }

    public static PlantReferenceCheckResult unavailable(String referencingService) {
        return PlantReferenceCheckResult.builder()
                .referencingService(referencingService)
                .referenceCount(-1L)
                .samples(Collections.emptyList())
                .available(false)
                .build();
    }
}
