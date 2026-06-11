package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.MaterialCategoryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.MaterialCategoryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.MaterialCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.MaterialCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.MaterialCategoryAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料分类服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/materialCategory/v1")
@RequiredArgsConstructor
public class ServiceMaterialCategoryController implements MaterialCategoryService {

    private final MaterialCategoryAppService materialCategoryAppService;
    private final MaterialCategoryAssembler materialCategoryAssembler;

    @Override
    @GetMapping("/snapshot")
    public MaterialCategoryPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        MaterialCategoryQuery query = MaterialCategoryQuery.builder()
                .includeInactive(includeInactiveFlag)
                .page(page)
                .size(size)
                .build();

        List<MaterialCategoryDto> categories = materialCategoryAppService.listMaterialCategories(query);

        long total = materialCategoryAppService.countMaterialCategories(null, includeInactiveFlag);

        List<MaterialCategoryResponse> rows = categories.stream()
                .map(materialCategoryAssembler::toResponse)
                .collect(Collectors.toList());

        return MaterialCategoryPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public MaterialCategoryResponse getByCode(@PathVariable("code") String code) {
        MaterialCategoryDto dto = materialCategoryAppService.getMaterialCategoryByCode(code);
        return materialCategoryAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/tree")
    public List<MaterialCategoryResponse> tree() {
        List<MaterialCategoryDto> dtoList = materialCategoryAppService.tree();
        return dtoList.stream()
                .map(materialCategoryAssembler::toResponse)
                .collect(Collectors.toList());
    }
}
