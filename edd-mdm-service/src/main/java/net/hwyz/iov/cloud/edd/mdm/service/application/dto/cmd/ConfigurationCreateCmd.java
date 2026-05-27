package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 配置创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationCreateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String variantCode;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String createBy;
}
