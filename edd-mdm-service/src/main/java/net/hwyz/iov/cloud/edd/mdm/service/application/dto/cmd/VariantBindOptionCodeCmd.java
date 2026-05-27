package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 版本绑定选项码命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantBindOptionCodeCmd {

    private String variantCode;
    private String optionCodeCode;
    private String optionFamilyCode;
    private String operator;
}
