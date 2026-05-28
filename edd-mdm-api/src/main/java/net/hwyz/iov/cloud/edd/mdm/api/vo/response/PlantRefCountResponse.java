package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Plant 引用计数响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantRefCountResponse {

    /**
     * 引用数量；-1 表示反查失败（FallbackFactory 标识）
     */
    private long referenceCount;
}
