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
 * RXSWIN登记持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_eead_rxswin_registry")
public class RxswinRegistryPo {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String manifestCode;
    private String manifestDigest;
    private String swinCode;
    private String rxswinValue;
    private String softwareBaselineCode;
    private String status;
    private Date approvedAt;
    private Date registeredAt;
    private String requestSource;
    private String traceId;
    private Integer version;
    private String createBy;
    private Date createTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
