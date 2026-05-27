package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.OptionFamilyService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionFamilyAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionFamilyQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.OptionFamilyAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项族服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/optionFamily/v1")
@RequiredArgsConstructor
public class ServiceOptionFamilyController implements OptionFamilyService {

    private final OptionFamilyAppService optionFamilyAppService;
    private final OptionFamilyAssembler optionFamilyAssembler;

    @Override
    @GetMapping("/listAll")
    public OptionFamilyPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "100") Integer size,
                                            @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<OptionFamilyDto> list = optionFamilyAppService.listOptionFamily(
                OptionFamilyQuery.builder().page(page).size(size).includeInactive(includeInactiveFlag).build());
        long total = optionFamilyAppService.countOptionFamily(includeInactiveFlag);
        List<OptionFamilyResponse> rows = list.stream()
                .map(optionFamilyAssembler::toResponse).collect(Collectors.toList());
        return OptionFamilyPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public OptionFamilyResponse getByCode(@PathVariable String code) {
        OptionFamilyDto dto = optionFamilyAppService.getOptionFamilyByCode(code);
        return optionFamilyAssembler.toResponse(dto);
    }
}
