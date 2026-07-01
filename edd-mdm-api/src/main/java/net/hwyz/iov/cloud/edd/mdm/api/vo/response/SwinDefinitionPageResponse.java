package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * SWIN定义分页响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionPageResponse {

    private Long total;
    private List<SwinDefinitionResponse> rows;

    public static SwinDefinitionPageResponse empty() {
        return SwinDefinitionPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
