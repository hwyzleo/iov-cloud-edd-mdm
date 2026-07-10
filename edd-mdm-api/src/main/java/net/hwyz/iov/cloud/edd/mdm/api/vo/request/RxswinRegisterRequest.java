package net.hwyz.iov.cloud.edd.mdm.api.vo.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记请求VO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegisterRequest {

    private String manifestCode;
    private String swinCode;
    private String manifestDigest;
    private String softwareBaselineCode;
    private Date approvedAt;
}
