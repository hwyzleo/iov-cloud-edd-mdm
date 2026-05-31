package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionFamilyResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionFamilyAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.OptionFamilyUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.OptionFamilyQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.OptionFamilyAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 选项族管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/optionFamily/v1")
@RequiredArgsConstructor
public class MptOptionFamilyController {

    private final OptionFamilyAppService optionFamilyAppService;
    private final OptionFamilyAssembler optionFamilyAssembler;

    @PostMapping("/create")
    public ApiResponse<OptionFamilyResponse> create(@RequestBody OptionFamilyCreateCmd cmd) {
        OptionFamilyDto dto = optionFamilyAppService.createOptionFamily(cmd);
        return ApiResponse.ok(optionFamilyAssembler.toResponse(dto));
    }

    @PutMapping("/{code}")
    public ApiResponse<OptionFamilyResponse> update(@PathVariable String code, @RequestBody OptionFamilyUpdateCmd cmd) {
        cmd.setCode(code);
        OptionFamilyDto dto = optionFamilyAppService.updateOptionFamily(cmd);
        return ApiResponse.ok(optionFamilyAssembler.toResponse(dto));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        optionFamilyAppService.deleteOptionFamily(code, modifyBy);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<OptionFamilyResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        OptionFamilyDto dto = optionFamilyAppService.deactivateOptionFamily(code, modifyBy);
        return ApiResponse.ok(optionFamilyAssembler.toResponse(dto));
    }

    @GetMapping("/{code}")
    public ApiResponse<OptionFamilyResponse> getByCode(@PathVariable String code) {
        OptionFamilyDto dto = optionFamilyAppService.getOptionFamilyByCode(code);
        return ApiResponse.ok(optionFamilyAssembler.toResponse(dto));
    }

    @GetMapping("/list")
    public ApiResponse<OptionFamilyPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestParam(required = false) Boolean includeInactive,
                                                      @RequestParam(required = false) String category) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<OptionFamilyDto> list = optionFamilyAppService.listOptionFamily(
                OptionFamilyQuery.builder().page(page).size(size).includeInactive(includeInactiveFlag).category(category).build());
        long total = optionFamilyAppService.countOptionFamily(includeInactiveFlag, category);
        List<OptionFamilyResponse> rows = list.stream()
                .map(optionFamilyAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(OptionFamilyPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/{code}/history")
    public ApiResponse<OptionFamilyHistoryPageResponse> getHistory(@PathVariable String code) {
        List<OptionFamilyHistoryDto> historyList = optionFamilyAppService.listOptionFamilyHistory(code);
        List<OptionFamilyHistoryResponse> rows = historyList.stream()
                .map(optionFamilyAssembler::toHistoryResponse).collect(Collectors.toList());
        return ApiResponse.ok(OptionFamilyHistoryPageResponse.builder().total((long) rows.size()).rows(rows).build());
    }
}
