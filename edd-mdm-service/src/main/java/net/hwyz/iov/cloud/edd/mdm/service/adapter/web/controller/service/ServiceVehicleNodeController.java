package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.VehicleNodeService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.VehicleNodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VehicleNodeListByOtaTypeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.VehicleNodeAppService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车载节点服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/mdm/eead/v1/vehicleNode")
@RequiredArgsConstructor
public class ServiceVehicleNodeController implements VehicleNodeService {

    private final VehicleNodeAppService vehicleNodeAppService;
    private final VehicleNodeAssembler vehicleNodeAssembler;

    @Override
    @GetMapping("/snapshot")
    public VehicleNodePageResponse snapshot(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "100") Integer size,
                                            @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        Page<VehicleNodeDto> result = vehicleNodeAppService.snapshot(includeInactiveFlag, page, size);

        List<VehicleNodeResponse> rows = result.getContent().stream()
                .map(vehicleNodeAssembler::toResponse)
                .collect(Collectors.toList());

        return VehicleNodePageResponse.builder()
                .total(result.getTotalElements())
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{nodeCode}")
    public VehicleNodeResponse getByCode(@PathVariable String nodeCode) {
        VehicleNodeDto dto = vehicleNodeAppService.queryByCode(nodeCode);
        return vehicleNodeAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/listByOtaType")
    public List<VehicleNodeResponse> listByOtaType(@RequestParam String type) {
        List<VehicleNodeDto> dtoList = vehicleNodeAppService.listByOtaType(
                VehicleNodeListByOtaTypeQuery.builder().otaSupportType(type).build()
        );
        return dtoList.stream()
                .map(vehicleNodeAssembler::toResponse)
                .collect(Collectors.toList());
    }
}
