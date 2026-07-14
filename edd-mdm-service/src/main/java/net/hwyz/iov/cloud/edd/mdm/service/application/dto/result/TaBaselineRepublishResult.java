package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TA基线补发结果（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-129）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineRepublishResult {

    /**
     * 基线编码
     */
    private String code;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 是否已补发
     */
    private Boolean republished;
}
