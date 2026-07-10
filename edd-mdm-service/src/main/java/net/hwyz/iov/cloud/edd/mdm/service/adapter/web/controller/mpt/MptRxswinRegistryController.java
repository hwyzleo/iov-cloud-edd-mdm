package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.RxswinRegistryResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.RxswinRegistryAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.RxswinRegistryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.RxswinRegistryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.RxswinRegistryAppService;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RXSWIN登记管理控制器（MPT）
 * <p>
 * 仅提供只读分页查询，不提供新增、修改、删除或重新生成接口。
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/rxswin/v1")
@RequiredArgsConstructor
public class MptRxswinRegistryController {

    private final RxswinRegistryAppService rxswinRegistryAppService;
    private final RxswinRegistryAssembler rxswinRegistryAssembler;

    @GetMapping("/list")
    public ApiResponse<List<RxswinRegistryResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String manifestCode,
            @RequestParam(required = false) String rxswinValue,
            @RequestParam(required = false) String swinCode,
            @RequestParam(required = false) String softwareBaselineCode,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date registeredAtStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date registeredAtEnd) {
        RxswinRegistryQuery query = RxswinRegistryQuery.builder()
                .page(page).size(size)
                .manifestCode(manifestCode)
                .rxswinValue(rxswinValue)
                .swinCode(swinCode)
                .softwareBaselineCode(softwareBaselineCode)
                .registeredAtStart(registeredAtStart)
                .registeredAtEnd(registeredAtEnd)
                .build();
        List<RxswinRegistryDto> dtoList = rxswinRegistryAppService.listRegistries(query);
        List<RxswinRegistryResponse> rows = dtoList.stream()
                .map(rxswinRegistryAssembler::toResponse).collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }
}
