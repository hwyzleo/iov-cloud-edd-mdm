package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 配置历史版本DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationHistoryDto {

    private Long snapshotId;
    private Long entityId;
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
    private String status;
    private String operationType;
    private Date snapshotTime;
    private String operator;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
}
