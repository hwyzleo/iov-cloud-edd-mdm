package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.service.CarLineService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLinePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.CarLineResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.CarLineAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.CarLineQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.CarLineAppService;
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
@RequestMapping("/service/carLine/v1")
@RequiredArgsConstructor
public class ServiceCarLineController implements CarLineService {

    private final CarLineAppService carLineAppService;
    private final CarLineAssembler carLineAssembler;

    @Override
    @GetMapping("/listAll")
    public CarLinePageResponse listAll(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "100") Integer size,
                                      @RequestParam(required = false) String brandCode,
                                      @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        // 查询车系列表
        List<CarLineDto> carLineList = carLineAppService.listCarLine(
                CarLineQuery.builder()
                        .page(page)
                        .size(size)
                        .brandCode(brandCode)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        // 查询总数
        long total = carLineAppService.countCarLine(brandCode, includeInactiveFlag);

        // 转换为响应对象
        List<CarLineResponse> rows = carLineList.stream()
                .map(carLineAssembler::toResponse)
                .collect(Collectors.toList());

        return CarLinePageResponse.builder()
                .total(total)
                .rows(rows)
                .build();
    }

    @Override
    @GetMapping("/{code}")
    public CarLineResponse getByCode(@PathVariable String code) {
        CarLineDto carLine = carLineAppService.getCarLineByCode(code);
        return carLineAssembler.toResponse(carLine);
    }
}
