package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.TypeApprovalBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.TypeApprovalBaselineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.TaBaselineProjectRequest;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TypeApprovalBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.TypeApprovalBaselineAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 型式批准基线MPT控制器
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/typeApprovalBaseline/v1")
@RequiredArgsConstructor
public class MptTypeApprovalBaselineController {

    private final TypeApprovalBaselineAppService typeApprovalBaselineAppService;
    private final TypeApprovalBaselineAssembler typeApprovalBaselineAssembler;

    /**
     * 触发卷积生成/刷新（DRAFT）
     */
    @PostMapping("/action/project")
    public ApiResponse<TypeApprovalBaselineResponse> project(@RequestBody TaBaselineProjectRequest request) {
        TypeApprovalBaselineDto dto = typeApprovalBaselineAppService.project(request);
        return ApiResponse.ok(typeApprovalBaselineAssembler.toResponse(dto));
    }

    /**
     * DRAFT -> RELEASED
     */
    @PostMapping("/{code}/action/release")
    public ApiResponse<TypeApprovalBaselineResponse> release(@PathVariable("code") String code) {
        TypeApprovalBaselineDto dto = typeApprovalBaselineAppService.release(code);
        return ApiResponse.ok(typeApprovalBaselineAssembler.toResponse(dto));
    }

    /**
     * RELEASED -> FROZEN（型批通过）
     */
    @PostMapping("/{code}/action/freeze")
    public ApiResponse<TypeApprovalBaselineResponse> freeze(@PathVariable("code") String code) {
        TypeApprovalBaselineDto dto = typeApprovalBaselineAppService.freeze(code);
        return ApiResponse.ok(typeApprovalBaselineAssembler.toResponse(dto));
    }

    /**
     * 删除（先反查保护）
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable("code") String code,
                                     @RequestParam(defaultValue = "false") boolean forceDelete) {
        typeApprovalBaselineAppService.delete(code, forceDelete);
        return ApiResponse.ok();
    }

    /**
     * 只读分页查询
     */
    @GetMapping("/list")
    public ApiResponse<TypeApprovalBaselinePageResponse> list(
            @RequestParam(required = false) String swinCode,
            @RequestParam(required = false) String anchorType,
            @RequestParam(required = false) String anchorCode,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<TypeApprovalBaselineDto> dtos = typeApprovalBaselineAppService.list(
                swinCode, anchorType, anchorCode, status, code, page, size);
        long total = typeApprovalBaselineAppService.count(
                swinCode, anchorType, anchorCode, status, code);
        return ApiResponse.ok(typeApprovalBaselineAssembler.toPageResponse(dtos, total));
    }
}
