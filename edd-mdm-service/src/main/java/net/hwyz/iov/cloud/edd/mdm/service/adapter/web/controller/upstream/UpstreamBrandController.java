package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.upstream;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.request.IngestRequest;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.response.IngestResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.IngestCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.IngestionResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.IngestionAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upstream/mdm/brand/v1")
@RequiredArgsConstructor
public class UpstreamBrandController {

    private final IngestionAppService ingestionAppService;

    @PostMapping("/ingest")
    public ApiResponse<IngestResponse> ingest(
            @RequestHeader("X-Source-System") String sourceSystem,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody IngestRequest request) {

        IngestCmd cmd = IngestCmd.builder()
                .sourceSystem(sourceSystem)
                .sourceId(request.getSourceId())
                .sourceVersion(request.getSourceVersion())
                .entityType("BRAND")
                .occurredAt(request.getOccurredAt())
                .payload(request.getPayload())
                .ingestionChannel("FEIGN")
                .messageId(java.util.UUID.randomUUID().toString())
                .build();

        IngestionResult result = ingestionAppService.ingest(cmd, authHeader);

        return ApiResponse.ok(IngestResponse.builder()
                .entityId(result.getEntityId())
                .version(result.getVersion())
                .operationType(result.getOperationType())
                .build());
    }
}
