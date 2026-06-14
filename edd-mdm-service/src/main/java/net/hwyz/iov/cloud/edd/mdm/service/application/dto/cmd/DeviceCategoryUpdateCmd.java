package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCategoryUpdateCmd {
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private Integer sortOrder;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String modifyBy;
}
