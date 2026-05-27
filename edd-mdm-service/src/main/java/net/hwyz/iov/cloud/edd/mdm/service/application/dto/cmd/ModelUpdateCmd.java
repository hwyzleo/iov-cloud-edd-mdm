package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车型更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelUpdateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String modelYear;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String modifyBy;
}
