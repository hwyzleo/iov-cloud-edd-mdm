package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 选项族更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionFamilyUpdateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String category;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String modifyBy;
}
