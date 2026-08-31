package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.DeviceCategoryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.DeviceCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.DeviceCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryCatalogPreviewResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.DeviceCategoryAppService;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.DeviceCategoryCatalogBootstrap;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类别管理后台 Controller（EEAD 子域）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/deviceCategory/v1")
@RequiredArgsConstructor
public class MptDeviceCategoryController {

    private final DeviceCategoryAppService deviceCategoryAppService;
    private final DeviceCategoryAssembler deviceCategoryAssembler;
    private final DeviceCategoryCatalogBootstrap deviceCategoryCatalogBootstrap;

    /**
     * 标准目录预检（CR-037 §6.1）：返回目录版本、标准设备族数量及冲突
     */
    @GetMapping("/catalog/preview")
    public ApiResponse<DeviceCategoryCatalogPreviewResult> previewCatalog() {
        return ApiResponse.ok(deviceCategoryCatalogBootstrap.preview());
    }

    /**
     * 标准目录 Bootstrap（CR-037 §6.1）：受控幂等初始化 24 个标准设备族，返回统计结果
     */
    @PostMapping("/catalog/bootstrap")
    public ApiResponse<DeviceCategoryCatalogBootstrapResult> bootstrapCatalog() {
        return ApiResponse.ok(deviceCategoryCatalogBootstrap.bootstrap());
    }

    @PostMapping("/create")
    public ApiResponse<DeviceCategoryResponse> create(@RequestBody DeviceCategoryCreateCmd cmd) {
        DeviceCategoryDto dto = deviceCategoryAppService.createDeviceCategory(cmd);
        return ApiResponse.ok(deviceCategoryAssembler.toResponse(dto));
    }

    @PutMapping("/{code}")
    public ApiResponse<DeviceCategoryResponse> update(@PathVariable String code, @RequestBody DeviceCategoryUpdateCmd cmd) {
        cmd.setCode(code);
        DeviceCategoryDto dto = deviceCategoryAppService.updateDeviceCategory(cmd);
        return ApiResponse.ok(deviceCategoryAssembler.toResponse(dto));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String operator) {
        deviceCategoryAppService.deleteDeviceCategory(code, operator);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<DeviceCategoryResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        DeviceCategoryDto dto = deviceCategoryAppService.deactivateDeviceCategory(code, modifyBy);
        return ApiResponse.ok(deviceCategoryAssembler.toResponse(dto));
    }

    @GetMapping("/{code}")
    public ApiResponse<DeviceCategoryResponse> getByCode(@PathVariable String code) {
        DeviceCategoryDto dto = deviceCategoryAppService.getDeviceCategoryByCode(code);
        return ApiResponse.ok(deviceCategoryAssembler.toResponse(dto));
    }

    @GetMapping("/list")
    public ApiResponse<DeviceCategoryPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size,
                                                        @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        DeviceCategoryQuery query = DeviceCategoryQuery.builder()
                .page(page).size(size).includeInactive(includeInactiveFlag).build();
        List<DeviceCategoryDto> categories = deviceCategoryAppService.listDeviceCategories(query);
        long total = deviceCategoryAppService.countDeviceCategories(includeInactiveFlag);
        List<DeviceCategoryResponse> rows = categories.stream()
                .map(deviceCategoryAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(DeviceCategoryPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/listAll")
    public ApiResponse<List<DeviceCategoryResponse>> listAll() {
        List<DeviceCategoryDto> dtoList = deviceCategoryAppService.listAllActiveDeviceCategories();
        List<DeviceCategoryResponse> rows = dtoList.stream()
                .map(deviceCategoryAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    @GetMapping("/{code}/history")
    public ApiResponse<DeviceCategoryPageResponse> getHistory(@PathVariable String code) {
        List<DeviceCategoryHistoryDto> historyList = deviceCategoryAppService.listDeviceCategoryHistory(code);
        List<DeviceCategoryResponse> rows = historyList.stream()
                .map(deviceCategoryAssembler::historyToResponse).collect(Collectors.toList());
        return ApiResponse.ok(DeviceCategoryPageResponse.builder()
                .total((long) rows.size()).rows(rows).build());
    }
}
