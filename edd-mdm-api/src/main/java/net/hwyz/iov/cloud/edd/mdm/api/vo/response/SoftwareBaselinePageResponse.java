package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselinePageResponse {
    private List<SoftwareBaselineResponse> list;
    private long total;

    public static SoftwareBaselinePageResponse empty() {
        return SoftwareBaselinePageResponse.builder()
                .list(java.util.Collections.emptyList())
                .total(0)
                .build();
    }
}
