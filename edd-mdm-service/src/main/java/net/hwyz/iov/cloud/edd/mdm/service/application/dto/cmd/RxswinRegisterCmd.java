package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记命令
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegisterCmd {

    private String manifestCode;
    private String swinCode;
    private String manifestDigest;
    private String softwareBaselineCode;
    private Date approvedAt;
    private String requestSource;
    private String traceId;
}
