package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.CarLineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.CarLineCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.CarLineUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.CarLineQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.CarLineAppService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineResponse;
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
 * 车系管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/carLine/v1")
@RequiredArgsConstructor
public class MptCarLineController {

    private final CarLineAppService carLineAppService;
    private final CarLineAssembler carLineAssembler;

    /**
     * 创建车系
     *
     * @param cmd 创建命令
     * @return 车系响应
     */
    @PostMapping("/create")
    public ApiResponse<CarLineResponse> create(@RequestBody CarLineCreateCmd cmd) {
        CarLineDto carLine = carLineAppService.createCarLine(cmd);
        return ApiResponse.ok(carLineAssembler.toResponse(carLine));
    }

    /**
     * 更新车系
     *
     * @param code 车系code
     * @param cmd  更新命令
     * @return 车系响应
     */
    @PutMapping("/{code}")
    public ApiResponse<CarLineResponse> update(@PathVariable String code, @RequestBody CarLineUpdateCmd cmd) {
        cmd.setCode(code);
        CarLineDto carLine = carLineAppService.updateCarLine(cmd);
        return ApiResponse.ok(carLineAssembler.toResponse(carLine));
    }

    /**
     * 删除车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        carLineAppService.deleteCarLine(code, modifyBy);
        return ApiResponse.ok();
    }

    /**
     * 失效车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     * @return 车系响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<CarLineResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        CarLineDto carLine = carLineAppService.deactivateCarLine(code, modifyBy);
        return ApiResponse.ok(carLineAssembler.toResponse(carLine));
    }

    /**
     * 查询车系详情
     *
     * @param code 车系code
     * @return 车系响应
     */
    @GetMapping("/{code}")
    public ApiResponse<CarLineResponse> getByCode(@PathVariable String code) {
        CarLineDto carLine = carLineAppService.getCarLineByCode(code);
        return ApiResponse.ok(carLineAssembler.toResponse(carLine));
    }

    /**
     * 分页查询车系列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 车系分页响应
     */
    @GetMapping("/list")
    public ApiResponse<CarLinePageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String brandCode,
                                                @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<CarLineDto> carLineList = carLineAppService.listCarLine(
                CarLineQuery.builder()
                        .page(page)
                        .size(size)
                        .brandCode(brandCode)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = carLineAppService.countCarLine(brandCode, includeInactiveFlag);

        List<CarLineResponse> rows = carLineList.stream()
                .map(carLineAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(CarLinePageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询车系历史版本列表
     *
     * @param code 车系code
     * @return 车系历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<CarLineHistoryPageResponse> getHistory(@PathVariable String code) {
        List<CarLineHistoryDto> historyList = carLineAppService.listCarLineHistory(code);

        List<CarLineHistoryResponse> rows = historyList.stream()
                .map(carLineAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(CarLineHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
