package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 版本创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantCreateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String modelCode;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String createBy;
}
