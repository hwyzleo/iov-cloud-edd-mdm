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
 * 型式批准基线项持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_eead_type_approval_baseline_item")
public class TaBaselineItemPo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taBaselineId;
    private String vehicleNodeCode;
    private String partCode;
    private String approvedVersion;
    private String sourceBaselineCode;
    private String createBy;
    private Date createTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
