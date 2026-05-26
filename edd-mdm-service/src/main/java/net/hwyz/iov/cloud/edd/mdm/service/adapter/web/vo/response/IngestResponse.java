package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestResponse {
    private Long entityId;
    private Integer version;
    private String operationType;
}
