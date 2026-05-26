package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.PlatformAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlatformCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlatformUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PlatformQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.PlatformAppService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformHistoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformHistoryResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PlatformResponse;
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
 * 平台管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/platform/v1")
@RequiredArgsConstructor
public class MptPlatformController {

    private final PlatformAppService platformAppService;
    private final PlatformAssembler platformAssembler;

    /**
     * 创建平台
     *
     * @param cmd 创建命令
     * @return 平台响应
     */
    @PostMapping("/create")
    public ApiResponse<PlatformResponse> create(@RequestBody PlatformCreateCmd cmd) {
        PlatformDto platform = platformAppService.createPlatform(cmd);
        return ApiResponse.ok(platformAssembler.toResponse(platform));
    }

    /**
     * 更新平台
     *
     * @param code 平台code
     * @param cmd  更新命令
     * @return 平台响应
     */
    @PutMapping("/{code}")
    public ApiResponse<PlatformResponse> update(@PathVariable String code, @RequestBody PlatformUpdateCmd cmd) {
        cmd.setCode(code);
        PlatformDto platform = platformAppService.updatePlatform(cmd);
        return ApiResponse.ok(platformAssembler.toResponse(platform));
    }

    /**
     * 删除平台
     *
     * @param code     平台code
     * @param modifyBy 修改人
     */
    @DeleteMapping("/{code}")
    public ApiResponse<Void> delete(@PathVariable String code, @RequestParam String modifyBy) {
        platformAppService.deletePlatform(code, modifyBy);
        return ApiResponse.ok();
    }

    /**
     * 失效平台
     *
     * @param code     平台code
     * @param modifyBy 修改人
     * @return 平台响应
     */
    @PostMapping("/{code}/deactivate")
    public ApiResponse<PlatformResponse> deactivate(@PathVariable String code, @RequestParam String modifyBy) {
        PlatformDto platform = platformAppService.deactivatePlatform(code, modifyBy);
        return ApiResponse.ok(platformAssembler.toResponse(platform));
    }

    /**
     * 查询平台详情
     *
     * @param code 平台code
     * @return 平台响应
     */
    @GetMapping("/{code}")
    public ApiResponse<PlatformResponse> getByCode(@PathVariable String code) {
        PlatformDto platform = platformAppService.getPlatformByCode(code);
        return ApiResponse.ok(platformAssembler.toResponse(platform));
    }

    /**
     * 分页查询平台列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param includeInactive 是否包含失效记录
     * @return 平台分页响应
     */
    @GetMapping("/list")
    public ApiResponse<PlatformPageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) Boolean includeInactive) {
        boolean includeInactiveFlag = Boolean.TRUE.equals(includeInactive);

        List<PlatformDto> platforms = platformAppService.listPlatforms(
                PlatformQuery.builder()
                        .page(page)
                        .size(size)
                        .includeInactive(includeInactiveFlag)
                        .build()
        );

        long total = platformAppService.countPlatforms(includeInactiveFlag);

        List<PlatformResponse> rows = platforms.stream()
                .map(platformAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PlatformPageResponse.builder()
                .total(total)
                .rows(rows)
                .build());
    }

    /**
     * 查询平台历史版本列表
     *
     * @param code 平台code
     * @return 平台历史版本分页响应
     */
    @GetMapping("/{code}/history")
    public ApiResponse<PlatformHistoryPageResponse> getHistory(@PathVariable String code) {
        List<PlatformHistoryDto> historyList = platformAppService.listPlatformHistory(code);

        List<PlatformHistoryResponse> rows = historyList.stream()
                .map(platformAssembler::toHistoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(PlatformHistoryPageResponse.builder()
                .total((long) rows.size())
                .rows(rows)
                .build());
    }
}
