package net.hwyz.iov.cloud.edd.mdm.service.application.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * RXSWIN登记DTO
 *
 * @author hwyz_leo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RxswinRegistryDto {

    private Long id;
    private String manifestCode;
    private String manifestDigest;
    private String swinCode;
    private String rxswinValue;
    private String softwareBaselineCode;
    private String status;
    private Date approvedAt;
    private Date registeredAt;
    private String requestSource;
    private String traceId;
    private Integer version;
    private Boolean idempotentHit;
}
