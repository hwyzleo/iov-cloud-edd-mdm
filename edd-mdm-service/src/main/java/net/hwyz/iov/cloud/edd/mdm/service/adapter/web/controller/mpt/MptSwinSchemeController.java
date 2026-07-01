package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinSchemeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SwinSchemeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinSchemeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinSchemeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinSchemeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinSchemeAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN编码方案管理控制器（MPT）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/swinScheme/v1")
@RequiredArgsConstructor
public class MptSwinSchemeController {

    private final SwinSchemeAppService swinSchemeAppService;
    private final SwinSchemeAssembler swinSchemeAssembler;

    @PostMapping("/create")
    public ApiResponse<SwinSchemeResponse> create(@RequestBody SwinSchemeCreateCmd cmd) {
        SwinSchemeDto dto = swinSchemeAppService.createSwinScheme(cmd);
        return ApiResponse.ok(swinSchemeAssembler.toResponse(dto));
    }

    @PutMapping("/{code}")
    public ApiResponse<SwinSchemeResponse> update(@PathVariable String code, @RequestBody SwinSchemeUpdateCmd cmd) {
        cmd.setCode(code);
        SwinSchemeDto dto = swinSchemeAppService.updateSwinScheme(cmd);
        return ApiResponse.ok(swinSchemeAssembler.toResponse(dto));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code) {
        swinSchemeAppService.deleteSwinScheme(code, null);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<SwinSchemeResponse> deactivate(@PathVariable String code) {
        SwinSchemeDto dto = swinSchemeAppService.deactivateSwinScheme(code, null);
        return ApiResponse.ok(swinSchemeAssembler.toResponse(dto));
    }

    @GetMapping("/{code}")
    public ApiResponse<SwinSchemeResponse> getByCode(@PathVariable String code) {
        SwinSchemeDto dto = swinSchemeAppService.getSwinSchemeByCode(code);
        return ApiResponse.ok(swinSchemeAssembler.toResponse(dto));
    }

    @GetMapping("/list")
    public ApiResponse<SwinSchemePageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) Boolean includeInactive) {
        SwinSchemeQuery query = SwinSchemeQuery.builder()
                .page(page).size(size).includeInactive(Boolean.TRUE.equals(includeInactive)).build();
        List<SwinSchemeDto> schemes = swinSchemeAppService.listSwinSchemes(query);
        long total = swinSchemeAppService.countSwinSchemes(Boolean.TRUE.equals(includeInactive));
        List<SwinSchemeResponse> rows = schemes.stream()
                .map(swinSchemeAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(SwinSchemePageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/listAll")
    public ApiResponse<List<SwinSchemeResponse>> listAll() {
        List<SwinSchemeDto> dtoList = swinSchemeAppService.listAllActiveSwinSchemes();
        List<SwinSchemeResponse> rows = dtoList.stream()
                .map(swinSchemeAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }
}
