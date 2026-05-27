package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 选项码分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionCodePageResponse {

    private Long total;
    private List<OptionCodeResponse> rows;

    public static OptionCodePageResponse empty() {
        return OptionCodePageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
