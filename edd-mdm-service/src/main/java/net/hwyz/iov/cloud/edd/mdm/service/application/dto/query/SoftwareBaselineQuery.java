package net.hwyz.iov.cloud.edd.mdm.service.application.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareBaselineQuery {
    private int page;
    private int size;
    private String anchorType;
    private String anchorCode;
    private String baselineStatus;
    private String status;
    private boolean includeDraft;
    private boolean includeSuperseded;
}
