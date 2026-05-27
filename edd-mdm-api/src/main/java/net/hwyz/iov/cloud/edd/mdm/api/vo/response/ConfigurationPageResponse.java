package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 配置分页响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationPageResponse {

    private Long total;
    private List<ConfigurationResponse> rows;

    public static ConfigurationPageResponse empty() {
        return ConfigurationPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
