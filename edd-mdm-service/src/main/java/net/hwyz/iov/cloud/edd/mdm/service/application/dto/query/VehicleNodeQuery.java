package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车载节点查询条件
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeQuery {

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 功能域
     */
    private String functionalDomain;

    /**
     * OTA支持类型
     */
    private String otaSupportType;

    /**
     * 是否核心节点
     */
    private Boolean isCoreNode;

    /**
     * 状态
     */
    private String status;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;
}
