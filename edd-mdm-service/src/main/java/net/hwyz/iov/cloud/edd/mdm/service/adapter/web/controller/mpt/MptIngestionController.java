package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.vo.response.IngestionLogResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.IngestionLogQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.IngestionLogDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.IngestionAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mpt/mdm/ingestion/v1")
@RequiredArgsConstructor
public class MptIngestionController {

    private final IngestionAppService ingestionAppService;

    @GetMapping("/log")
    public ApiResponse<List<IngestionLogResponse>> listLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sourceSystem,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String status) {

        IngestionLogQuery query = IngestionLogQuery.builder()
                .page(page).size(size)
                .sourceSystem(sourceSystem).entityType(entityType).status(status)
                .build();

        List<IngestionLogDto> logs = ingestionAppService.listIngestionLogs(query);
        return ApiResponse.ok(logs.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/log/{messageId}")
    public ApiResponse<IngestionLogResponse> getLog(@PathVariable String messageId) {
        IngestionLogDto log = ingestionAppService.getIngestionLogByMessageId(messageId);
        return ApiResponse.ok(toResponse(log));
    }

    private IngestionLogResponse toResponse(IngestionLogDto dto) {
        if (dto == null) return null;
        return IngestionLogResponse.builder()
                .id(dto.getId()).messageId(dto.getMessageId())
                .sourceSystem(dto.getSourceSystem()).sourceId(dto.getSourceId())
                .sourceVersion(dto.getSourceVersion()).entityType(dto.getEntityType())
                .entityCode(dto.getEntityCode()).ingestionChannel(dto.getIngestionChannel())
                .receivedAt(dto.getReceivedAt()).processedAt(dto.getProcessedAt())
                .status(dto.getStatus()).errorCode(dto.getErrorCode())
                .errorMessage(dto.getErrorMessage()).payloadHash(dto.getPayloadHash())
                .build();
    }
}
