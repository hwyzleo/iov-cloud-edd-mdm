package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车系更新命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesUpdateCmd {

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 官方名称
     */
    private String name;

    /**
     * 本地化名称
     */
    private String nameLocal;

    /**
     * 车系类型
     */
    private String seriesType;

    /**
     * 生命周期状态
     */
    private String lifecycleStatus;

    /**
     * 目标市场
     */
    private String targetMarket;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 修改人
     */
    private String modifyBy;
}
