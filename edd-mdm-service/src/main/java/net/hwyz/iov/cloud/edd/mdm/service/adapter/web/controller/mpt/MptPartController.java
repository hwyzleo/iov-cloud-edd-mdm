package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PartAssembler;
import jakarta.validation.Valid;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartGenerationUpgradeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartImportCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartMinorRevisionCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PartUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PartQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PartAppService;
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
 * 零件管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/material/part/v1")
@RequiredArgsConstructor
public class MptPartController {

    private final PartAppService partAppService;
    private final PartAssembler partAssembler;

    /**
     * 创建零件
     *
     * @param cmd 创建命令
     * @return 零件响应
     */
    @PostMapping("/create")
    public ApiResponse<PartResponse> create(@Valid @RequestBody PartCreateCmd cmd) {
        PartDto dto = partAppService.createPart(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 受权限手动指定code创建
     * CR-023 US-080
     *
     * @param cmd 导入命令
     * @return 零件响应
     */
    @PostMapping("/createWithCode")
    public ApiResponse<PartResponse> createWithCode(@RequestBody PartImportCmd cmd) {
        PartDto dto = partAppService.importPart(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 存量批量导入
     * CR-023 US-080
     *
     * @param cmd 导入命令
     * @return 零件响应
     */
    @PostMapping("/import")
    public ApiResponse<PartResponse> importPart(@RequestBody PartImportCmd cmd) {
        PartDto dto = partAppService.importPart(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 代次升级（互换性变更）
     * CR-023 US-074
     *
     * @param code     当前零件号
     * @param operator 操作人
     * @return 新零件响应
     */
    @PostMapping("/{code}/upgradeGeneration")
    public ApiResponse<PartResponse> upgradeGeneration(@PathVariable String code, @RequestParam String operator) {
        PartGenerationUpgradeCmd cmd = PartGenerationUpgradeCmd.builder()
                .code(code)
                .operator(operator)
                .build();
        PartDto dto = partAppService.upgradeGeneration(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 小修订（仅升图纸版本）
     * CR-023 US-075
     *
     * @param code 零件号
     * @param cmd  小修订命令
     * @return 零件响应
     */
    @PutMapping("/{code}/minorRevision")
    public ApiResponse<PartResponse> minorRevision(@PathVariable String code, @RequestBody PartMinorRevisionCmd cmd) {
        cmd.setCode(code);
        PartDto dto = partAppService.minorRevision(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 更新零件
     *
     * @param code 零件code
     * @param cmd  更新命令
     * @return 零件响应
     */
    @PutMapping("/{code}")
    public ApiResponse<PartResponse> update(@PathVariable String code, @RequestBody PartUpdateCmd cmd) {
        cmd.setCode(code);
        PartDto dto = partAppService.updatePart(cmd);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 删除零件
     *
     * @param code     零件code
     * @param operator 操作人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String operator) {
        partAppService.deletePart(code, operator, false);
        return ApiResponse.ok();
    }

    /**
     * 强制删除零件
     *
     * @param code     零件code
     * @param operator 操作人
     */
    @DeleteMapping("/{code}/force")
    public ApiResponse<Void> forceDelete(@PathVariable String code, @RequestParam String operator) {
        partAppService.deletePart(code, operator, true);
        return ApiResponse.ok();
    }

    /**
     * 失效零件
     *
     * @param code     零件code
     * @param modifyBy 修改人
     * @return 零件响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<PartResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        PartDto dto = partAppService.deactivatePart(code, modifyBy);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 查询零件详情
     *
     * @param code 零件code
     * @return 零件响应
     */
    @GetMapping("/{code}")
    public ApiResponse<PartResponse> getByCode(@PathVariable String code) {
        PartDto dto = partAppService.getPartByCode(code);
        return ApiResponse.ok(partAssembler.toResponse(dto));
    }

    /**
     * 分页查询零件列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param categoryCode    物料分类编码
     * @param partType        零件类型
     * @param vehicleNodeCode 车辆节点编码
     * @param supplierCode    供应商编码
     * @param lifecycleStage  生命周期阶段
     * @param includeInactive 是否包含失效记录
     * @return 零件分页响应
     */
    @GetMapping("/list")
    public ApiResponse<PartPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String categoryCode,
                                               @RequestParam(required = false) String partType,
                                               @RequestParam(required = false) String vehicleNodeCode,
                                               @RequestParam(required = false) String supplierCode,
                                               @RequestParam(required = false) String lifecycleStage,
                                               @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<PartDto> parts = partAppService.listParts(
                PartQuery.builder()
                        .page(page)
                        .size(size)
                        .categoryCode(categoryCode)
                        .partType(partType)
                        .vehicleNodeCode(vehicleNodeCode)
                        .supplierCode(supplierCode)
                        .lifecycleStage(lifecycleStage)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = partAppService.countParts(categoryCode, partType, vehicleNodeCode,
                supplierCode, lifecycleStage, includeInactiveFlag);

        List<PartResponse> rows = parts.stream()
                .map(partAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PartPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询所有ACTIVE零件
     *
     * @return 零件响应列表
     */
    @GetMapping("/listAll")
    public ApiResponse<List<PartResponse>> listAll() {
        List<PartDto> dtoList = partAppService.listAllActiveParts();
        List<PartResponse> rows = dtoList.stream()
                .map(partAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 导出零件
     *
     * @return 零件响应列表
     */
    @GetMapping("/export")
    public ApiResponse<List<PartResponse>> export() {
        List<PartDto> dtoList = partAppService.listAllActiveParts();
        List<PartResponse> rows = dtoList.stream()
                .map(partAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 查询零件历史版本列表
     *
     * @param code 零件code
     * @return 零件历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<PartHistoryPageResponse> getHistory(@PathVariable String code) {
        List<PartHistoryDto> historyList = partAppService.listPartHistory(code);

        List<PartHistoryResponse> rows = historyList.stream()
                .map(partAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PartHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
