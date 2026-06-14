package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.DeviceCategoryStatus;

import java.util.Date;

/**
 * 设备类别历史快照实体（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryHistory {

    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private Integer sortOrder;
    private String source;
    private String externalRefId;
    private Long externalVersion;
    private Date lastSyncTime;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private DeviceCategoryStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
