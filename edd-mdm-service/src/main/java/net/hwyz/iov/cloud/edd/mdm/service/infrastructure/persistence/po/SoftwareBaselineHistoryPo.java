package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 软件基线历史快照持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_material_software_baseline_history")
public class SoftwareBaselineHistoryPo {

    @TableId(type = IdType.AUTO)
    private Long snapshotId;

    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;

    private String code;
    private String name;
    private String anchorType;
    private String anchorCode;
    private String baselineVersion;
    private String baselineStatus;
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
    private String itemsSnapshot;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
    private Boolean forceDelete;
}
