package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.FunctionalDomain;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.HsmCapability;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.NodeType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.OtaSupportType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SecurityLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.VehicleNodeStatus;

import java.util.Date;

/**
 * 车载节点聚合根（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleNode {

    /**
     * 主键
     */
    private Long id;

    // === 身份属性 ===

    /**
     * 业务主键，全局唯一，不可变（如 TBOX / CCP / IDCM / LIDAR_F）
     */
    private String nodeCode;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 本地化名称
     */
    private String nameLocal;

    /**
     * 描述/备注
     */
    private String description;

    // === 分类属性 ===

    /**
     * 节点类型枚举
     */
    private NodeType nodeType;

    /**
     * 功能域枚举
     */
    private FunctionalDomain functionalDomain;

    /**
     * 设备分类（比 nodeType 更细的子分类）
     */
    private String deviceCategory;

    // === 能力声明 ===

    /**
     * 是否核心节点
     */
    private Boolean isCoreNode;

    /**
     * OTA 支持类型枚举
     */
    private OtaSupportType otaSupportType;

    /**
     * HSM 能力枚举（可选）
     */
    private HsmCapability hsmCapability;

    /**
     * 信息安全等级枚举（可选，对齐 ISO/SAE 21434）
     */
    private SecurityLevel securityLevel;

    // === MDM 同步与治理字段（与 Product / Party MDM 各表同构） ===

    /**
     * 数据来源：MDM / MANUAL，默认 MANUAL
     */
    private String source;

    /**
     * 上游系统主键（source=MANUAL 时为 NULL）
     */
    private String externalRefId;

    /**
     * 上游系统版本号
     */
    private Long externalVersion;

    /**
     * 最后同步时间
     */
    private Date lastSyncTime;

    // === 通用治理字段 ===

    /**
     * 业务版本号，每次变更 +1
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
    private VehicleNodeStatus status;

    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    // ====================================================================
    // 业务方法
    // ====================================================================

    /**
     * 创建车载节点
     * <p>
     * 默认 status=ACTIVE、source=MANUAL（沿用 Supplier 风格）
     * 业务规则：effectiveFrom <= effectiveTo（若两者均不为空）
     */
    public static VehicleNode create(String nodeCode, String name, String nameLocal, String description,
                                     NodeType nodeType, FunctionalDomain functionalDomain, String deviceCategory,
                                     Boolean isCoreNode, OtaSupportType otaSupportType,
                                     HsmCapability hsmCapability, SecurityLevel securityLevel,
                                     Date effectiveFrom, Date effectiveTo, String createBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        Date now = new Date();
        return VehicleNode.builder()
                .nodeCode(nodeCode).name(name).nameLocal(nameLocal).description(description)
                .nodeType(nodeType).functionalDomain(functionalDomain).deviceCategory(deviceCategory)
                .isCoreNode(isCoreNode != null ? isCoreNode : false)
                .otaSupportType(otaSupportType).hsmCapability(hsmCapability).securityLevel(securityLevel)
                .source("MANUAL").externalRefId(null).externalVersion(null).lastSyncTime(now)
                .version(1).effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .status(VehicleNodeStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .build();
    }

    /**
     * 更新车载节点（nodeCode 不可变）
     */
    public void update(String name, String nameLocal, String description,
                       NodeType nodeType, FunctionalDomain functionalDomain, String deviceCategory,
                       Boolean isCoreNode, OtaSupportType otaSupportType,
                       HsmCapability hsmCapability, SecurityLevel securityLevel,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        validateEffectiveDate(effectiveFrom, effectiveTo);
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.nodeType = nodeType;
        this.functionalDomain = functionalDomain;
        this.deviceCategory = deviceCategory;
        if (isCoreNode != null) {
            this.isCoreNode = isCoreNode;
        }
        this.otaSupportType = otaSupportType;
        this.hsmCapability = hsmCapability;
        this.securityLevel = securityLevel;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 失效车载节点（status=ACTIVE → INACTIVE）
     */
    public void deactivate(String modifyBy) {
        if (this.status != VehicleNodeStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的车载节点才能失效");
        }
        this.status = VehicleNodeStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 标记审计字段（用于物理删除前持久化最后一份审计记录）
     */
    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 校验生效期（effectiveFrom <= effectiveTo）
     */
    private static void validateEffectiveDate(Date effectiveFrom, Date effectiveTo) {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new IllegalArgumentException("生效开始时间不能晚于生效结束时间");
        }
    }
}
