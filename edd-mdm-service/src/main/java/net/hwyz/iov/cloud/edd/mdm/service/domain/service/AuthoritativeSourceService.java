package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.enums.ConflictPolicy;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.NonAuthoritativeSourceException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.AuthoritativeSourceConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthoritativeSourceService {

    private final AuthoritativeSourceConfigRepository configRepository;

    public void validateAuthoritativeSource(EntityType entityType, String code, String sourceSystem) {
        Map<String, String> config = configRepository.findConfig(entityType, code);
        String authoritativeSource = config.get("authoritativeSource");
        String conflictPolicy = config.get("conflictPolicy");

        if (!authoritativeSource.equals(sourceSystem)) {
            if (ConflictPolicy.REJECT.name().equals(conflictPolicy)) {
                throw new NonAuthoritativeSourceException(
                        String.format("非权威源写入被拒绝: entityType=%s, code=%s, source=%s, authoritative=%s",
                                entityType, code, sourceSystem, authoritativeSource));
            }
            throw new NonAuthoritativeSourceException(
                    String.format("非权威源写入（仅审计）: entityType=%s, code=%s, source=%s, authoritative=%s",
                            entityType, code, sourceSystem, authoritativeSource));
        }
    }

    public Map<String, String> getConfig(EntityType entityType, String code) {
        return configRepository.findConfig(entityType, code);
    }
}
