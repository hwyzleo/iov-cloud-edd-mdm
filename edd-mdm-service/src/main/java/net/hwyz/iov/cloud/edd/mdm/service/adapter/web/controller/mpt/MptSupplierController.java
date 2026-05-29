package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SupplierAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SupplierUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SupplierHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SupplierAppService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SupplierResponse;
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
 * 供应商管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/supplier/v1")
@RequiredArgsConstructor
public class MptSupplierController {

    private final SupplierAppService supplierAppService;
    private final SupplierAssembler supplierAssembler;

    /**
     * 创建供应商
     *
     * @param cmd 创建命令
     * @return 供应商响应
     */
    @PostMapping("/create")
    public ApiResponse<SupplierResponse> create(@RequestBody SupplierCreateCmd cmd) {
        SupplierDto supplier = supplierAppService.createSupplier(cmd);
        return ApiResponse.ok(supplierAssembler.toResponse(supplier));
    }

    /**
     * 更新供应商
     *
     * @param code 供应商code
     * @param cmd  更新命令
     * @return 供应商响应
     */
    @PutMapping("/{code}")
    public ApiResponse<SupplierResponse> update(@PathVariable String code, @RequestBody SupplierUpdateCmd cmd) {
        cmd.setCode(code);
        SupplierDto supplier = supplierAppService.updateSupplier(cmd);
        return ApiResponse.ok(supplierAssembler.toResponse(supplier));
    }

    /**
     * 删除供应商
     *
     * @param code     供应商code
     * @param modifyBy 修改人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        supplierAppService.deleteSupplier(code, modifyBy);
        return ApiResponse.ok();
    }

    /**
     * 失效供应商
     *
     * @param code     供应商code
     * @param modifyBy 修改人
     * @return 供应商响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<SupplierResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        SupplierDto supplier = supplierAppService.deactivateSupplier(code, modifyBy);
        return ApiResponse.ok(supplierAssembler.toResponse(supplier));
    }

    /**
     * 查询供应商详情
     *
     * @param code 供应商code
     * @return 供应商响应
     */
    @GetMapping("/{code}")
    public ApiResponse<SupplierResponse> getByCode(@PathVariable String code) {
        SupplierDto supplier = supplierAppService.getSupplierByCode(code);
        return ApiResponse.ok(supplierAssembler.toResponse(supplier));
    }

    /**
     * 查询所有ACTIVE供应商
     *
     * @return 供应商响应列表
     */
    @GetMapping("/listAll")
    public ApiResponse<List<SupplierResponse>> listAll() {
        List<SupplierDto> dtoList = supplierAppService.listAllActive();
        List<SupplierResponse> rows = dtoList.stream()
                .map(supplierAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 分页查询供应商列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 供应商分页响应
     */
    @GetMapping("/list")
    public ApiResponse<SupplierPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<SupplierDto> suppliers = supplierAppService.listSuppliers(
                net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SupplierQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = supplierAppService.countSuppliers(includeInactiveFlag);

        List<SupplierResponse> rows = suppliers.stream()
                .map(supplierAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(SupplierPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询供应商历史版本列表
     *
     * @param code 供应商code
     * @return 供应商历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<SupplierHistoryPageResponse> getHistory(@PathVariable String code) {
        List<SupplierHistoryDto> historyList = supplierAppService.listSupplierHistory(code);

        List<SupplierHistoryResponse> rows = historyList.stream()
                .map(supplierAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(SupplierHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
