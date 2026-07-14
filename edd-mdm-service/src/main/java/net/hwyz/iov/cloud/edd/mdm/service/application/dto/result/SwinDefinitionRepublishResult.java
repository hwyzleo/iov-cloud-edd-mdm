package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SWIN定义补发结果（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-131）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionRepublishResult {

    /**
     * SWIN代码
     */
    private String swinCode;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 是否已补发
     */
    private Boolean republished;
}
