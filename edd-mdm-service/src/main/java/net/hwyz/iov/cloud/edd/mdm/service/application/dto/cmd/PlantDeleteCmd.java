package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工厂删除命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantDeleteCmd {

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 是否强制删除
     */
    private boolean forceDelete;
}
