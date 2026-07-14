package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 软件基线批量补发请求
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineRepublishRequest {

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
    private String baselineStatus;

    /**
     * 指定基线编码列表
     */
    private List<String> codes;

    /**
     * 是否全量补发
     */
    private Boolean all;
}
