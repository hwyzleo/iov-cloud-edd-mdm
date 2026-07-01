package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SWIN管理的软件系统值对象（EEAD 子域）
 * <p>
 * 记录SWIN定义管理的软件系统清单。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinManagedSystem {

    private Long id;
    private Long swinDefinitionId;
    private String vehicleNodeCode;
    private String softwareComponentCode;
    private String description;
    private String createBy;
    private java.util.Date createTime;
}
