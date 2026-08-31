package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.OptionFamilyCatalogBootstrapResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.service.OptionFamilyCatalogBootstrap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 选项族标准目录 Bootstrap 启动执行器（CR-035 §5.3）
 * <p>
 * 受配置开关 iov.mdm.option-family-catalog.bootstrap-enabled 控制（默认关闭）。
 * 开启时在应用启动后执行一次性幂等导入；成功执行后应将该开关置为 false（部署变更显式开启一次）。
 *
 * @author hwyz_leo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OptionFamilyCatalogBootstrapRunner implements ApplicationRunner {

    private final OptionFamilyCatalogBootstrap optionFamilyCatalogBootstrap;

    @Value("${iov.mdm.option-family-catalog.bootstrap-enabled:false}")
    private boolean bootstrapEnabled;

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapEnabled) {
            log.info("Option Family 标准目录 Bootstrap 未启用（iov.mdm.option-family-catalog.bootstrap-enabled=false），跳过启动导入");
            return;
        }
        OptionFamilyCatalogBootstrapResult result = optionFamilyCatalogBootstrap.bootstrap();
        log.info("Option Family 标准目录 Bootstrap 启动导入完成: {}", result);
        log.info("成功执行后请将配置 iov.mdm.option-family-catalog.bootstrap-enabled 置为 false；重复执行保持幂等");
    }
}
