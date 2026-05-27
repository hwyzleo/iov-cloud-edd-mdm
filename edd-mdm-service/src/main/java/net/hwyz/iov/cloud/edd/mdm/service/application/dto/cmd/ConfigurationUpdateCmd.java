package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 配置更新命令（CR-005：code 不接受入参，仅通过 URL path 参数定位记录）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationUpdateCmd {

    private String name;
    private String nameLocal;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String modifyBy;
}
