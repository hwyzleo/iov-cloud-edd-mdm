package net.hwyz.iov.cloud.edd.mdm.api.fallback;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.api.service.ConfigurationService;
import net.hwyz.iov.cloud.edd.mdm.api.vo.request.ConfigurationByVariantAndOptionCodesRequest;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationPageResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.ConfigurationResponse;
import net.hwyz.iov.cloud.edd.mdm.api.vo.response.OptionCodeResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 配置相关服务降级处理
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
public class ConfigurationServiceFallbackFactory implements FallbackFactory<ConfigurationService> {

    @Override
    public ConfigurationService create(Throwable throwable) {
        return new ConfigurationService() {
            @Override
            public ConfigurationPageResponse listAll(Integer page, Integer size, String variantCode, Boolean includeInactive) {
                log.error("配置服务获取配置全量快照调用失败", throwable);
                return ConfigurationPageResponse.empty();
            }

            @Override
            public ConfigurationResponse getByCode(String code) {
                log.error("配置服务根据code[{}]获取配置信息调用失败", code, throwable);
                return null;
            }

            @Override
            public List<OptionCodeResponse> getOptionCodes(String code) {
                log.error("配置服务根据code[{}]获取关联选项码列表调用失败", code, throwable);
                return Collections.emptyList();
            }

            @Override
            public List<ConfigurationResponse> findByOptionCodes(List<String> optionCodes) {
                log.error("配置服务根据选项码组合查询配置调用失败", throwable);
                return Collections.emptyList();
            }

            @Override
            public String resolveConfiguration(ConfigurationByVariantAndOptionCodesRequest request) {
                log.error("配置服务根据版本和选项码组合反查配置code调用失败", throwable);
                return null;
            }
        };
    }
}
