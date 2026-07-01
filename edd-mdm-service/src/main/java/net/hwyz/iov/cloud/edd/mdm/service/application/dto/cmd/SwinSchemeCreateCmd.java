package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN编码方案创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemeCreateCmd {
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String route;
    private String structurePattern;
    private String versionFormat;
    private Integer sortOrder;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String createBy;
}
