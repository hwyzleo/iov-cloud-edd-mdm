package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 配置更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationUpdateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String modifyBy;
}
