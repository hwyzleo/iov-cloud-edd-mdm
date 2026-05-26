package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.AuthoritativeSourceConfigRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.AuthoritativeSourceConfigMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.AuthoritativeSourceConfigPo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthoritativeSourceConfigRepositoryImpl implements AuthoritativeSourceConfigRepository {

    private final AuthoritativeSourceConfigMapper configMapper;

    @Override
    public Map<String, String> findConfig(EntityType entityType, String code) {
        LambdaQueryWrapper<AuthoritativeSourceConfigPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthoritativeSourceConfigPo::getEntityType, entityType.name());
        wrapper.eq(AuthoritativeSourceConfigPo::getCodePattern, code);
        wrapper.eq(AuthoritativeSourceConfigPo::getEnabled, 1);
        wrapper.eq(AuthoritativeSourceConfigPo::getRowValid, true);
        wrapper.orderByAsc(AuthoritativeSourceConfigPo::getPriority);
        wrapper.last("LIMIT 1");
        AuthoritativeSourceConfigPo po = configMapper.selectOne(wrapper);

        if (po != null) {
            Map<String, String> result = new HashMap<>();
            result.put("authoritativeSource", po.getAuthoritativeSource());
            result.put("conflictPolicy", po.getConflictPolicy());
            return result;
        }
        return findDefaultConfig(entityType);
    }

    @Override
    public Map<String, String> findDefaultConfig(EntityType entityType) {
        LambdaQueryWrapper<AuthoritativeSourceConfigPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AuthoritativeSourceConfigPo::getEntityType, entityType.name());
        wrapper.eq(AuthoritativeSourceConfigPo::getCodePattern, "*");
        wrapper.eq(AuthoritativeSourceConfigPo::getEnabled, 1);
        wrapper.eq(AuthoritativeSourceConfigPo::getRowValid, true);
        wrapper.orderByAsc(AuthoritativeSourceConfigPo::getPriority);
        wrapper.last("LIMIT 1");
        AuthoritativeSourceConfigPo po = configMapper.selectOne(wrapper);

        Map<String, String> result = new HashMap<>();
        if (po != null) {
            result.put("authoritativeSource", po.getAuthoritativeSource());
            result.put("conflictPolicy", po.getConflictPolicy());
        } else {
            result.put("authoritativeSource", "LOCAL");
            result.put("conflictPolicy", "REJECT");
        }
        return result;
    }
}
