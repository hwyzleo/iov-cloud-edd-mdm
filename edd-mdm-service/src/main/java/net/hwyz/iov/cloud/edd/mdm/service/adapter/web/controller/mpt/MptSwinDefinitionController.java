package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinDefinitionAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SWIN定义管理控制器（MPT）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/swinDefinition/v1")
@RequiredArgsConstructor
public class MptSwinDefinitionController {

    private final SwinDefinitionAppService swinDefinitionAppService;

    @PostMapping("/create")
    public ResponseEntity<SwinDefinition> createSwinDefinition(@RequestBody CreateSwinDefinitionRequest request) {
        SwinDefinition swinDefinition = swinDefinitionAppService.createSwinDefinition(
                request.getSwinCode(), request.getSchemeCode(), request.getTypeRefType(), request.getTypeRefCode(),
                request.getName(), request.getNameLocal(), request.getDescription(), request.getCreateBy());
        return ResponseEntity.ok(swinDefinition);
    }

    @PutMapping("/{swinCode}")
    public ResponseEntity<SwinDefinition> updateSwinDefinition(@PathVariable String swinCode, @RequestBody UpdateSwinDefinitionRequest request) {
        SwinDefinition swinDefinition = swinDefinitionAppService.updateSwinDefinition(
                swinCode, request.getName(), request.getNameLocal(), request.getDescription(), request.getModifyBy());
        return ResponseEntity.ok(swinDefinition);
    }

    @DeleteMapping("/{swinCode}")
    public ResponseEntity<Void> deleteSwinDefinition(@PathVariable String swinCode, @RequestParam String operator) {
        swinDefinitionAppService.deleteSwinDefinition(swinCode, operator);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{swinCode}/deactivate")
    public ResponseEntity<SwinDefinition> deactivateSwinDefinition(@PathVariable String swinCode, @RequestParam String modifyBy) {
        SwinDefinition swinDefinition = swinDefinitionAppService.deactivateSwinDefinition(swinCode, modifyBy);
        return ResponseEntity.ok(swinDefinition);
    }

    @GetMapping("/{swinCode}")
    public ResponseEntity<SwinDefinition> getSwinDefinition(@PathVariable String swinCode) {
        SwinDefinition swinDefinition = swinDefinitionAppService.getSwinDefinitionBySwinCode(swinCode);
        return ResponseEntity.ok(swinDefinition);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SwinDefinition>> listSwinDefinitions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<SwinDefinition> swinDefinitions = swinDefinitionAppService.getSwinDefinitions(page, size, includeInactive);
        return ResponseEntity.ok(swinDefinitions);
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<SwinDefinition>> listAllActiveSwinDefinitions() {
        List<SwinDefinition> swinDefinitions = swinDefinitionAppService.getAllActiveSwinDefinitions();
        return ResponseEntity.ok(swinDefinitions);
    }

    @lombok.Data
    public static class CreateSwinDefinitionRequest {
        private String swinCode;
        private String schemeCode;
        private String typeRefType;
        private String typeRefCode;
        private String name;
        private String nameLocal;
        private String description;
        private String createBy;
    }

    @lombok.Data
    public static class UpdateSwinDefinitionRequest {
        private String name;
        private String nameLocal;
        private String description;
        private String modifyBy;
    }
}
