package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.InvalidEffectiveDateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PlatformType;

import java.util.Date;

/**
 * 平台聚合根
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Platform {

    /**
     * 主键ID
     */
    private Long id;

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
     * 平台类型
     */
    private PlatformType platformType;

    /**
     * EE架构代号
     */
    private String architecture;

    /**
     * 来源系统
     */
    private String sourceSystem;

    /**
     * 来源ID
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
    private PlatformStatus status;

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

    /**
     * 乐观锁版本号
     */
    private Integer rowVersion;

    /**
     * 行有效标记
     */
    private Boolean rowValid;

    /**
     * 校验生效期合法性
     *
     * @throws InvalidEffectiveDateException 生效期无效异常
     */
    public void validateEffectiveDate() throws InvalidEffectiveDateException {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new InvalidEffectiveDateException("生效开始时间不能晚于生效结束时间");
        }
    }

    /**
     * 创建平台
     *
     * @param code          业务主键
     * @param name          官方名称
     * @param nameLocal     本地化名称
     * @param platformType  平台类型
     * @param architecture  EE架构代号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param createBy      创建人
     * @return 平台聚合根
     */
    public static Platform create(String code, String name, String nameLocal,
                                  PlatformType platformType, String architecture,
                                  Date effectiveFrom, Date effectiveTo, String createBy) {
        Platform platform = Platform.builder()
                .code(code)
                .name(name)
                .nameLocal(nameLocal)
                .platformType(platformType)
                .architecture(architecture)
                .sourceSystem("LOCAL")
                .sourceId(code)
                .sourceVersion(null)
                .ingestionChannel("LOCAL")
                .ingestionTime(new Date())
                .sourcePayloadHash(null)
                .effectiveFrom(effectiveFrom)
                .effectiveTo(effectiveTo)
                .version(1)
                .status(PlatformStatus.ACTIVE)
                .createBy(createBy)
                .createTime(new Date())
                .modifyBy(createBy)
                .modifyTime(new Date())
                .rowVersion(0)
                .rowValid(true)
                .build();

        platform.validateEffectiveDate();
        return platform;
    }

    /**
     * 更新平台
     *
     * @param name          官方名称
     * @param nameLocal     本地化名称
     * @param platformType  平台类型
     * @param architecture  EE架构代号
     * @param effectiveFrom 生效开始时间
     * @param effectiveTo   生效结束时间
     * @param modifyBy      修改人
     */
    public void update(String name, String nameLocal, PlatformType platformType,
                       String architecture, Date effectiveFrom, Date effectiveTo,
                       String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.platformType = platformType;
        this.architecture = architecture;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();

        validateEffectiveDate();
    }

    /**
     * 从上游数据创建平台
     *
     * @param code              业务主键
     * @param name              官方名称
     * @param nameLocal         本地化名称
     * @param platformType      平台类型
     * @param architecture      EE架构代号
     * @param effectiveFrom     生效开始时间
     * @param effectiveTo       生效结束时间
     * @param sourceSystem      来源系统
     * @param sourceId          来源ID
     * @param sourceVersion     来源版本
     * @param ingestionChannel  数据接入渠道
     * @param sourcePayloadHash 来源数据哈希
     * @param createBy          创建人
     * @return 平台聚合根
     */
    public static Platform createFromUpstream(String code, String name, String nameLocal,
                                              PlatformType platformType, String architecture,
                                              Date effectiveFrom, Date effectiveTo,
                                              String sourceSystem, String sourceId, String sourceVersion,
                                              String ingestionChannel, String sourcePayloadHash,
                                              String createBy) {
        Platform platform = Platform.builder()
                .code(code).name(name).nameLocal(nameLocal)
                .platformType(platformType).architecture(architecture)
                .sourceSystem(sourceSystem).sourceId(sourceId).sourceVersion(sourceVersion)
                .ingestionChannel(ingestionChannel).ingestionTime(new Date())
                .sourcePayloadHash(sourcePayloadHash)
                .effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .version(1).status(PlatformStatus.ACTIVE)
                .createBy(createBy).createTime(new Date())
                .modifyBy(createBy).modifyTime(new Date())
                .rowVersion(0).rowValid(true)
                .build();
        platform.validateEffectiveDate();
        return platform;
    }

    /**
     * 从上游数据更新平台
     *
     * @param name              官方名称
     * @param nameLocal         本地化名称
     * @param platformType      平台类型
     * @param architecture      EE架构代号
     * @param effectiveFrom     生效开始时间
     * @param effectiveTo       生效结束时间
     * @param sourceSystem      来源系统
     * @param sourceId          来源ID
     * @param sourceVersion     来源版本
     * @param ingestionChannel  数据接入渠道
     * @param sourcePayloadHash 来源数据哈希
     * @param modifyBy          修改人
     */
    public void updateFromUpstream(String name, String nameLocal, PlatformType platformType,
                                   String architecture, Date effectiveFrom, Date effectiveTo,
                                   String sourceSystem, String sourceId, String sourceVersion,
                                   String ingestionChannel, String sourcePayloadHash,
                                   String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.platformType = platformType;
        this.architecture = architecture;
        this.sourceSystem = sourceSystem;
        this.sourceId = sourceId;
        this.sourceVersion = sourceVersion;
        this.ingestionChannel = ingestionChannel;
        this.ingestionTime = new Date();
        this.sourcePayloadHash = sourcePayloadHash;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
        validateEffectiveDate();
    }

    /**
     * 失效平台
     *
     * @param modifyBy 修改人
     */
    public void deactivate(String modifyBy) {
        if (this.status != PlatformStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的平台才能失效");
        }
        this.status = PlatformStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 删除平台（仅DRAFT状态可删除）
     *
     * @param modifyBy 修改人
     */
    public void delete(String modifyBy) {
        if (this.status != PlatformStatus.DRAFT) {
            throw new IllegalStateException("只有DRAFT状态的平台才能删除");
        }
        this.rowValid = false;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }
}
