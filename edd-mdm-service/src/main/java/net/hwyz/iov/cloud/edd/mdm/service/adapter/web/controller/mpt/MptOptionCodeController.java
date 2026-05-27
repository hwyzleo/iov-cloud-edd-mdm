package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionCodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionCodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionCodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.OptionCodeAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项码管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/optionCode/v1")
@RequiredArgsConstructor
public class MptOptionCodeController {

    private final OptionCodeAppService optionCodeAppService;
    private final OptionCodeAssembler optionCodeAssembler;

    @PostMapping("/create")
    public ApiResponse<OptionCodeResponse> create(@RequestBody OptionCodeCreateCmd cmd) {
        OptionCodeDto dto = optionCodeAppService.createOptionCode(cmd);
        return ApiResponse.ok(optionCodeAssembler.toResponse(dto));
    }

    @PutMapping("/{code}")
    public ApiResponse<OptionCodeResponse> update(@PathVariable String code, @RequestBody OptionCodeUpdateCmd cmd) {
        cmd.setCode(code);
        OptionCodeDto dto = optionCodeAppService.updateOptionCode(cmd);
        return ApiResponse.ok(optionCodeAssembler.toResponse(dto));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        optionCodeAppService.deleteOptionCode(code, modifyBy);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<OptionCodeResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        OptionCodeDto dto = optionCodeAppService.deactivateOptionCode(code, modifyBy);
        return ApiResponse.ok(optionCodeAssembler.toResponse(dto));
    }

    @GetMapping("/{code}")
    public ApiResponse<OptionCodeResponse> getByCode(@PathVariable String code) {
        OptionCodeDto dto = optionCodeAppService.getOptionCodeByCode(code);
        return ApiResponse.ok(optionCodeAssembler.toResponse(dto));
    }

    @GetMapping("/list")
    public ApiResponse<OptionCodePageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
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

        return ApiResponse.ok(OptionCodePageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/{code}/history")
    public ApiResponse<OptionCodeHistoryPageResponse> getHistory(@PathVariable String code) {
        List<OptionCodeHistoryDto> historyList = optionCodeAppService.listOptionCodeHistory(code);

        List<OptionCodeHistoryResponse> rows = historyList.stream()
                .map(optionCodeAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(OptionCodeHistoryPageResponse.builder()
                .total((long) rows.size()).rows(rows).build());
    }
}
