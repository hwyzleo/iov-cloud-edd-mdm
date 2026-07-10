package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.BaselineStatus;

import java.util.Date;
import java.util.List;

/**
 * 软件基线历史快照实体
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineHistory {

    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    private String code;
    private String name;
    private AnchorType anchorType;
    private String anchorCode;
    private String baselineVersion;
    private BaselineStatus baselineStatus;
    private Date releasedAt;
    private String releasedBy;
    private String supersededByCode;
    private String description;

    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;

    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
    private Boolean forceDelete;

    private List<SoftwareBaselineItem> itemsSnapshot;
}
