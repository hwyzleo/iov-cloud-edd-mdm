package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * VMD VehiclePart 反查样本列表响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePartListResponse {

    /**
     * 反查的 nodeCode
     */
    private String nodeCode;

    /**
     * 引用总数
     */
    private long totalCount;

    /**
     * 引用样本
     */
    private List<VehiclePartSampleVO> samples;
}
