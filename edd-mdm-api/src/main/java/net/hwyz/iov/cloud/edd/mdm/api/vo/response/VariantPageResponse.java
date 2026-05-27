package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 版本分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantPageResponse {

    private Long total;
    private List<VariantResponse> rows;

    public static VariantPageResponse empty() {
        return VariantPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
