package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 品牌创建命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandCreateCmd {

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
     * 品牌描述
     */
    private String description;

    /**
     * Logo URL
     */
    private String logo;

    /**
     * 国家
     */
    private String country;

    /**
     * 创立年份
     */
    private Integer foundedYear;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 创建人
     */
    private String createBy;
}
