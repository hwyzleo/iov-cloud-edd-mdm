package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.PlatformService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PlatformAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PlatformQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PlatformAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/platform/v1")
@RequiredArgsConstructor
public class ServicePlatformController implements PlatformService {

    private final PlatformAppService platformAppService;
    private final PlatformAssembler platformAssembler;

    @Override
    @GetMapping("/listAll")
    public PlatformPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "100") Integer size,
                                        @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        // 查询平台列表
        List<PlatformDto> platforms = platformAppService.listPlatforms(
                PlatformQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        // 查询总数
        long total = platformAppService.countPlatforms(includeInactiveFlag);

        // 转换为响应对象
        List<PlatformResponse> rows = platforms.stream()
                .map(platformAssembler::toResponse)
                .collect(Collectors.toList());

        return PlatformPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public PlatformResponse getByCode(@PathVariable String code) {
        PlatformDto platform = platformAppService.getPlatformByCode(code);
        return platformAssembler.toResponse(platform);
    }
}
