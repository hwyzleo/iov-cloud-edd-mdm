package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * SWIN编码方案分页响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemePageResponse {

    private Long total;
    private List<SwinSchemeResponse> rows;

    public static SwinSchemePageResponse empty() {
        return SwinSchemePageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
