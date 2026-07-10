package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.RxswinRegistryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.RxswinRegisterRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.RxswinRegistryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.RxswinRegistryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.RxswinRegisterCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.RxswinRegistryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.RxswinRegistryAppService;
import org.springframework.web.bind.annotation.*;

/**
 * RXSWIN登记服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/mdm/eead/v1/rxswin")
@RequiredArgsConstructor
public class ServiceRxswinRegistryController implements RxswinRegistryService {

    private final RxswinRegistryAppService rxswinRegistryAppService;
    private final RxswinRegistryAssembler rxswinRegistryAssembler;

    @Override
    @PostMapping("/register")
    public RxswinRegistryResponse register(@RequestBody RxswinRegisterRequest request) {
        RxswinRegisterCmd cmd = RxswinRegisterCmd.builder()
                .manifestCode(request.getManifestCode())
                .swinCode(request.getSwinCode())
                .manifestDigest(request.getManifestDigest())
                .softwareBaselineCode(request.getSoftwareBaselineCode())
                .approvedAt(request.getApprovedAt())
                .requestSource("OTA")
                .build();
        RxswinRegistryDto dto = rxswinRegistryAppService.register(cmd);
        return rxswinRegistryAssembler.toResponse(dto);
    }

    @Override
    @GetMapping("/byManifestCode/{manifestCode}")
    public RxswinRegistryResponse getByManifestCode(@PathVariable("manifestCode") String manifestCode) {
        RxswinRegistryDto dto = rxswinRegistryAppService.getByManifestCode(manifestCode);
        return rxswinRegistryAssembler.toResponse(dto);
    }
}
