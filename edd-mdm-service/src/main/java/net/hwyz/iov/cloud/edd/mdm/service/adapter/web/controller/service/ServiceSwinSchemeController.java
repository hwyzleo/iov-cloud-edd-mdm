package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinSchemeAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SWIN编码方案服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/swinScheme/v1")
@RequiredArgsConstructor
public class ServiceSwinSchemeController {

    private final SwinSchemeAppService swinSchemeAppService;

    @GetMapping("/snapshot")
    public List<SwinScheme> snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        return swinSchemeAppService.getSwinSchemes(page, size, includeInactiveFlag);
    }

    @GetMapping("/{code}")
    public SwinScheme getByCode(@PathVariable String code) {
        return swinSchemeAppService.getSwinSchemeByCode(code);
    }

    @GetMapping("/listAll")
    public List<SwinScheme> listAll() {
        return swinSchemeAppService.getAllActiveSwinSchemes();
    }
}
