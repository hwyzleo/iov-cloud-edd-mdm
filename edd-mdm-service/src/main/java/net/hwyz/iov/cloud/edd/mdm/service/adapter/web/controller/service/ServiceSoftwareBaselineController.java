package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.SoftwareBaselineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SoftwareBaselineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SoftwareBaselineAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 软件基线 Feign 服务端实现
 *
 * @author hwyz_leo
 */
@RestController
@RequiredArgsConstructor
public class ServiceSoftwareBaselineController implements SoftwareBaselineService {

    private final SoftwareBaselineAppService softwareBaselineAppService;
    private final SoftwareBaselineAssembler softwareBaselineAssembler;

    @Override
    public SoftwareBaselinePageResponse snapshot(Boolean includeDraft, Boolean includeSuperseded,
                                                  Integer page, Integer size) {
        List<SoftwareBaselineDto> dtos = softwareBaselineAppService.snapshot(
                includeDraft != null && includeDraft,
                includeSuperseded != null && includeSuperseded,
                page, size);
        long total = softwareBaselineAppService.snapshotCount(
                includeDraft != null && includeDraft,
                includeSuperseded != null && includeSuperseded);
        return softwareBaselineAssembler.toPageResponse(dtos, total);
    }

    @Override
    public SoftwareBaselineResponse getByCode(String code) {
        SoftwareBaselineDto dto = softwareBaselineAppService.getSoftwareBaselineByCode(code);
        return softwareBaselineAssembler.toResponse(dto);
    }

    @Override
    public List<SoftwareBaselineResponse> listByAnchor(String anchorType, String anchorCode) {
        AnchorType type = AnchorType.valueOf(anchorType);
        return softwareBaselineAppService.listByAnchor(type, anchorCode).stream()
                .map(softwareBaselineAssembler::toResponse)
                .toList();
    }

    @Override
    public List<SoftwareBaselineResponse> listByPart(String partCode) {
        return softwareBaselineAppService.listByPartCode(partCode).stream()
                .map(softwareBaselineAssembler::toResponse)
                .toList();
    }
}
