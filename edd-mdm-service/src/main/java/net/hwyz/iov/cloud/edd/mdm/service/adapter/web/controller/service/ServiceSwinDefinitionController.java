package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinDefinitionAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SWIN定义服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/swinDefinition/v1")
@RequiredArgsConstructor
public class ServiceSwinDefinitionController {

    private final SwinDefinitionAppService swinDefinitionAppService;

    @GetMapping("/snapshot")
    public List<SwinDefinition> snapshot(@RequestParam(defaultValue = "false") Boolean includeInactive,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        return swinDefinitionAppService.getSwinDefinitions(page, size, includeInactiveFlag);
    }

    @GetMapping("/{swinCode}")
    public SwinDefinition getBySwinCode(@PathVariable String swinCode) {
        return swinDefinitionAppService.getSwinDefinitionBySwinCode(swinCode);
    }

    @GetMapping("/listAll")
    public List<SwinDefinition> listAll() {
        return swinDefinitionAppService.getAllActiveSwinDefinitions();
    }
}
