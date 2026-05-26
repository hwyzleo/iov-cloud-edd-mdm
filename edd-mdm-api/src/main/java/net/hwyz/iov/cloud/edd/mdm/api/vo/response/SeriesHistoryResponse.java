package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 车系历史版本响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesHistoryResponse {

    /**
     * 快照ID
     */
    private Long snapshotId;

    /**
     * 关联主表id
     */
    private Long entityId;

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
     * 品牌code
     */
    private String brandCode;

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
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 来源系统ID
     */
    private String sourceId;

    /**
     * 来源版本
     */
    private String sourceVersion;

    /**
     * 数据接入渠道
     */
    private String ingestionChannel;

    /**
     * 数据接入时间
     */
    private Date ingestionTime;

    /**
     * 来源数据哈希
     */
    private String sourcePayloadHash;

    /**
     * 业务版本号
     */
    private Integer version;

    /**
     * 生效开始时间
     */
    private Date effectiveFrom;

    /**
     * 生效结束时间
     */
    private Date effectiveTo;

    /**
     * 状态
     */
    private String status;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 快照时间
     */
    private Date snapshotTime;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;
}
