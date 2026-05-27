package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 车型分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPageResponse {

    private Long total;
    private List<ModelResponse> rows;

    public static ModelPageResponse empty() {
        return ModelPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
