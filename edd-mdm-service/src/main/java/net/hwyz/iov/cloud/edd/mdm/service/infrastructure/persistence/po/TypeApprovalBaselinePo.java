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
 * 型式批准基线持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_eead_type_approval_baseline")
public class TypeApprovalBaselinePo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taBaselineCode;
    private String swinCode;
    private String anchorType;
    private String anchorCode;
    private String status;
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
}
