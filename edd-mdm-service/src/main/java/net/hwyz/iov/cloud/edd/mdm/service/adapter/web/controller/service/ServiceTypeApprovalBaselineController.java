package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.TypeApprovalBaselineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.TypeApprovalBaselineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TypeApprovalBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.TypeApprovalBaselineAppService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 型式批准基线服务控制器（Service 层，供其他服务调用）
 *
 * @author hwyz_leo
 */
@RestController
@RequiredArgsConstructor
public class ServiceTypeApprovalBaselineController implements TypeApprovalBaselineService {

    private final TypeApprovalBaselineAppService typeApprovalBaselineAppService;
    private final TypeApprovalBaselineAssembler typeApprovalBaselineAssembler;

    @Override
    public TypeApprovalBaselineResponse getByCode(String code) {
        TypeApprovalBaselineDto dto = typeApprovalBaselineAppService.getByCode(code);
        return typeApprovalBaselineAssembler.toResponse(dto);
    }

    @Override
    public List<TypeApprovalBaselineResponse> listBySwinCode(String swinCode) {
        List<TypeApprovalBaselineDto> dtos = typeApprovalBaselineAppService.listBySwinCode(swinCode);
        return typeApprovalBaselineAssembler.toResponseList(dtos);
    }
}
