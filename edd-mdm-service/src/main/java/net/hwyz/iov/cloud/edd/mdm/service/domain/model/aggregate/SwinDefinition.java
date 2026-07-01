package net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SWIN定义聚合根（EEAD 子域）
 * <p>
 * 类型级SWIN定义，关联一个编码方案到一个Variant或Model。
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinition {

    private Long id;
    private String swinCode;
    private String schemeCode;
    private String typeRefType;
    private String typeRefCode;
    private String name;
    private String nameLocal;
    private String description;
    private Integer version;
    private SwinDefinitionStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;

    /**
     * 管理的软件系统清单（不持久化到主表，快照时序列化到历史表）
     */
    @Builder.Default
    private List<SwinManagedSystem> managedSystems = new ArrayList<>();

    /**
     * 创建新的SWIN定义
     *
     * @param swinCode      SWIN业务主键
     * @param schemeCode    编码方案代码
     * @param typeRefType   引用类型（VARIANT/MODEL）
     * @param typeRefCode   引用代码
     * @param name          名称
     * @param nameLocal     本地化名称
     * @param description   描述
     * @param createBy      创建人
     * @return 新创建的SwinDefinition实例
     */
    public static SwinDefinition create(String swinCode, String schemeCode, String typeRefType, String typeRefCode,
                                         String name, String nameLocal, String description, String createBy) {
        if (swinCode == null || swinCode.isBlank()) {
            throw new IllegalArgumentException("SWIN代码不能为空");
        }
        if (schemeCode == null || schemeCode.isBlank()) {
            throw new IllegalArgumentException("编码方案代码不能为空");
        }
        if (typeRefType == null || typeRefType.isBlank()) {
            throw new IllegalArgumentException("引用类型不能为空");
        }
        if (!"VARIANT".equals(typeRefType) && !"MODEL".equals(typeRefType)) {
            throw new IllegalArgumentException("引用类型必须是VARIANT或MODEL");
        }
        if (typeRefCode == null || typeRefCode.isBlank()) {
            throw new IllegalArgumentException("引用代码不能为空");
        }
        Date now = new Date();
        return SwinDefinition.builder()
                .swinCode(swinCode).schemeCode(schemeCode)
                .typeRefType(typeRefType).typeRefCode(typeRefCode)
                .name(name).nameLocal(nameLocal).description(description)
                .version(1).status(SwinDefinitionStatus.ACTIVE)
                .createBy(createBy).createTime(now)
                .modifyBy(createBy).modifyTime(now)
                .rowVersion(0).rowValid(true)
                .managedSystems(new ArrayList<>())
                .build();
    }

    /**
     * 更新SWIN定义
     *
     * @param name        名称
     * @param nameLocal   本地化名称
     * @param description 描述
     * @param modifyBy    修改人
     */
    public void update(String name, String nameLocal, String description, String modifyBy) {
        this.name = name;
        this.nameLocal = nameLocal;
        this.description = description;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 使SWIN定义失效
     *
     * @param modifyBy 修改人
     */
    public void deactivate(String modifyBy) {
        if (this.status != SwinDefinitionStatus.ACTIVE) {
            throw new IllegalStateException("只有ACTIVE状态的SWIN定义才能失效");
        }
        this.status = SwinDefinitionStatus.INACTIVE;
        this.version = this.version + 1;
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 标记为待删除
     *
     * @param modifyBy 修改人
     */
    public void markAsDeleting(String modifyBy) {
        this.modifyBy = modifyBy;
        this.modifyTime = new Date();
    }

    /**
     * 添加管理的软件系统
     *
     * @param managedSystem 管理的软件系统
     */
    public void addManagedSystem(SwinManagedSystem managedSystem) {
        this.managedSystems.add(managedSystem);
        this.version = this.version + 1;
        this.modifyTime = new Date();
    }

    /**
     * 移除管理的软件系统
     *
     * @param vehicleNodeCode 车载节点代码
     */
    public void removeManagedSystem(String vehicleNodeCode) {
        this.managedSystems.removeIf(ms -> ms.getVehicleNodeCode().equals(vehicleNodeCode));
        this.version = this.version + 1;
        this.modifyTime = new Date();
    }
}
