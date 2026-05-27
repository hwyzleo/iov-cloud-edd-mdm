package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ModelResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.ModelAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ModelCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ModelUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ModelQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ModelHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.ModelAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车型管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/model/v1")
@RequiredArgsConstructor
public class MptModelController {

    private final ModelAppService modelAppService;
    private final ModelAssembler modelAssembler;

    @PostMapping("/create")
    public ApiResponse<ModelResponse> create(@RequestBody ModelCreateCmd cmd) {
        ModelDto model = modelAppService.createModel(cmd);
        return ApiResponse.ok(modelAssembler.toResponse(model));
    }

    @PutMapping("/{code}")
    public ApiResponse<ModelResponse> update(@PathVariable String code, @RequestBody ModelUpdateCmd cmd) {
        cmd.setCode(code);
        ModelDto model = modelAppService.updateModel(cmd);
        return ApiResponse.ok(modelAssembler.toResponse(model));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        modelAppService.deleteModel(code, modifyBy);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<ModelResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        ModelDto model = modelAppService.deactivateModel(code, modifyBy);
        return ApiResponse.ok(modelAssembler.toResponse(model));
    }

    @GetMapping("/{code}")
    public ApiResponse<ModelResponse> getByCode(@PathVariable String code) {
        ModelDto model = modelAppService.getModelByCode(code);
        return ApiResponse.ok(modelAssembler.toResponse(model));
    }

    @GetMapping("/list")
    public ApiResponse<ModelPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size,
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
        return ApiResponse.ok(ModelPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/{code}/history")
    public ApiResponse<ModelHistoryPageResponse> getHistory(@PathVariable String code) {
        List<ModelHistoryDto> historyList = modelAppService.listModelHistory(code);
        List<ModelHistoryResponse> rows = historyList.stream()
                .map(modelAssembler::toHistoryResponse).collect(Collectors.toList());
        return ApiResponse.ok(ModelHistoryPageResponse.builder().total((long) rows.size()).rows(rows).build());
    }
}
