package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SWIN定义创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionCreateCmd {
    @NotBlank(message = "SWIN编码不能为空")
    private String swinCode;
    @NotBlank(message = "编码方案代码不能为空")
    private String schemeCode;
    @NotBlank(message = "引用类型不能为空")
    private String typeRefType;
    @NotBlank(message = "引用代码不能为空")
    private String typeRefCode;
    @NotBlank(message = "名称不能为空")
    private String name;
    private String nameLocal;
    private String description;
    private String createBy;
}
