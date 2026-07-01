package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * SWIN管理软件系统响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwinManagedSystemResponse {
    private Long id;
    private String swinCode;
    private String vehicleNodeCode;
    private String createBy;
    private Date createTime;
    private String modifyBy;
    private Date modifyTime;
}
