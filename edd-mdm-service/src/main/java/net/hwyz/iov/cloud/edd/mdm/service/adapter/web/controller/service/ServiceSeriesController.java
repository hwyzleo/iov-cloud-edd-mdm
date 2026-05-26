package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.SeriesService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SeriesResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.SeriesAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SeriesQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SeriesDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.SeriesAppService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车系服务端Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/service/series/v1")
@RequiredArgsConstructor
public class ServiceSeriesController implements SeriesService {

    private final SeriesAppService seriesAppService;
    private final SeriesAssembler seriesAssembler;

    @Override
    @GetMapping("/listAll")
    public SeriesPageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "100") Integer size,
                                      @RequestParam(required = false) String brandCode,
                                      @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        // 查询车系列表
        List<SeriesDto> seriesList = seriesAppService.listSeries(
                SeriesQuery.builder()
                        .page(page)
                        .size(size)
                        .brandCode(brandCode)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        // 查询总数
        long total = seriesAppService.countSeries(brandCode, includeInactiveFlag);

        // 转换为响应对象
        List<SeriesResponse> rows = seriesList.stream()
                .map(seriesAssembler::toResponse)
                .collect(Collectors.toList());

        return SeriesPageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public SeriesResponse getByCode(@PathVariable String code) {
        SeriesDto series = seriesAppService.getSeriesByCode(code);
        return seriesAssembler.toResponse(series);
    }
}
