package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 型式批准基线响应
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TypeApprovalBaselineResponse {

    private Long id;
    private String taBaselineCode;
    private String swinCode;
    private String anchorType;
    private String anchorCode;
    private String status;
    private String projectionDigest;
    private String sourceBaselineScope;
    private Date effectiveFrom;
    private String remark;
    private Integer version;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private List<TaBaselineItemResponse> items;
}
