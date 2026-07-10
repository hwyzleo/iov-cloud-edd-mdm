package net.hwyz.iov.cloud.edd.mdm.api.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记响应VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegistryResponse {

    private String manifestCode;
    private String swinCode;
    private String rxswinValue;
    private String softwareBaselineCode;
    private String status;
    private Boolean idempotentHit;
    private Date registeredAt;
}
