package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.SwinSchemeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SwinSchemeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinSchemeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinSchemeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinSchemeAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN编码方案服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/mdm/eead/v1/swinScheme")
@RequiredArgsConstructor
public class ServiceSwinSchemeController implements SwinSchemeService {

    private final SwinSchemeAppService swinSchemeAppService;
    private final SwinSchemeAssembler swinSchemeAssembler;

    @Override
    @GetMapping("/snapshot")
    public SwinSchemePageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size) {
        SwinSchemeQuery query = SwinSchemeQuery.builder()
                .includeInactive(Boolean.TRUE.equals(includeInactive)).page(page).size(size).build();
        List<SwinSchemeDto> schemes = swinSchemeAppService.listSwinSchemes(query);
        long total = swinSchemeAppService.countSwinSchemes(Boolean.TRUE.equals(includeInactive));
        List<SwinSchemeResponse> rows = schemes.stream()
                .map(swinSchemeAssembler::toResponse).collect(Collectors.toList());
        return SwinSchemePageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public SwinSchemeResponse getSwinSchemeByCode(@PathVariable("code") String code) {
        SwinSchemeDto dto = swinSchemeAppService.getSwinSchemeByCode(code);
        return swinSchemeAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listAll")
    public List<SwinSchemeResponse> listAllActiveSwinSchemes() {
        List<SwinSchemeDto> dtoList = swinSchemeAppService.listAllActiveSwinSchemes();
        return dtoList.stream()
                .map(swinSchemeAssembler::toResponse).collect(Collectors.toList());
    }
}
