package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 零件查询条件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartQuery {

    /**
     * 页码
     */
    private int page;

    /**
     * 每页大小
     */
    private int size;

    /**
     * 物料分类编码
     */
    private String categoryCode;

    /**
     * 零件类型
     */
    private String partType;

    /**
     * 车辆节点编码
     */
    private String vehicleNodeCode;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 生命周期阶段
     */
    private String lifecycleStage;

    /**
     * 关重特性（KEY/MAJOR/SIMPLE）
     */
    private String isKeyPart;

    /**
     * 是否包含失效记录
     */
    private boolean includeInactive;
}
