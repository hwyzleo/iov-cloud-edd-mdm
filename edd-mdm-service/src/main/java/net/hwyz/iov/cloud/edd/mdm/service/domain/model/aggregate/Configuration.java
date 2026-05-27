package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.InvalidEffectiveDateException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.ConfigurationStatus;

import java.util.Date;

/**
 * 配置聚合根
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String variantCode;
    private String description;
    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private ConfigurationStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    public void validateEffectiveDate() throws InvalidEffectiveDateException {
        if (effectiveFrom != null && effectiveTo != null && effectiveFrom.after(effectiveTo)) {
            throw new InvalidEffectiveDateException("生效开始时间不能晚于生效结束时间");
        }
    }

    public static Configuration create(String code, String name, String nameLocal, String variantCode,
                                       String description, Date effectiveFrom, Date effectiveTo, String createBy) {
        Configuration configuration = Configuration.builder()
                .code(code).name(name).nameLocal(nameLocal).variantCode(variantCode).description(description)
                .sourceSystem("LOCAL").sourceId(code).sourceVersion(null)
                .ingestionChannel("LOCAL").ingestionTime(new Date()).sourcePayloadHash(null)
                .effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .version(1).status(ConfigurationStatus.ACTIVE)
                .createBy(createBy).createTime(new Date())
                .modifyBy(createBy).modifyTime(new Date())
                .rowVersion(0).rowValid(true)
                .build();
        configuration.validateEffectiveDate();
        return configuration;
    }

    public void update(String name, String nameLocal, String description,
                       Date effectiveFrom, Date effectiveTo, String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
        validateEffectiveDate();
    }

    public static Configuration createFromUpstream(String code, String name, String nameLocal, String variantCode,
                                                   String description, Date effectiveFrom, Date effectiveTo,
                                                   String sourceSystem, String sourceId, String sourceVersion,
                                                   String ingestionChannel, String sourcePayloadHash, String createBy) {
        Configuration configuration = Configuration.builder()
                .code(code).name(name).nameLocal(nameLocal).variantCode(variantCode).description(description)
                .sourceSystem(sourceSystem).sourceId(sourceId).sourceVersion(sourceVersion)
                .ingestionChannel(ingestionChannel).ingestionTime(new Date()).sourcePayloadHash(sourcePayloadHash)
                .effectiveFrom(effectiveFrom).effectiveTo(effectiveTo)
                .version(1).status(ConfigurationStatus.ACTIVE)
                .createBy(createBy).createTime(new Date())
                .modifyBy(createBy).modifyTime(new Date())
                .rowVersion(0).rowValid(true)
                .build();
        configuration.validateEffectiveDate();
        return configuration;
    }

    public void updateFromUpstream(String name, String nameLocal, String description,
                                   Date effectiveFrom, Date effectiveTo,
                                   String sourceSystem, String sourceId, String sourceVersion,
                                   String ingestionChannel, String sourcePayloadHash, String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
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

    public void deactivate(String modifyBy) {
        if (this.status != ConfigurationStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的配置才能失效");
        }
        this.status = ConfigurationStatus.INACTIVE;
        this.effectiveTo = new Date();
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    public void delete(String modifyBy) {
        if (this.status != ConfigurationStatus.DRAFT) {
            throw new IllegalStateException("只有DRAFT状态的配置才能删除");
        }
        this.rowValid = false;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }
}
