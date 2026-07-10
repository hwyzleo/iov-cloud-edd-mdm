package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.SwinDefinitionService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.SwinDefinitionResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * SWIN定义服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class SwinDefinitionServiceFallbackFactory implements FallbackFactory<SwinDefinitionService> {

    @Override
    public SwinDefinitionService create(Throwable throwable) {
        return new SwinDefinitionService() {
            @Override
            public SwinDefinitionPageResponse getSnapshot(boolean includeInactive, int page, int size) {
                log.error("SWIN定义服务获取全量快照调用失败", throwable);
                return SwinDefinitionPageResponse.empty();
            }

            @Override
            public SwinDefinitionResponse getSwinDefinitionBySwinCode(String swinCode) {
                log.error("SWIN定义服务根据swinCode[{}]获取SWIN定义信息调用失败", swinCode, throwable);
                return null;
            }

            @Override
            public List<SwinDefinitionResponse> listAllActiveSwinDefinitions() {
                log.error("SWIN定义服务获取全量列表调用失败", throwable);
                return Collections.emptyList();
            }

            @Override
            public List<SwinDefinitionResponse> getSwinDefinitionsBySchemeCode(String schemeCode) {
                log.error("SWIN定义服务根据schemeCode[{}]获取SWIN定义列表调用失败", schemeCode, throwable);
                return Collections.emptyList();
            }

            @Override
            public List<SwinDefinitionResponse> getSwinDefinitionsByTypeRef(String typeRefType, String typeRefCode) {
                log.error("SWIN定义服务根据typeRefType[{}]和typeRefCode[{}]获取SWIN定义列表调用失败", typeRefType, typeRefCode, throwable);
                return Collections.emptyList();
            }

            @Override
            public List<SwinDefinitionResponse> listByNode(String vehicleNodeCode) {
                log.error("SWIN定义服务根据vehicleNodeCode[{}]获取SWIN定义列表调用失败", vehicleNodeCode, throwable);
                return Collections.emptyList();
            }
        };
    }
}
