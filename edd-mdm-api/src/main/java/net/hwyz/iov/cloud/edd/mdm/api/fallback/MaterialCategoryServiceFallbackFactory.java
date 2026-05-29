package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.MaterialCategoryService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.MaterialCategoryResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 物料分类相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class MaterialCategoryServiceFallbackFactory implements FallbackFactory<MaterialCategoryService> {

    @Override
    public MaterialCategoryService create(Throwable throwable) {
        return new MaterialCategoryService() {
            @Override
            public MaterialCategoryPageResponse snapshot(Boolean includeInactive, Integer page, Integer size) {
                log.error("物料分类服务获取全量快照调用失败", throwable);
                return MaterialCategoryPageResponse.empty();
            }

            @Override
            public MaterialCategoryResponse getByCode(String code) {
                log.error("物料分类服务根据code[{}]获取物料分类信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<MaterialCategoryResponse> tree() {
                log.error("物料分类服务获取树形结构调用失败", throwable);
                return Collections.emptyList();
            }
        };
    }
}
