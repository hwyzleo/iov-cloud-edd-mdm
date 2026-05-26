package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.BrandService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.BrandResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.BrandAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.BrandDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.BrandAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/brand/v1")
@RequiredArgsConstructor
public class ServiceBrandController implements BrandService {

    private final BrandAppService brandAppService;
    private final BrandAssembler brandAssembler;

    @Override
    @GetMapping("/listAll")
    public BrandPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "100") Integer size,
                                     @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        // 查询品牌列表
        List<BrandDto> brands = brandAppService.listBrands(
                net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.BrandQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        // 查询总数
        long total = brandAppService.countBrands(includeInactiveFlag);

        // 转换为响应对象
        List<BrandResponse> rows = brands.stream()
                .map(brandAssembler::toResponse)
                .collect(Collectors.toList());

        return BrandPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public BrandResponse getByCode(@PathVariable String code) {
        BrandDto brand = brandAppService.getBrandByCode(code);
        return brandAssembler.toResponse(brand);
    }
}
