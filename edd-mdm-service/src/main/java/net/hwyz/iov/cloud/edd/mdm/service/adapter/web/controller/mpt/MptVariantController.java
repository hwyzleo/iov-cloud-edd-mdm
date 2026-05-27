package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VariantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.VariantAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantBindOptionCodeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VariantUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VariantQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VariantHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.VariantAppService;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.VariantOptionCodeBindingPo;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 版本管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/variant/v1")
@RequiredArgsConstructor
public class MptVariantController {

    private final VariantAppService variantAppService;
    private final VariantAssembler variantAssembler;

    @PostMapping("/create")
    public ApiResponse<VariantResponse> create(@RequestBody VariantCreateCmd cmd) {
        VariantDto variant = variantAppService.createVariant(cmd);
        return ApiResponse.ok(variantAssembler.toResponse(variant));
    }

    @PutMapping("/{code}")
    public ApiResponse<VariantResponse> update(@PathVariable String code, @RequestBody VariantUpdateCmd cmd) {
        cmd.setCode(code);
        VariantDto variant = variantAppService.updateVariant(cmd);
        return ApiResponse.ok(variantAssembler.toResponse(variant));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        variantAppService.deleteVariant(code, modifyBy);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<VariantResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        VariantDto variant = variantAppService.deactivateVariant(code, modifyBy);
        return ApiResponse.ok(variantAssembler.toResponse(variant));
    }

    @GetMapping("/{code}")
    public ApiResponse<VariantResponse> getByCode(@PathVariable String code) {
        VariantDto variant = variantAppService.getVariantByCode(code);
        return ApiResponse.ok(variantAssembler.toResponse(variant));
    }

    @GetMapping("/list")
    public ApiResponse<VariantPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
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
        return ApiResponse.ok(VariantPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/{code}/history")
    public ApiResponse<VariantHistoryPageResponse> getHistory(@PathVariable String code) {
        List<VariantHistoryDto> historyList = variantAppService.listVariantHistory(code);
        List<VariantHistoryResponse> rows = historyList.stream()
                .map(variantAssembler::toHistoryResponse).collect(Collectors.toList());
        return ApiResponse.ok(VariantHistoryPageResponse.builder().total((long) rows.size()).rows(rows).build());
    }

    @PostMapping("/{code}/bind")
    public ApiResponse<Void> bindOptionCode(@PathVariable String code, @RequestBody VariantBindOptionCodeCmd cmd) {
        cmd.setVariantCode(code);
        variantAppService.bindOptionCode(cmd);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/unbind")
    public ApiResponse<Void> unbindOptionCode(@PathVariable String code,
                                             @RequestParam String optionCodeCode,
                                             @RequestParam String operator) {
        variantAppService.unbindOptionCode(code, optionCodeCode, operator);
        return ApiResponse.ok();
    }

    @GetMapping("/{code}/optionCodes")
    public ApiResponse<List<VariantOptionCodeBindingPo>> listOptionCodes(@PathVariable String code) {
        return ApiResponse.ok(variantAppService.listOptionCodes(code));
    }
}
