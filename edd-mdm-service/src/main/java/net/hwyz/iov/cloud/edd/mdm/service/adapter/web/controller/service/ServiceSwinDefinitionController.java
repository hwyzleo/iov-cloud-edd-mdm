package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.SwinDefinitionService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SwinDefinitionAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinDefinitionQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinDefinitionDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinDefinitionAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN定义服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/mdm/eead/v1/swinDefinition")
@RequiredArgsConstructor
public class ServiceSwinDefinitionController implements SwinDefinitionService {

    private final SwinDefinitionAppService swinDefinitionAppService;
    private final SwinDefinitionAssembler swinDefinitionAssembler;

    @Override
    @GetMapping("/snapshot")
    public SwinDefinitionPageResponse getSnapshot(@RequestParam(defaultValue = "false") boolean includeInactive,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "100") int size) {
        SwinDefinitionQuery query = SwinDefinitionQuery.builder()
                .includeInactive(includeInactive).page(page).size(size).build();
        List<SwinDefinitionDto> definitions = swinDefinitionAppService.listSwinDefinitions(query);
        long total = swinDefinitionAppService.countSwinDefinitions(includeInactive);
        List<SwinDefinitionResponse> rows = definitions.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
        return SwinDefinitionPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{swinCode}")
    public SwinDefinitionResponse getSwinDefinitionBySwinCode(@PathVariable("swinCode") String swinCode) {
        SwinDefinitionDto dto = swinDefinitionAppService.getSwinDefinitionBySwinCode(swinCode);
        return swinDefinitionAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listAll")
    public List<SwinDefinitionResponse> listAllActiveSwinDefinitions() {
        List<SwinDefinitionDto> dtoList = swinDefinitionAppService.listAllActiveSwinDefinitions();
        return dtoList.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
    }

    @Override
    @GetMapping("/byScheme/{schemeCode}")
    public List<SwinDefinitionResponse> getSwinDefinitionsBySchemeCode(@PathVariable String schemeCode) {
        List<SwinDefinitionDto> dtoList = swinDefinitionAppService.listSwinDefinitionsBySchemeCode(schemeCode);
        return dtoList.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
    }

    @Override
    @GetMapping("/byTypeRef")
    public List<SwinDefinitionResponse> getSwinDefinitionsByTypeRef(@RequestParam String typeRefType,
                                                                     @RequestParam String typeRefCode) {
        List<SwinDefinitionDto> dtoList = swinDefinitionAppService.listSwinDefinitionsByTypeRef(typeRefType, typeRefCode);
        return dtoList.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
    }
}
