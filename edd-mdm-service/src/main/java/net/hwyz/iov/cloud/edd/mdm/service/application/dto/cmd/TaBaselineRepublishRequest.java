package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TA基线批量补发请求（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-130）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaBaselineRepublishRequest {

    /**
     * SWIN代码
     */
    private String swinCode;

    /**
     * 锚定层级类型
     */
    private String anchorType;

    /**
     * 锚点编码
     */
    private String anchorCode;

    /**
     * 基线状态
     */
    private String status;

    /**
     * 指定基线编码列表
     */
    private List<String> codes;

    /**
     * 是否全量补发
     */
    private Boolean all;
}
