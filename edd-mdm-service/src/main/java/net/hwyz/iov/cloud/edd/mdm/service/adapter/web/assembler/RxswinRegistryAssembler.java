package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler;

import net.hwyz.iov.cloud.edd.mdm.api.vo.response.RxswinRegistryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.RxswinRegistryDto;
import org.springframework.stereotype.Component;

/**
 * RXSWIN登记Assembler
 *
 * @author hwyz_leo
 */
@Component
public class RxswinRegistryAssembler {

    /**
     * DTO转Response VO
     *
     * @param dto RXSWIN登记DTO
     * @return RXSWIN登记响应VO
     */
    public RxswinRegistryResponse toResponse(RxswinRegistryDto dto) {
        if (dto == null) {
            return null;
        }
        return RxswinRegistryResponse.builder()
                .manifestCode(dto.getManifestCode())
                .swinCode(dto.getSwinCode())
                .rxswinValue(dto.getRxswinValue())
                .softwareBaselineCode(dto.getSoftwareBaselineCode())
                .status(dto.getStatus())
                .idempotentHit(dto.getIdempotentHit())
                .registeredAt(dto.getRegisteredAt())
                .build();
    }
}
