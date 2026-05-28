package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VMD VehiclePart 反查计数响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePartCountResponse {

    /**
     * 反查的 nodeCode
     */
    private String nodeCode;

    /**
     * 引用数量
     */
    private long referenceCount;

    /**
     * 来源过滤器
     */
    private String sourceFilter;
}
