package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 零件简要响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartBriefResponse {

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 零件类型
     */
    private String partType;

    /**
     * 物料分类编码
     */
    private String categoryCode;

    /**
     * 生命周期阶段
     */
    private String lifecycleStage;

    /**
     * 状态
     */
    private String status;
}
