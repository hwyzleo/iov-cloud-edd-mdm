package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.PlantService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PlantAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PlantAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工厂服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/mdm/org/v1/plant")
@RequiredArgsConstructor
public class ServicePlantController implements PlantService {

    private final PlantAppService plantAppService;
    private final PlantAssembler plantAssembler;

    @Override
    @GetMapping("/snapshot")
    public PlantPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<PlantDto> plants = plantAppService.snapshot(includeInactiveFlag, page, size);

        long total = plantAppService.snapshotCount(includeInactiveFlag);

        List<PlantResponse> rows = plants.stream()
                .map(plantAssembler::toResponse)
                .collect(Collectors.toList());

        return PlantPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public PlantResponse getByCode(@PathVariable("code") String code) {
        PlantDto dto = plantAppService.getPlantByCode(code);
        return plantAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listByType")
    public List<PlantBriefResponse> listByType(@RequestParam("type") String type) {
        List<PlantDto> dtoList = plantAppService.listByPlantType(type);
        return dtoList.stream()
                .map(plantAssembler::toBriefResponse)
                .collect(Collectors.toList());
    }
}
