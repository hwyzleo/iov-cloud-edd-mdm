package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 配置历史版本分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationHistoryPageResponse {

    private Long total;
    private List<ConfigurationHistoryResponse> rows;

    public static ConfigurationHistoryPageResponse empty() {
        return ConfigurationHistoryPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
