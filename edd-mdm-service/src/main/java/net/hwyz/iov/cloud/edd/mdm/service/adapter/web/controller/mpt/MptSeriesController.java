package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SeriesAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SeriesCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SeriesUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SeriesQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SeriesDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SeriesAppService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesResponse;
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
@RequestMapping("/api/mpt/mdm/series/v1")
@RequiredArgsConstructor
public class MptSeriesController {

    private final SeriesAppService seriesAppService;
    private final SeriesAssembler seriesAssembler;

    /**
     * 创建车系
     *
     * @param cmd 创建命令
     * @return 车系响应
     */
    @PostMapping("/create")
    public SeriesResponse create(@RequestBody SeriesCreateCmd cmd) {
        SeriesDto series = seriesAppService.createSeries(cmd);
        return seriesAssembler.toResponse(series);
    }

    /**
     * 更新车系
     *
     * @param code 车系code
     * @param cmd  更新命令
     * @return 车系响应
     */
    @PutMapping("/{code}")
    public SeriesResponse update(@PathVariable String code, @RequestBody SeriesUpdateCmd cmd) {
        cmd.setCode(code);
        SeriesDto series = seriesAppService.updateSeries(cmd);
        return seriesAssembler.toResponse(series);
    }

    /**
     * 删除车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     */
    @DeleteMapping("/{code}")
    public void delete(@PathVariable String code, @RequestParam String modifyBy) {
        seriesAppService.deleteSeries(code, modifyBy);
    }

    /**
     * 失效车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     * @return 车系响应
     */
    @PostMapping("/{code}/deactivate")
    public SeriesResponse deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        SeriesDto series = seriesAppService.deactivateSeries(code, modifyBy);
        return seriesAssembler.toResponse(series);
    }

    /**
     * 查询车系详情
     *
     * @param code 车系code
     * @return 车系响应
     */
    @GetMapping("/{code}")
    public SeriesResponse getByCode(@PathVariable String code) {
        SeriesDto series = seriesAppService.getSeriesByCode(code);
        return seriesAssembler.toResponse(series);
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
    public SeriesPageResponse list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String brandCode,
                                   @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<SeriesDto> seriesList = seriesAppService.listSeries(
                SeriesQuery.builder()
                        .page(page)
                        .size(size)
                        .brandCode(brandCode)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = seriesAppService.countSeries(brandCode, includeInactiveFlag);

        List<SeriesResponse> rows = seriesList.stream()
                .map(seriesAssembler::toResponse)
                .collect(Collectors.toList());

        return SeriesPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }
}
