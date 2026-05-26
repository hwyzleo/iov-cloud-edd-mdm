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
 * 事件发件箱持久化对象
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mdm_outbox")
public class OutboxPo {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 聚合类型
     */
    private String aggregateType;

    /**
     * 聚合根ID（code）
     */
    private String aggregateId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * JSON格式事件体
     */
    private String payload;

    /**
     * 事件发生时间
     */
    private Date occurredAt;

    /**
     * 是否已发送
     */
    private Boolean sent;

    /**
     * 发送时间
     */
    private Date sentAt;

    /**
     * 重试次数
     */
    private Integer retryCount;

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
     * 乐观锁版本号
     */
    private Integer rowVersion;

    /**
     * 行有效标记
     */
    private Boolean rowValid;
}
