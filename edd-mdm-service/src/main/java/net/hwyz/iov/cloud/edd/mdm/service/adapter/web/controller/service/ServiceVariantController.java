package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.VariantService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionCodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.VariantAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VariantQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.VariantAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 版本服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/variant/v1")
@RequiredArgsConstructor
public class ServiceVariantController implements VariantService {

    private final VariantAppService variantAppService;
    private final VariantAssembler variantAssembler;
    private final OptionCodeAssembler optionCodeAssembler;

    @Override
    @GetMapping("/listAll")
    public VariantPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "100") Integer size,
                                      @RequestParam(required = false) String modelCode,
                                      @RequestParam(required = false) String carLineCode,
                                      @RequestParam(required = false) String platformCode,
                                      @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<VariantDto> variantList = variantAppService.listVariants(
                VariantQuery.builder().page(page).size(size)
                        .modelCode(modelCode).carLineCode(carLineCode).platformCode(platformCode)
                        .includeInactive(includeInactiveFlag).build());
        long total = variantAppService.countVariants(modelCode, carLineCode, platformCode, includeInactiveFlag);
        List<VariantResponse> rows = variantList.stream()
                .map(variantAssembler::toResponse).collect(Collectors.toList());
        return VariantPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public VariantResponse getByCode(@PathVariable String code) {
        VariantDto variant = variantAppService.getVariantByCode(code);
        return variantAssembler.toResponse(variant);
    }

    @Override
    @GetMapping("/{code}/optionCodes")
    public List<OptionCodeResponse> getOptionCodes(@PathVariable String code) {
        List<OptionCodeDto> list = variantAppService.listOptionCodeDetails(code);
        return list.stream().map(optionCodeAssembler::toResponse).collect(Collectors.toList());
    }
}
