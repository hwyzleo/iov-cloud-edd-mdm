package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN编码方案响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinSchemeResponse {
    private Long id;
    private String code;
    private String name;
    private String nameLocal;
    private String description;
    private String route;
    private Integer sortOrder;
    private String source;
    private String externalRefId;
    private Long externalVersion;
    private Date lastSyncTime;
    private Integer version;
    private Date effectiveFrom;
    private Date effectiveTo;
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
}
