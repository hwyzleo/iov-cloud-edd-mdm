package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.MaterialCategoryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.MaterialCategoryUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.MaterialCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.MaterialCategoryAppService;
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
 * 物料分类管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/material/category/v1")
@RequiredArgsConstructor
public class MptMaterialCategoryController {

    private final MaterialCategoryAppService materialCategoryAppService;
    private final MaterialCategoryAssembler materialCategoryAssembler;

    /**
     * 创建物料分类
     *
     * @param cmd 创建命令
     * @return 物料分类响应
     */
    @PostMapping("/create")
    public ApiResponse<MaterialCategoryResponse> create(@RequestBody MaterialCategoryCreateCmd cmd) {
        MaterialCategoryDto dto = materialCategoryAppService.createMaterialCategory(cmd);
        return ApiResponse.ok(materialCategoryAssembler.toResponse(dto));
    }

    /**
     * 更新物料分类
     *
     * @param code 物料分类code
     * @param cmd  更新命令
     * @return 物料分类响应
     */
    @PutMapping("/{code}")
    public ApiResponse<MaterialCategoryResponse> update(@PathVariable String code, @RequestBody MaterialCategoryUpdateCmd cmd) {
        cmd.setCode(code);
        MaterialCategoryDto dto = materialCategoryAppService.updateMaterialCategory(cmd);
        return ApiResponse.ok(materialCategoryAssembler.toResponse(dto));
    }

    /**
     * 删除物料分类
     *
     * @param code     物料分类code
     * @param operator 操作人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String operator) {
        materialCategoryAppService.deleteMaterialCategory(code, operator);
        return ApiResponse.ok();
    }

    /**
     * 失效物料分类
     *
     * @param code     物料分类code
     * @param modifyBy 修改人
     * @return 物料分类响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<MaterialCategoryResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        MaterialCategoryDto dto = materialCategoryAppService.deactivateMaterialCategory(code, modifyBy);
        return ApiResponse.ok(materialCategoryAssembler.toResponse(dto));
    }

    /**
     * 查询物料分类详情
     *
     * @param code 物料分类code
     * @return 物料分类响应
     */
    @GetMapping("/{code}")
    public ApiResponse<MaterialCategoryResponse> getByCode(@PathVariable String code) {
        MaterialCategoryDto dto = materialCategoryAppService.getMaterialCategoryByCode(code);
        return ApiResponse.ok(materialCategoryAssembler.toResponse(dto));
    }

    /**
     * 分页查询物料分类列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param parentCode      父分类编码
     * @param includeInactive 是否包含失效记录
     * @return 物料分类分页响应
     */
    @GetMapping("/list")
    public ApiResponse<MaterialCategoryPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size,
                                                          @RequestParam(required = false) String parentCode,
                                                          @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<MaterialCategoryDto> categories = materialCategoryAppService.listMaterialCategories(
                MaterialCategoryQuery.builder()
                        .page(page)
                        .size(size)
                        .parentCode(parentCode)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = materialCategoryAppService.countMaterialCategories(parentCode, includeInactiveFlag);

        List<MaterialCategoryResponse> rows = categories.stream()
                .map(materialCategoryAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(MaterialCategoryPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询所有ACTIVE物料分类（树形结构）
     *
     * @return 物料分类响应列表
     */
    @GetMapping("/listAll")
    public ApiResponse<List<MaterialCategoryResponse>> tree() {
        List<MaterialCategoryDto> dtoList = materialCategoryAppService.tree();
        List<MaterialCategoryResponse> rows = dtoList.stream()
                .map(materialCategoryAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 查询物料分类历史版本列表
     *
     * @param code 物料分类code
     * @return 物料分类历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<MaterialCategoryHistoryPageResponse> getHistory(@PathVariable String code) {
        List<MaterialCategoryHistoryDto> historyList = materialCategoryAppService.listMaterialCategoryHistory(code);

        List<MaterialCategoryHistoryResponse> rows = historyList.stream()
                .map(materialCategoryAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(MaterialCategoryHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
