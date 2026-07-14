package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SWIN定义添加受管系统命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinManagedSystemAddCmd {
    @NotBlank(message = "车载节点代码不能为空")
    private String vehicleNodeCode;
    private Boolean isTypeApprovalRelevant;
    private String createBy;
}
