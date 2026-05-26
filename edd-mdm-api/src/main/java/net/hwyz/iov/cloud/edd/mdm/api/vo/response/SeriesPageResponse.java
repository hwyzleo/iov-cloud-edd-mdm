package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 车系分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesPageResponse {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页数据
     */
    private List<SeriesResponse> rows;

    /**
     * 创建空的分页响应
     *
     * @return 空分页响应
     */
    public static SeriesPageResponse empty() {
        return SeriesPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
