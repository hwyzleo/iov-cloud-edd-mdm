package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlantResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PlantAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantDeleteCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlantUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PlantQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlantHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PlantAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工厂管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/plant/v1")
@RequiredArgsConstructor
public class MptPlantController {

    private final PlantAppService plantAppService;
    private final PlantAssembler plantAssembler;

    /**
     * 创建工厂
     *
     * @param cmd 创建命令
     * @return 工厂响应
     */
    @PostMapping("/create")
    public ApiResponse<PlantResponse> create(@RequestBody PlantCreateCmd cmd) {
        PlantDto dto = plantAppService.createPlant(cmd);
        return ApiResponse.ok(plantAssembler.toResponse(dto));
    }

    /**
     * 更新工厂
     *
     * @param code 工厂code
     * @param cmd  更新命令
     * @return 工厂响应
     */
    @PutMapping("/{code}")
    public ApiResponse<PlantResponse> update(@PathVariable String code, @RequestBody PlantUpdateCmd cmd) {
        cmd.setCode(code);
        PlantDto dto = plantAppService.updatePlant(cmd);
        return ApiResponse.ok(plantAssembler.toResponse(dto));
    }

    /**
     * 删除工厂
     *
     * @param code     工厂code
     * @param operator 操作人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String operator) {
        plantAppService.deletePlant(code, operator, false);
        return ApiResponse.ok();
    }

    /**
     * 强制删除工厂
     *
     * @param code     工厂code
     * @param operator 操作人
     */
    @DeleteMapping("/{code}/force")
    public ApiResponse<Void> forceDelete(@PathVariable String code, @RequestParam String operator) {
        plantAppService.deletePlant(code, operator, true);
        return ApiResponse.ok();
    }

    /**
     * 失效工厂
     *
     * @param code     工厂code
     * @param modifyBy 修改人
     * @return 工厂响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<PlantResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        PlantDto dto = plantAppService.deactivatePlant(code, modifyBy);
        return ApiResponse.ok(plantAssembler.toResponse(dto));
    }

    /**
     * 查询工厂详情
     *
     * @param code 工厂code
     * @return 工厂响应
     */
    @GetMapping("/{code}")
    public ApiResponse<PlantResponse> getByCode(@PathVariable String code) {
        PlantDto dto = plantAppService.getPlantByCode(code);
        return ApiResponse.ok(plantAssembler.toResponse(dto));
    }

    /**
     * 分页查询工厂列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param plantType       工厂类型
     * @param country         国家
     * @param includeInactive 是否包含失效记录
     * @return 工厂分页响应
     */
    @GetMapping("/list")
    public ApiResponse<PlantPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String plantType,
                                               @RequestParam(required = false) String country,
                                               @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<PlantDto> plants = plantAppService.listPlants(
                PlantQuery.builder()
                        .page(page)
                        .size(size)
                        .plantType(plantType)
                        .country(country)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = plantAppService.countPlants(plantType, country, includeInactiveFlag);

        List<PlantResponse> rows = plants.stream()
                .map(plantAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PlantPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询所有ACTIVE工厂
     *
     * @return 工厂响应列表
     */
    @GetMapping("/listAll")
    public ApiResponse<List<PlantResponse>> listAll() {
        List<PlantDto> dtoList = plantAppService.listAllActivePlants();
        List<PlantResponse> rows = dtoList.stream()
                .map(plantAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 导出工厂
     *
     * @return 工厂响应列表
     */
    @GetMapping("/export")
    public ApiResponse<List<PlantResponse>> export() {
        List<PlantDto> dtoList = plantAppService.listAllActivePlants();
        List<PlantResponse> rows = dtoList.stream()
                .map(plantAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 查询工厂历史版本列表
     *
     * @param code 工厂code
     * @return 工厂历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<PlantHistoryPageResponse> getHistory(@PathVariable String code) {
        List<PlantHistoryDto> historyList = plantAppService.listPlantHistory(code);

        List<PlantHistoryResponse> rows = historyList.stream()
                .map(plantAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PlantHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
