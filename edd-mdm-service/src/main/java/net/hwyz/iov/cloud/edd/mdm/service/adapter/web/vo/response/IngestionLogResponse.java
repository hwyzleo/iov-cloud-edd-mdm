package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionLogResponse {
    private Long id;
    private String messageId;
    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String entityType;
    private String entityCode;
    private String ingestionChannel;
    private Date receivedAt;
    private Date processedAt;
    private String status;
    private String errorCode;
    private String errorMessage;
    private String payloadHash;
}
