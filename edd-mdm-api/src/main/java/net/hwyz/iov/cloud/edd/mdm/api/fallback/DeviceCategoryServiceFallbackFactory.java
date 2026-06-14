package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.DeviceCategoryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.DeviceCategoryResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 设备类别相关服务降级处理（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class DeviceCategoryServiceFallbackFactory implements FallbackFactory<DeviceCategoryService> {

    @Override
    public DeviceCategoryService create(Throwable throwable) {
        return new DeviceCategoryService() {
            @Override
            public DeviceCategoryPageResponse snapshot(Boolean includeInactive, Integer page, Integer size) {
                log.error("设备类别服务获取全量快照调用失败", throwable);
                return DeviceCategoryPageResponse.empty();
            }

            @Override
            public DeviceCategoryResponse getByCode(String code) {
                log.error("设备类别服务根据code[{}]获取设备类别信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<DeviceCategoryResponse> listAll() {
                log.error("设备类别服务获取全量列表调用失败", throwable);
                return Collections.emptyList();
            }
        };
    }
}
