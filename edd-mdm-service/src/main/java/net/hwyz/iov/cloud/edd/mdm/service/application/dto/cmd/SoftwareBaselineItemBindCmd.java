package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineItemBindCmd {
    @NotBlank(message = "基线编码不能为空")
    private String baselineCode;
    @NotBlank(message = "零件编码不能为空")
    private String partCode;
    private String remark;
    private String operator;
}
