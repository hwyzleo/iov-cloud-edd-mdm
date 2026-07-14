package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 软件基线补发结果
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineRepublishResult {

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
