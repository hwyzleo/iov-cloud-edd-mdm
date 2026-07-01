package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * SWIN定义响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinDefinitionResponse {
    private Long id;
    private String swinCode;
    private String schemeCode;
    private String typeRefType;
    private String typeRefCode;
    private String name;
    private String nameLocal;
    private String description;
    private Integer version;
    private String status;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
    private List<SwinManagedSystemResponse> managedSystems;
}
