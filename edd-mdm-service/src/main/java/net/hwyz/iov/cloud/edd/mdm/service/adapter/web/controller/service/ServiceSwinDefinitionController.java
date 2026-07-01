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
    public SwinDefinitionPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size) {
        SwinDefinitionQuery query = SwinDefinitionQuery.builder()
                .includeInactive(Boolean.TRUE.equals(includeInactive)).page(page).size(size).build();
        List<SwinDefinitionDto> definitions = swinDefinitionAppService.listSwinDefinitions(query);
        long total = swinDefinitionAppService.countSwinDefinitions(Boolean.TRUE.equals(includeInactive));
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
}
