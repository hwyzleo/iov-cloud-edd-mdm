package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SWIN定义批量补发请求（EEAD 子域）
 * <p>
 * 落地 MDM-DSN-CR-031（US-132）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionRepublishRequest {

    /**
     * 编码方案代码
     */
    private String schemeCode;

    /**
     * 引用类型
     */
    private String typeRefType;

    /**
     * 引用代码
     */
    private String typeRefCode;

    /**
     * SWIN定义状态
     */
    private String status;

    /**
     * 指定SWIN代码列表
     */
    private List<String> swinCodes;

    /**
     * 是否全量补发
     */
    private Boolean all;
}
