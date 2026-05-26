package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.IngestionAuthService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.IngestionAuthException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionAuthServiceImpl implements IngestionAuthService {

    @Override
    public void authenticate(String sourceSystem, String authHeader) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            throw new IngestionAuthException("来源系统编码不能为空");
        }
        // TODO: 实际鉴权逻辑 - 校验 sourceSystem 是否已注册且启用
        log.debug("上游来源鉴权通过: {}", sourceSystem);
    }
}
