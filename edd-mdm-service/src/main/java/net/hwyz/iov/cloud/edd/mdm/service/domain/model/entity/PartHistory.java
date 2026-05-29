package net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.KeyPartLevel;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.LifecycleStage;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.PartType;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartHistory {
    private Long snapshotId;
    private Long entityId;
    private String operationType;
    private Date snapshotTime;
    private String operator;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String categoryCode;
    private PartType partType;
    private String vehicleNodeCode;
    private String supplierCode;
    private Boolean isSoftware;
    private Boolean fotaUpgradeable;
    private Boolean isSafetyCritical;
    private KeyPartLevel isKeyPart;
    private Boolean isRegulatoryPart;
    private Boolean isFramePart;
    private Boolean isAccuratelyTraced;
    private String ffaCode;
    private String ffaDesc;
    private Boolean isDigitate;
    private String initialModel;
    private String productionCode;
    private Date firstProductionDate;
    private String designer;
    private String designerDept;
    private String uom;
    private String drawingNo;
    private String drawingVersion;
    private BigDecimal weight;
    private String weightUom;
    private LifecycleStage lifecycleStage;
    private String substitutePartCode;
    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String ingestionChannel;
    private Date ingestionTime;
    private String sourcePayloadHash;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private PartStatus status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private Integer rowVersion;
    private Boolean rowValid;
}
