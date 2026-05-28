package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工厂查询条件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantQuery {

    /**
     * 页码
     */
    private int page;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 工厂类型
     */
    private String plantType;

    /**
     * 国家
     */
    private String country;

    /**
     * 是否包含失效记录
     */
    private boolean includeInactive;
}
