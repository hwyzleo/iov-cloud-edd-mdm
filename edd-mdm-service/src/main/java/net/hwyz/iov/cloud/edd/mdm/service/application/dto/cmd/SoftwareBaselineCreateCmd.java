package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineCreateCmd {
    @NotBlank(message = "基线名称不能为空")
    private String name;
    @NotBlank(message = "锚定层级不能为空")
    private String anchorType;
    @NotBlank(message = "锚点编码不能为空")
    private String anchorCode;
    @NotBlank(message = "基线版本不能为空")
    private String baselineVersion;
    private String description;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String createBy;
}
