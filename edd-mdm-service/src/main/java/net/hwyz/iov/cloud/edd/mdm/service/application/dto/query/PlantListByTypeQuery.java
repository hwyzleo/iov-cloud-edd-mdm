package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 按工厂类型查询
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantListByTypeQuery {

    /**
     * 工厂类型
     */
    private String plantType;
}
