package net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestCmd {
    private String sourceSystem;
    private String sourceId;
    private String sourceVersion;
    private String entityType;
    private Date occurredAt;
    private Map<String, Object> payload;
    private String ingestionChannel;
    private String messageId;
}
