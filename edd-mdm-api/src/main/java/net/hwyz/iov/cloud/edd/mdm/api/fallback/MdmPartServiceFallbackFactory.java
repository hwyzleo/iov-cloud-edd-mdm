package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.MdmPartService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartBriefResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.PartResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 零件相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class MdmPartServiceFallbackFactory implements FallbackFactory<MdmPartService> {

    @Override
    public MdmPartService create(Throwable throwable) {
        return new MdmPartService() {
            @Override
            public PartPageResponse snapshot(Boolean includeInactive, Integer page, Integer size) {
                log.error("零件服务获取全量快照调用失败", throwable);
                return PartPageResponse.empty();
            }

            @Override
            public PartResponse getByCode(String code) {
                log.error("零件服务根据code[{}]获取零件信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<PartBriefResponse> listByCategory(String categoryCode) {
                log.error("零件服务按物料分类[{}]查询零件列表调用失败", categoryCode, throwable);
                return Collections.emptyList();
            }

            @Override
            public List<PartBriefResponse> listByVehicleNode(String vehicleNodeCode) {
                log.error("零件服务按车辆节点[{}]查询零件列表调用失败", vehicleNodeCode, throwable);
                return Collections.emptyList();
            }

            @Override
            public List<PartBriefResponse> listBySupplier(String supplierCode) {
                log.error("零件服务按供应商[{}]查询零件列表调用失败", supplierCode, throwable);
                return Collections.emptyList();
            }
        };
    }
}
