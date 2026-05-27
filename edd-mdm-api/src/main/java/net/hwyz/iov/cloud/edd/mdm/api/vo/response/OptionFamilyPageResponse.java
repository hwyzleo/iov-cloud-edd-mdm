package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 选项族分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionFamilyPageResponse {

    private Long total;
    private List<OptionFamilyResponse> rows;

    public static OptionFamilyPageResponse empty() {
        return OptionFamilyPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
