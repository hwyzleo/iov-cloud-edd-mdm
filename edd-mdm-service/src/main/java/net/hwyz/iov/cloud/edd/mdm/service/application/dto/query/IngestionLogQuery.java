package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionLogQuery {
    private int page;
    private int size;
    private String sourceSystem;
    private String entityType;
    private String status;
    private String startTime;
    private String endTime;
}
