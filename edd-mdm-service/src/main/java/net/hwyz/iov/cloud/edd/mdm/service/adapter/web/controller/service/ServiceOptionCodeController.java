package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.OptionCodeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionCodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionCodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.OptionCodeAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项码服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/optionCode/v1")
@RequiredArgsConstructor
public class ServiceOptionCodeController implements OptionCodeService {

    private final OptionCodeAppService optionCodeAppService;
    private final OptionCodeAssembler optionCodeAssembler;

    @Override
    @GetMapping("/listAll")
    public OptionCodePageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "100") Integer size,
                                          @RequestParam(required = false) String optionFamilyCode,
                                          @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<OptionCodeDto> list = optionCodeAppService.listOptionCode(
                OptionCodeQuery.builder()
                        .page(page).size(size)
                        .optionFamilyCode(optionFamilyCode)
                        .includeInactive(includeInactiveFlag)
                        .build());

        long total = optionCodeAppService.countOptionCode(optionFamilyCode, includeInactiveFlag);

        List<OptionCodeResponse> rows = list.stream()
                .map(optionCodeAssembler::toResponse)
                .collect(Collectors.toList());

        return OptionCodePageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public OptionCodeResponse getByCode(@PathVariable String code) {
        OptionCodeDto dto = optionCodeAppService.getOptionCodeByCode(code);
        return optionCodeAssembler.toResponse(dto);
    }
}
