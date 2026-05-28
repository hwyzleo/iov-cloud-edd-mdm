package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 车载节点删除命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNodeDeleteCmd {

    /**
     * 业务主键
     */
    private String nodeCode;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 是否强制删除
     */
    private boolean forceDelete;
}
