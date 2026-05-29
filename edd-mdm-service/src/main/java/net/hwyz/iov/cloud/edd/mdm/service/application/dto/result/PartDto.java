package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 零件DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务主键（code）
     */
    private String code;

    /**
     * 零件名称
     */
    private String name;

    /**
     * 本地化名称
     */
    private String nameLocal;

    /**
     * 描述
     */
    private String description;

    /**
     * 物料分类编码
     */
    private String categoryCode;

    /**
     * 零件类型
     */
    private String partType;

    /**
     * 车辆节点编码
     */
    private String vehicleNodeCode;

    /**
     * 供应商编码
     */
    private String supplierCode;

    /**
     * 是否软件
     */
    private Boolean isSoftware;

    /**
     * 是否支持FOTA升级
     */
    private Boolean fotaUpgradeable;

    /**
     * 是否安全关键件
     */
    private Boolean isSafetyCritical;

    /**
     * 关重特性（KEY/MAJOR/SIMPLE）
     */
    private String isKeyPart;

    /**
     * 是否法规件
     */
    private Boolean isRegulatoryPart;

    /**
     * 是否架构件
     */
    private Boolean isFramePart;

    /**
     * 是否精准追溯
     */
    private Boolean isAccuratelyTraced;

    /**
     * 功能配置特征码
     */
    private String ffaCode;

    /**
     * 功能配置特征描述
     */
    private String ffaDesc;

    /**
     * 是否有数模
     */
    private Boolean isDigitate;

    /**
     * 初始车型
     */
    private String initialModel;

    /**
     * 对应生产件号
     */
    private String productionCode;

    /**
     * 首次投产时间
     */
    private Date firstProductionDate;

    /**
     * 设计工程师
     */
    private String designer;

    /**
     * 设计工程师部门
     */
    private String designerDept;

    /**
     * 计量单位
     */
    private String uom;

    /**
     * 图纸编号
     */
    private String drawingNo;

    /**
     * 图纸版本
     */
    private String drawingVersion;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 重量单位
     */
    private String weightUom;

    /**
     * 生命周期阶段
     */
    private String lifecycleStage;

    /**
     * 替代零件编码
     */
    private String substitutePartCode;

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
