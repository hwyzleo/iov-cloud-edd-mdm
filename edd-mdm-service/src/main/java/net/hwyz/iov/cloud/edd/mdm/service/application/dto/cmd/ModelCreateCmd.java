package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车型创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCreateCmd {

    private String code;
    private String name;
    private String nameLocal;
    private String carLineCode;
    private String platformCode;
    private String modelYear;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String createBy;
}
