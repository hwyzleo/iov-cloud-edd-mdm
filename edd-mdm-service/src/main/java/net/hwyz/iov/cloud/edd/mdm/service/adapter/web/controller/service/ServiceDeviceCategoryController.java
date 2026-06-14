package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.DeviceCategoryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.DeviceCategoryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.DeviceCategoryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.DeviceCategoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.DeviceCategoryAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类别服务端 Controller（EEAD 子域）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/mdm/eead/v1/deviceCategory")
@RequiredArgsConstructor
public class ServiceDeviceCategoryController implements DeviceCategoryService {

    private final DeviceCategoryAppService deviceCategoryAppService;
    private final DeviceCategoryAssembler deviceCategoryAssembler;

    @Override
    @GetMapping("/snapshot")
    public DeviceCategoryPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        DeviceCategoryQuery query = DeviceCategoryQuery.builder()
                .includeInactive(includeInactiveFlag).page(page).size(size).build();
        List<DeviceCategoryDto> categories = deviceCategoryAppService.listDeviceCategories(query);
        long total = deviceCategoryAppService.countDeviceCategories(includeInactiveFlag);
        List<DeviceCategoryResponse> rows = categories.stream()
                .map(deviceCategoryAssembler::toResponse).collect(Collectors.toList());
        return DeviceCategoryPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public DeviceCategoryResponse getByCode(@PathVariable("code") String code) {
        DeviceCategoryDto dto = deviceCategoryAppService.getDeviceCategoryByCode(code);
        return deviceCategoryAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listAll")
    public List<DeviceCategoryResponse> listAll() {
        List<DeviceCategoryDto> dtoList = deviceCategoryAppService.listAllActiveDeviceCategories();
        return dtoList.stream()
                .map(deviceCategoryAssembler::toResponse).collect(Collectors.toList());
    }
}
