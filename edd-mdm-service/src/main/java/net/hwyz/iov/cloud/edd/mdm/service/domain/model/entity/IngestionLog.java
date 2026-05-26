package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.enums.IngestionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;

import java.util.Date;

/**
 * 数据接入日志实体
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionLog {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 来源版本
     */
    private String sourceVersion;

    /**
     * 实体类型
     */
    private EntityType entityType;

    /**
     * 实体编码
     */
    private String entityCode;

    /**
     * 接入渠道
     */
    private String ingestionChannel;

    /**
     * 接收时间
     */
    private Date receivedAt;

    /**
     * 处理时间
     */
    private Date processedAt;

    /**
     * 接入状态
     */
    private IngestionStatus status;

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 载荷哈希
     */
    private String payloadHash;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 行版本号
     */
    private Integer rowVersion;

    /**
     * 行有效标识
     */
    private Boolean rowValid;
}
