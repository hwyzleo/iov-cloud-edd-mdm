package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SwinSchemeAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * SWIN编码方案管理控制器（MPT）
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/swinScheme/v1")
@RequiredArgsConstructor
public class MptSwinSchemeController {

    private final SwinSchemeAppService swinSchemeAppService;

    @PostMapping("/create")
    public ResponseEntity<SwinScheme> createSwinScheme(@RequestBody CreateSwinSchemeRequest request) {
        SwinScheme swinScheme = swinSchemeAppService.createSwinScheme(
                request.getCode(), request.getName(), request.getNameLocal(), request.getDescription(),
                request.getRoute(), request.getSortOrder(), request.getEffectiveFrom(), request.getEffectiveTo(),
                request.getCreateBy());
        return ResponseEntity.ok(swinScheme);
    }

    @PutMapping("/{code}")
    public ResponseEntity<SwinScheme> updateSwinScheme(@PathVariable String code, @RequestBody UpdateSwinSchemeRequest request) {
        SwinScheme swinScheme = swinSchemeAppService.updateSwinScheme(
                code, request.getName(), request.getNameLocal(), request.getDescription(),
                request.getRoute(), request.getSortOrder(), request.getEffectiveFrom(), request.getEffectiveTo(),
                request.getModifyBy());
        return ResponseEntity.ok(swinScheme);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteSwinScheme(@PathVariable String code, @RequestParam String operator) {
        swinSchemeAppService.deleteSwinScheme(code, operator);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{code}/deactivate")
    public ResponseEntity<SwinScheme> deactivateSwinScheme(@PathVariable String code, @RequestParam String modifyBy) {
        SwinScheme swinScheme = swinSchemeAppService.deactivateSwinScheme(code, modifyBy);
        return ResponseEntity.ok(swinScheme);
    }

    @GetMapping("/{code}")
    public ResponseEntity<SwinScheme> getSwinScheme(@PathVariable String code) {
        SwinScheme swinScheme = swinSchemeAppService.getSwinSchemeByCode(code);
        return ResponseEntity.ok(swinScheme);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SwinScheme>> listSwinSchemes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<SwinScheme> swinSchemes = swinSchemeAppService.getSwinSchemes(page, size, includeInactive);
        return ResponseEntity.ok(swinSchemes);
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<SwinScheme>> listAllActiveSwinSchemes() {
        List<SwinScheme> swinSchemes = swinSchemeAppService.getAllActiveSwinSchemes();
        return ResponseEntity.ok(swinSchemes);
    }

    @lombok.Data
    public static class CreateSwinSchemeRequest {
        private String code;
        private String name;
        private String nameLocal;
        private String description;
        private String route;
        private Integer sortOrder;
        private Date effectiveFrom;
        private Date effectiveTo;
        private String createBy;
    }

    @lombok.Data
    public static class UpdateSwinSchemeRequest {
        private String name;
        private String nameLocal;
        private String description;
        private String route;
        private Integer sortOrder;
        private Date effectiveFrom;
        private Date effectiveTo;
        private String modifyBy;
    }
}
