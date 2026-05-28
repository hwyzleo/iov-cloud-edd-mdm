package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工厂简要响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantBriefResponse {

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 工厂名称
     */
    private String name;

    /**
     * 工厂类型
     */
    private String plantType;

    /**
     * 国家
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 状态
     */
    private String status;
}
