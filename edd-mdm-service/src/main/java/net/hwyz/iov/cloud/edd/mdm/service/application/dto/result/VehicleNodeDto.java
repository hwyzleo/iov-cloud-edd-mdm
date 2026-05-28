package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车载节点DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务主键
     */
    private String nodeCode;

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
     * 数据来源
     */
    private String source;

    /**
     * 上游系统主键
     */
    private String externalRefId;

    /**
     * 上游系统版本号
     */
    private Long externalVersion;

    /**
     * 最后同步时间
     */
    private Date lastSyncTime;

    /**
     * 业务版本号
     */
    private Integer version;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;
}
