package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 零件小修订命令
 * CR-023 新增
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartMinorRevisionCmd {

    /**
     * 当前零件号
     */
    private String code;

    /**
     * 新图纸版本
     */
    private String drawingVersion;

    /**
     * 操作人
     */
    private String operator;
}
