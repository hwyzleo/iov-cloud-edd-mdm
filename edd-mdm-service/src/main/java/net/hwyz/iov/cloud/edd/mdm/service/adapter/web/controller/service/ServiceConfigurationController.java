package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.ConfigurationService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.ConfigurationByVariantAndOptionCodesRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.ConfigurationAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.OptionCodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.ConfigurationQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.ConfigurationDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionCodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.ConfigurationAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/service/configuration/v1")
@RequiredArgsConstructor
public class ServiceConfigurationController implements ConfigurationService {

    private final ConfigurationAppService configurationAppService;
    private final ConfigurationAssembler configurationAssembler;
    private final OptionCodeAssembler optionCodeAssembler;

    @Override
    @GetMapping("/listAll")
    public ConfigurationPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "100") Integer size,
                                            @RequestParam(required = false) String variantCode,
                                            @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);
        List<ConfigurationDto> list = configurationAppService.listConfigurations(
                ConfigurationQuery.builder().page(page).size(size)
                        .variantCode(variantCode).includeInactive(includeInactiveFlag).build());
        long total = configurationAppService.countConfigurations(variantCode, includeInactiveFlag);
        List<ConfigurationResponse> rows = list.stream()
                .map(configurationAssembler::toResponse).collect(Collectors.toList());
        return ConfigurationPageResponse.builder().total(total).rows(rows).build();
    }

    @Override
    @GetMapping("/{code}")
    public ConfigurationResponse getByCode(@PathVariable String code) {
        ConfigurationDto configuration = configurationAppService.getConfigurationByCode(code);
        return configurationAssembler.toResponse(configuration);
    }

    @Override
    @GetMapping("/{code}/optionCodes")
    public List<OptionCodeResponse> getOptionCodes(@PathVariable String code) {
        List<OptionCodeDto> list = configurationAppService.listOptionCodeDetails(code);
        return list.stream().map(optionCodeAssembler::toResponse).collect(Collectors.toList());
    }

    @Override
    @PostMapping("/findByOptionCodes")
    public List<ConfigurationResponse> findByOptionCodes(@RequestBody List<String> optionCodes) {
        List<ConfigurationDto> list = configurationAppService.findByOptionCodes(optionCodes);
        return list.stream().map(configurationAssembler::toResponse).collect(Collectors.toList());
    }

    @Override
    @PostMapping("/resolveConfiguration")
    public String resolveConfiguration(@RequestBody ConfigurationByVariantAndOptionCodesRequest request) {
        return configurationAppService.resolveConfigurationCode(request.getVariantCode(), request.getOptionCodes());
    }
}
