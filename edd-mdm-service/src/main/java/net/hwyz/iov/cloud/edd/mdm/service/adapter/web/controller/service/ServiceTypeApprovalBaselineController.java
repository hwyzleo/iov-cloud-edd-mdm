package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.MdmTypeApprovalBaselineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.TypeApprovalBaselineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TypeApprovalBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.TypeApprovalBaselineAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 型式批准基线服务控制器（Service 层，供其他服务调用）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/typeApprovalBaseline/v1")
@RequiredArgsConstructor
public class ServiceTypeApprovalBaselineController implements MdmTypeApprovalBaselineService {

    private final TypeApprovalBaselineAppService typeApprovalBaselineAppService;
    private final TypeApprovalBaselineAssembler typeApprovalBaselineAssembler;

    @Override
    @GetMapping("/byCode/{code}")
    public TypeApprovalBaselineResponse getByCode(@PathVariable("code") String code) {
        TypeApprovalBaselineDto dto = typeApprovalBaselineAppService.getByCode(code);
        return typeApprovalBaselineAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/bySwin/{swinCode}")
    public List<TypeApprovalBaselineResponse> listBySwinCode(@PathVariable("swinCode") String swinCode) {
        List<TypeApprovalBaselineDto> dtos = typeApprovalBaselineAppService.listBySwinCode(swinCode);
        return typeApprovalBaselineAssembler.toResponseList(dtos);
    }
}
