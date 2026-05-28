package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车载节点更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeUpdateCmd {

    /**
     * 节点名称
     */
    private String name;

    /**
     * 本地化名称
     */
    private String nameLocal;

    /**
     * 描述
     */
    private String description;

    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 功能域
     */
    private String functionalDomain;

    /**
     * 设备分类
     */
    private String deviceCategory;

    /**
     * 是否核心节点
     */
    private Boolean isCoreNode;

    /**
     * OTA支持类型
     */
    private String otaSupportType;

    /**
     * HSM能力
     */
    private String hsmCapability;

    /**
     * 信息安全等级
     */
    private String securityLevel;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 修改人
     */
    private String modifyBy;
}
