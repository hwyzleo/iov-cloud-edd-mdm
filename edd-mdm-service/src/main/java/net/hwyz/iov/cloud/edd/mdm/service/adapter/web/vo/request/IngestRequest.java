package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.request;

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
public class IngestRequest {
    private String sourceId;
    private String sourceVersion;
    private Date occurredAt;
    private Map<String, Object> payload;
}
