package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.ModelService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.ModelAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ModelQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.ModelAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车型服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/model/v1")
@RequiredArgsConstructor
public class ServiceModelController implements ModelService {

    private final ModelAppService modelAppService;
    private final ModelAssembler modelAssembler;

    @Override
    @GetMapping("/listAll")
    public ModelPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "100") Integer size,
                                    @RequestParam(required = false) String carLineCode,
                                    @RequestParam(required = false) String platformCode,
                                    @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<ModelDto> modelList = modelAppService.listModels(
                ModelQuery.builder().page(page).size(size)
                        .carLineCode(carLineCode).platformCode(platformCode)
                        .includeInactive(includeInactiveFlag).build());
        long total = modelAppService.countModels(carLineCode, platformCode, includeInactiveFlag);
        List<ModelResponse> rows = modelList.stream()
                .map(modelAssembler::toResponse).collect(Collectors.toList());
        return ModelPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public ModelResponse getByCode(@PathVariable String code) {
        ModelDto model = modelAppService.getModelByCode(code);
        return modelAssembler.toResponse(model);
    }
}
