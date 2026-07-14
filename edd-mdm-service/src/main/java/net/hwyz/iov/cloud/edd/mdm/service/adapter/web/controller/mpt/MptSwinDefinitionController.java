package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SwinDefinitionAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinDefinitionUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SwinManagedSystemAddCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SwinDefinitionQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SwinDefinitionDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinDefinitionAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SWIN定义管理控制器（MPT）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/swinDefinition/v1")
@RequiredArgsConstructor
public class MptSwinDefinitionController {

    private final SwinDefinitionAppService swinDefinitionAppService;
    private final SwinDefinitionAssembler swinDefinitionAssembler;

    @PostMapping("/create")
    public ApiResponse<SwinDefinitionResponse> create(@Valid @RequestBody SwinDefinitionCreateCmd cmd) {
        SwinDefinitionDto dto = swinDefinitionAppService.createSwinDefinition(cmd);
        return ApiResponse.ok(swinDefinitionAssembler.toResponse(dto));
    }

    @PutMapping("/{swinCode}")
    public ApiResponse<SwinDefinitionResponse> update(@PathVariable String swinCode, @RequestBody SwinDefinitionUpdateCmd cmd) {
        cmd.setSwinCode(swinCode);
        SwinDefinitionDto dto = swinDefinitionAppService.updateSwinDefinition(cmd);
        return ApiResponse.ok(swinDefinitionAssembler.toResponse(dto));
    }

    @DeleteMapping("/{swinCode}")
    public ApiResponse<Void> delete(@PathVariable String swinCode) {
        swinDefinitionAppService.deleteSwinDefinition(swinCode, null);
        return ApiResponse.ok();
    }

    @PostMapping("/{swinCode}/deactivate")
    public ApiResponse<SwinDefinitionResponse> deactivate(@PathVariable String swinCode) {
        SwinDefinitionDto dto = swinDefinitionAppService.deactivateSwinDefinition(swinCode, null);
        return ApiResponse.ok(swinDefinitionAssembler.toResponse(dto));
    }

    @PostMapping("/{swinCode}/managedSystems")
    public ApiResponse<SwinDefinitionResponse> addManagedSystem(@PathVariable String swinCode,
                                                                 @Valid @RequestBody SwinManagedSystemAddCmd cmd) {
        SwinDefinitionDto dto = swinDefinitionAppService.addManagedSystem(swinCode, cmd);
        return ApiResponse.ok(swinDefinitionAssembler.toResponse(dto));
    }

    @DeleteMapping("/{swinCode}/managedSystems/{vehicleNodeCode}")
    public ApiResponse<Void> removeManagedSystem(@PathVariable String swinCode,
                                                  @PathVariable String vehicleNodeCode) {
        swinDefinitionAppService.removeManagedSystem(swinCode, vehicleNodeCode);
        return ApiResponse.ok();
    }

    @GetMapping("/{swinCode}")
    public ApiResponse<SwinDefinitionResponse> getBySwinCode(@PathVariable String swinCode) {
        SwinDefinitionDto dto = swinDefinitionAppService.getSwinDefinitionBySwinCode(swinCode);
        return ApiResponse.ok(swinDefinitionAssembler.toResponse(dto));
    }

    @GetMapping("/list")
    public ApiResponse<SwinDefinitionPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) Boolean includeInactive) {
        SwinDefinitionQuery query = SwinDefinitionQuery.builder()
                .page(page).size(size).includeInactive(Boolean.TRUE.equals(includeInactive)).build();
        List<SwinDefinitionDto> definitions = swinDefinitionAppService.listSwinDefinitions(query);
        long total = swinDefinitionAppService.countSwinDefinitions(Boolean.TRUE.equals(includeInactive));
        List<SwinDefinitionResponse> rows = definitions.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(SwinDefinitionPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/listAll")
    public ApiResponse<List<SwinDefinitionResponse>> listAll() {
        List<SwinDefinitionDto> dtoList = swinDefinitionAppService.listAllActiveSwinDefinitions();
        List<SwinDefinitionResponse> rows = dtoList.stream()
                .map(swinDefinitionAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }
}
