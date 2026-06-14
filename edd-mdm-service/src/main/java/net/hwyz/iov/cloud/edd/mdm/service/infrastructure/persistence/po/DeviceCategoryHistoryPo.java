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
 * 设备类别历史快照持久化对象（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_eead_device_category_history")
public class DeviceCategoryHistoryPo {

    @TableId(type = IdType.AUTO)
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
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
