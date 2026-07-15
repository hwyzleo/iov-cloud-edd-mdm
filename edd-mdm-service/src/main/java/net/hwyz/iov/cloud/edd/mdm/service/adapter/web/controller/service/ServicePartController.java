package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.MdmPartService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PartAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PartDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PartAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 零件服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/part/v1")
@RequiredArgsConstructor
public class ServicePartController implements MdmPartService {

    private final PartAppService partAppService;
    private final PartAssembler partAssembler;

    @Override
    @GetMapping("/snapshot")
    public PartPageResponse snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<PartDto> parts = partAppService.snapshot(includeInactiveFlag, page, size);

        long total = partAppService.snapshotCount(includeInactiveFlag);

        List<PartResponse> rows = parts.stream()
                .map(partAssembler::toResponse)
                .collect(Collectors.toList());

        return PartPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public PartResponse getByCode(@PathVariable("code") String code) {
        PartDto dto = partAppService.getPartByCode(code);
        return partAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listByCategory")
    public List<PartBriefResponse> listByCategory(@RequestParam("categoryCode") String categoryCode) {
        List<PartDto> dtoList = partAppService.listByCategory(categoryCode);
        return dtoList.stream()
                .map(partAssembler::toBriefResponse)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/listByVehicleNode")
    public List<PartBriefResponse> listByVehicleNode(@RequestParam("vehicleNodeCode") String vehicleNodeCode) {
        List<PartDto> dtoList = partAppService.listByVehicleNode(vehicleNodeCode);
        return dtoList.stream()
                .map(partAssembler::toBriefResponse)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/listBySupplier")
    public List<PartBriefResponse> listBySupplier(@RequestParam("supplierCode") String supplierCode) {
        List<PartDto> dtoList = partAppService.listBySupplier(supplierCode);
        return dtoList.stream()
                .map(partAssembler::toBriefResponse)
                .collect(Collectors.toList());
    }
}
