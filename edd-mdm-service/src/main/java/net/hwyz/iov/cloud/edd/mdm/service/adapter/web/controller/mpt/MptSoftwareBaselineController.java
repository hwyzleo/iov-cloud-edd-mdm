package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SoftwareBaselineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SoftwareBaselineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineItemBindCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SoftwareBaselineQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SoftwareBaselineAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 软件基线后台管理接口
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/material/softwareBaseline/v1")
@RequiredArgsConstructor
public class MptSoftwareBaselineController {

    private final SoftwareBaselineAppService softwareBaselineAppService;
    private final SoftwareBaselineAssembler softwareBaselineAssembler;

    @PostMapping("/create")
    public ApiResponse<SoftwareBaselineDto> create(@RequestBody SoftwareBaselineCreateCmd cmd) {
        return ApiResponse.ok(softwareBaselineAppService.createSoftwareBaseline(cmd));
    }

    @PutMapping("/{code}")
    public ApiResponse<SoftwareBaselineDto> update(@PathVariable String code,
                                                     @RequestBody SoftwareBaselineUpdateCmd cmd) {
        cmd.setCode(code);
        return ApiResponse.ok(softwareBaselineAppService.updateSoftwareBaseline(cmd));
    }

    @PostMapping("/{code}/items/bind")
    public ApiResponse<SoftwareBaselineDto> bindItem(@PathVariable String code,
                                                      @RequestBody SoftwareBaselineItemBindCmd cmd) {
        cmd.setBaselineCode(code);
        return ApiResponse.ok(softwareBaselineAppService.bindItem(cmd));
    }

    @PostMapping("/{code}/items/unbind")
    public ApiResponse<SoftwareBaselineDto> unbindItem(@PathVariable String code,
                                                        @RequestParam String partCode,
                                                        @RequestParam(required = false) String operator) {
        return ApiResponse.ok(softwareBaselineAppService.unbindItem(code, partCode, operator));
    }

    @PostMapping("/{code}/release")
    public ApiResponse<SoftwareBaselineDto> release(@PathVariable String code,
                                                     @RequestParam(required = false) String releasedBy) {
        return ApiResponse.ok(softwareBaselineAppService.releaseSoftwareBaseline(code, releasedBy));
    }

    @PostMapping("/{code}/supersede")
    public ApiResponse<Void> supersede(@PathVariable String code,
                                        @RequestParam String supersededByCode,
                                        @RequestParam(required = false) String operator) {
        softwareBaselineAppService.supersedeSoftwareBaseline(code, supersededByCode, operator);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code,
                                     @RequestParam(required = false) String operator) {
        softwareBaselineAppService.deleteSoftwareBaseline(code, operator, false);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{code}/force")
    public ApiResponse<Void> forceDelete(@PathVariable String code,
                                          @RequestParam(required = false) String operator) {
        softwareBaselineAppService.deleteSoftwareBaseline(code, operator, true);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{code}")
    public ApiResponse<SoftwareBaselineDto> getByCode(@PathVariable String code) {
        return ApiResponse.ok(softwareBaselineAppService.getSoftwareBaselineByCode(code));
    }

    @GetMapping("/list")
    public ApiResponse<SoftwareBaselinePageResponse> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String anchorType,
            @RequestParam(required = false) String anchorCode,
            @RequestParam(required = false) String baselineStatus,
            @RequestParam(required = false) String status) {
        SoftwareBaselineQuery query = SoftwareBaselineQuery.builder()
                .page(page).size(size)
                .anchorType(anchorType).anchorCode(anchorCode)
                .baselineStatus(baselineStatus).status(status)
                .build();
        List<SoftwareBaselineDto> baselines = softwareBaselineAppService.listSoftwareBaseline(query);
        long total = softwareBaselineAppService.countSoftwareBaseline(query);
        return ApiResponse.ok(softwareBaselineAssembler.toPageResponse(baselines, total));
    }

    /**
     * 导出软件基线列表
     *
     * @return 软件基线响应列表
     */
    @GetMapping("/export")
    public ApiResponse<List<SoftwareBaselineResponse>> export() {
        List<SoftwareBaselineDto> dtoList = softwareBaselineAppService.listAllActive();
        List<SoftwareBaselineResponse> rows = dtoList.stream()
                .map(softwareBaselineAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    @GetMapping("/{code}/history")
    public ApiResponse<SoftwareBaselineHistoryPageResponse> history(@PathVariable String code) {
        List<SoftwareBaselineHistoryDto> historyList = softwareBaselineAppService.findHistoryByCode(code);
        List<SoftwareBaselineHistoryResponse> rows = historyList.stream()
                .map(softwareBaselineAssembler::toHistoryResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(SoftwareBaselineHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
