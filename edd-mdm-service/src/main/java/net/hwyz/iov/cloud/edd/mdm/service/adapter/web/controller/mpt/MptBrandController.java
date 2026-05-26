package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.BrandAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.BrandCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.BrandUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.BrandAppService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
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
 * 品牌管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/brand/v1")
@RequiredArgsConstructor
public class MptBrandController {

    private final BrandAppService brandAppService;
    private final BrandAssembler brandAssembler;

    /**
     * 创建品牌
     *
     * @param cmd 创建命令
     * @return 品牌响应
     */
    @PostMapping("/create")
    public ApiResponse<BrandResponse> create(@RequestBody BrandCreateCmd cmd) {
        BrandDto brand = brandAppService.createBrand(cmd);
        return ApiResponse.ok(brandAssembler.toResponse(brand));
    }

    /**
     * 更新品牌
     *
     * @param code 品牌code
     * @param cmd  更新命令
     * @return 品牌响应
     */
    @PutMapping("/{code}")
    public ApiResponse<BrandResponse> update(@PathVariable String code, @RequestBody BrandUpdateCmd cmd) {
        cmd.setCode(code);
        BrandDto brand = brandAppService.updateBrand(cmd);
        return ApiResponse.ok(brandAssembler.toResponse(brand));
    }

    /**
     * 删除品牌
     *
     * @param code     品牌code
     * @param modifyBy 修改人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        brandAppService.deleteBrand(code, modifyBy);
        return ApiResponse.ok();
    }

    /**
     * 失效品牌
     *
     * @param code     品牌code
     * @param modifyBy 修改人
     * @return 品牌响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<BrandResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        BrandDto brand = brandAppService.deactivateBrand(code, modifyBy);
        return ApiResponse.ok(brandAssembler.toResponse(brand));
    }

    /**
     * 查询品牌详情
     *
     * @param code 品牌code
     * @return 品牌响应
     */
    @GetMapping("/{code}")
    public ApiResponse<BrandResponse> getByCode(@PathVariable String code) {
        BrandDto brand = brandAppService.getBrandByCode(code);
        return ApiResponse.ok(brandAssembler.toResponse(brand));
    }

    /**
     * 分页查询品牌列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 品牌分页响应
     */
    @GetMapping("/list")
    public ApiResponse<BrandPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<BrandDto> brands = brandAppService.listBrands(
                net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.BrandQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = brandAppService.countBrands(includeInactiveFlag);

        List<BrandResponse> rows = brands.stream()
                .map(brandAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(BrandPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }
}
