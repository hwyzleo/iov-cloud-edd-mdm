package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车载节点历史版本实体（EEAD 子域）
 * <p>
 * 字段与主表保持镜像 + 快照专用字段（snapshotId / entityId / operationType / snapshotTime / operator / forceDelete）。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeHistory {

    private Long snapshotId;
    private Long entityId;
    private String nodeCode;
    private String name;
    private String nameLocal;
    private String description;
    private String nodeType;
    private String functionalDomain;
    private String deviceCategory;
    private Boolean isCoreNode;
    private String otaSupportType;
    private String hsmCapability;
    private String securityLevel;
    private String source;
    private String externalRefId;
    private Long externalVersion;
    private Date lastSyncTime;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    /**
     * force 旁路删除标识（仅 DELETE 操作有意义，US-045）
     */
    private Boolean forceDelete;

    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
