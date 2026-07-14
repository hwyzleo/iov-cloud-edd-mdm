package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.TaBaselineStatus;

import java.util.Date;
import java.util.List;

/**
 * 型式批准基线历史快照实体
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineHistory {

    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    private String taBaselineCode;
    private String swinCode;
    private AnchorType anchorType;
    private String anchorCode;
    private TaBaselineStatus status;
    private String projectionDigest;
    private String sourceBaselineScope;
    private Date effectiveFrom;
    private String remark;
    private Integer version;

    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    private List<TaBaselineItem> itemsSnapshot;
}
