package net.hwyz.iov.cloud.edd.mdm.service;

import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.framework.security.annotation.EnableCustomConfig;
import net.hwyz.iov.cloud.framework.security.annotation.EnableCustomFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主数据管理服务启动类
 *
 * @author hwyz_leo
 */
@Slf4j
@EnableCustomConfig
@EnableDiscoveryClient
@SpringBootApplication
@EnableCustomFeignClients
public class MdmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MdmApplication.class, args);
    }
}
