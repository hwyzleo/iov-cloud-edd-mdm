package net.hwyz.iov.cloud.edd.mdm.service.adapter.web.controller.mpt;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodePageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.VehicleNodeResponse;
import net.hwyz.iov.cloud.edd.mdm.service.adapter.web.assembler.VehicleNodeAssembler;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeDeleteCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.VehicleNodeUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.VehicleNodeQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.VehicleNodeDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.VehicleNodeAppService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.VehicleNodeHistory;
import net.hwyz.iov.cloud.framework.common.bean.ApiResponse;
import org.springframework.data.domain.Page;
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
 * 车载节点管理后台Controller
 *
 * @author hwyz_leo
 */
@RestController
@RequestMapping("/api/mpt/vehicleNode/v1")
@RequiredArgsConstructor
public class MptVehicleNodeController {

    private final VehicleNodeAppService vehicleNodeAppService;
    private final VehicleNodeAssembler vehicleNodeAssembler;

    /**
     * 创建车载节点
     *
     * @param cmd 创建命令
     * @return 车载节点响应
     */
    @PostMapping("/create")
    public ApiResponse<VehicleNodeResponse> create(@RequestBody VehicleNodeCreateCmd cmd) {
        VehicleNodeDto dto = vehicleNodeAppService.create(cmd);
        return ApiResponse.ok(vehicleNodeAssembler.toResponse(dto));
    }

    /**
     * 更新车载节点
     *
     * @param nodeCode 车载节点nodeCode
     * @param cmd      更新命令
     * @return 车载节点响应
     */
    @PutMapping("/{nodeCode}")
    public ApiResponse<VehicleNodeResponse> update(@PathVariable String nodeCode, @RequestBody VehicleNodeUpdateCmd cmd) {
        VehicleNodeDto dto = vehicleNodeAppService.update(nodeCode, cmd);
        return ApiResponse.ok(vehicleNodeAssembler.toResponse(dto));
    }

    /**
     * 删除车载节点
     *
     * @param nodeCode 车载节点nodeCode
     * @param operator 操作人
     */
    @DeleteMapping("/{nodeCode}")
    public ApiResponse<Void> delete(@PathVariable String nodeCode, @RequestParam String operator) {
        vehicleNodeAppService.delete(VehicleNodeDeleteCmd.builder()
                .nodeCode(nodeCode)
                .operator(operator)
                .forceDelete(false)
                .build());
        return ApiResponse.ok();
    }

    /**
     * 强制删除车载节点
     *
     * @param nodeCode 车载节点nodeCode
     * @param operator 操作人
     */
    @DeleteMapping("/{nodeCode}/force")
    public ApiResponse<Void> forceDelete(@PathVariable String nodeCode, @RequestParam String operator) {
        vehicleNodeAppService.delete(VehicleNodeDeleteCmd.builder()
                .nodeCode(nodeCode)
                .operator(operator)
                .forceDelete(true)
                .build());
        return ApiResponse.ok();
    }

    /**
     * 失效车载节点
     *
     * @param nodeCode 车载节点nodeCode
     * @param modifyBy 修改人
     * @return 车载节点响应
     */
    @PostMapping("/{nodeCode}/deactivate")
    public ApiResponse<VehicleNodeResponse> deactivate(@PathVariable String nodeCode, @RequestParam String modifyBy) {
        VehicleNodeDto dto = vehicleNodeAppService.deactivate(nodeCode, modifyBy);
        return ApiResponse.ok(vehicleNodeAssembler.toResponse(dto));
    }

    /**
     * 查询车载节点详情
     *
     * @param nodeCode 车载节点nodeCode
     * @return 车载节点响应
     */
    @GetMapping("/{nodeCode}")
    public ApiResponse<VehicleNodeResponse> getByCode(@PathVariable String nodeCode) {
        VehicleNodeDto dto = vehicleNodeAppService.queryByCode(nodeCode);
        return ApiResponse.ok(vehicleNodeAssembler.toResponse(dto));
    }

    /**
     * 分页查询车载节点列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 车载节点分页响应
     */
    @GetMapping("/list")
    public ApiResponse<VehicleNodePageResponse> list(@RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "10") Integer size,
                                                    @RequestParam(required = false) String nodeType,
                                                    @RequestParam(required = false) String functionalDomain,
                                                    @RequestParam(required = false) String otaSupportType,
                                                    @RequestParam(required = false) Boolean isCoreNode,
                                                    @RequestParam(required = false) String status) {
        Page<VehicleNodeDto> result = vehicleNodeAppService.list(VehicleNodeQuery.builder()
                .page(page)
                .size(size)
                .nodeType(nodeType)
                .functionalDomain(functionalDomain)
                .otaSupportType(otaSupportType)
                .isCoreNode(isCoreNode)
                .status(status)
                .build());

        List<VehicleNodeResponse> rows = result.getContent().stream()
                .map(vehicleNodeAssembler::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.ok(VehicleNodePageResponse.builder()
                .total(result.getTotalElements())
                .rows(rows)
                .build());
    }

    /**
     * 查询所有ACTIVE车载节点
     *
     * @return 车载节点响应列表
     */
    @GetMapping("/listAll")
    public ApiResponse<List<VehicleNodeResponse>> listAll() {
        List<VehicleNodeDto> dtoList = vehicleNodeAppService.listAllActive();
        List<VehicleNodeResponse> rows = dtoList.stream()
                .map(vehicleNodeAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 导出车载节点
     *
     * @return 车载节点响应列表
     */
    @GetMapping("/export")
    public ApiResponse<List<VehicleNodeResponse>> export() {
        List<VehicleNodeDto> dtoList = vehicleNodeAppService.listAllActive();
        List<VehicleNodeResponse> rows = dtoList.stream()
                .map(vehicleNodeAssembler::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.ok(rows);
    }

    /**
     * 查询车载节点历史版本列表
     *
     * @param nodeCode 车载节点nodeCode
     * @return 历史版本列表
     */
    @GetMapping("/{nodeCode}/history")
    public ApiResponse<List<VehicleNodeHistory>> history(@PathVariable String nodeCode) {
        List<VehicleNodeHistory> historyList = vehicleNodeAppService.queryHistory(nodeCode);
        return ApiResponse.ok(historyList);
    }
}
