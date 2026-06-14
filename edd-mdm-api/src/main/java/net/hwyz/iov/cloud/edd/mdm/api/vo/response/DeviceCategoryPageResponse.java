package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 设备类别分页响应（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryPageResponse {

    private Long total;
    private List<DeviceCategoryResponse> rows;

    public static DeviceCategoryPageResponse empty() {
        return DeviceCategoryPageResponse.builder()
                .total(0L)
                .rows(Collections.emptyList())
                .build();
    }
}
