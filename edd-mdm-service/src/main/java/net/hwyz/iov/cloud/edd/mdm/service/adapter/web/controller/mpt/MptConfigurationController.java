package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.ConfigurationAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationBindOptionCodeCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.ConfigurationUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ConfigurationQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.ConfigurationAppService;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.ConfigurationOptionCodeBindingPo;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/configuration/v1")
@RequiredArgsConstructor
public class MptConfigurationController {

    private final ConfigurationAppService configurationAppService;
    private final ConfigurationAssembler configurationAssembler;

    @PostMapping("/create")
    public ApiResponse<ConfigurationResponse> create(@RequestBody ConfigurationCreateCmd cmd) {
        ConfigurationDto configuration = configurationAppService.createConfiguration(cmd);
        return ApiResponse.ok(configurationAssembler.toResponse(configuration));
    }

    @PutMapping("/{code}")
    public ApiResponse<ConfigurationResponse> update(@PathVariable String code, @RequestBody ConfigurationUpdateCmd cmd) {
        // CR-005：code 不可变，仅由 path 参数定位记录；ConfigurationUpdateCmd 已不含 code 字段
        ConfigurationDto configuration = configurationAppService.updateConfiguration(code, cmd);
        return ApiResponse.ok(configurationAssembler.toResponse(configuration));
    }

    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        configurationAppService.deleteConfiguration(code, modifyBy);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/deactivate")
    public ApiResponse<ConfigurationResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        ConfigurationDto configuration = configurationAppService.deactivateConfiguration(code, modifyBy);
        return ApiResponse.ok(configurationAssembler.toResponse(configuration));
    }

    @GetMapping("/{code}")
    public ApiResponse<ConfigurationResponse> getByCode(@PathVariable String code) {
        ConfigurationDto configuration = configurationAppService.getConfigurationByCode(code);
        return ApiResponse.ok(configurationAssembler.toResponse(configuration));
    }

    @GetMapping("/list")
    public ApiResponse<ConfigurationPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestParam(required = false) String variantCode,
                                                      @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<ConfigurationDto> list = configurationAppService.listConfigurations(
                ConfigurationQuery.builder().page(page).size(size)
                        .variantCode(variantCode).includeInactive(includeInactiveFlag).build());
        long total = configurationAppService.countConfigurations(variantCode, includeInactiveFlag);
        List<ConfigurationResponse> rows = list.stream()
                .map(configurationAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(ConfigurationPageResponse.builder().total(total).rows(rows).build());
    }

    @GetMapping("/{code}/history")
    public ApiResponse<ConfigurationHistoryPageResponse> getHistory(@PathVariable String code) {
        List<ConfigurationHistoryDto> historyList = configurationAppService.listConfigurationHistory(code);
        List<ConfigurationHistoryResponse> rows = historyList.stream()
                .map(configurationAssembler::toHistoryResponse).collect(Collectors.toList());
        return ApiResponse.ok(ConfigurationHistoryPageResponse.builder().total((long) rows.size()).rows(rows).build());
    }

    @PostMapping("/{code}/bind")
    public ApiResponse<Void> bindOptionCode(@PathVariable String code, @RequestBody ConfigurationBindOptionCodeCmd cmd) {
        cmd.setConfigurationCode(code);
        configurationAppService.bindOptionCode(cmd);
        return ApiResponse.ok();
    }

    @PostMapping("/{code}/unbind")
    public ApiResponse<Void> unbindOptionCode(@PathVariable String code,
                                             @RequestParam String optionCodeCode,
                                             @RequestParam String operator) {
        configurationAppService.unbindOptionCode(code, optionCodeCode, operator);
        return ApiResponse.ok();
    }

    @GetMapping("/{code}/optionCodes")
    public ApiResponse<List<ConfigurationOptionCodeBindingPo>> listOptionCodes(@PathVariable String code) {
        return ApiResponse.ok(configurationAppService.listOptionCodes(code));
    }
}
