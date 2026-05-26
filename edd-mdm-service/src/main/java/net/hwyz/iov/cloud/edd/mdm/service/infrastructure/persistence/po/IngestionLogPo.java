package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_ingestion_log")
public class IngestionLogPo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String messageId;
    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String entityType;
    private String entityCode;
    private String ingestionChannel;
    private Date receivedAt;
    private Date processedAt;
    private String status;
    private String errorCode;
    private String errorMessage;
    private String payloadHash;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
