package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 选项族历史版本响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionFamilyHistoryResponse {

    private Long snapshotId;
    private Long entityId;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String category;
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
