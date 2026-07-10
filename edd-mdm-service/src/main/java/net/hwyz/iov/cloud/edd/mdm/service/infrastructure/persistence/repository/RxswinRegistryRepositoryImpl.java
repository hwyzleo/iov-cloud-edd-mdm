package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.RxswinRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinRegistryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.RxswinRegistryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.RxswinRegistryPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RXSWIN登记仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class RxswinRegistryRepositoryImpl implements RxswinRegistryRepository {

    private final RxswinRegistryMapper rxswinRegistryMapper;

    @Override
    public void save(RxswinRegistry rxswinRegistry) {
        RxswinRegistryPo po = toPo(rxswinRegistry);
        if (po.getId() == null) {
            rxswinRegistryMapper.insert(po);
            rxswinRegistry.setId(po.getId());
        } else {
            rxswinRegistryMapper.updateById(po);
        }
    }

    @Override
    public Optional<RxswinRegistry> findByManifestCode(String manifestCode) {
        LambdaQueryWrapper<RxswinRegistryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RxswinRegistryPo::getManifestCode, manifestCode);
        wrapper.eq(RxswinRegistryPo::getRowValid, true);
        return Optional.ofNullable(rxswinRegistryMapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public long countBySwinCode(String swinCode) {
        LambdaQueryWrapper<RxswinRegistryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RxswinRegistryPo::getSwinCode, swinCode);
        wrapper.eq(RxswinRegistryPo::getRowValid, true);
        return rxswinRegistryMapper.selectCount(wrapper);
    }

    @Override
    public List<RxswinRegistry> findPaginated(int page, int size, String manifestCode, String rxswinValue,
                                                String swinCode, String softwareBaselineCode,
                                                Date registeredAtStart, Date registeredAtEnd) {
        LambdaQueryWrapper<RxswinRegistryPo> wrapper = buildQueryWrapper(manifestCode, rxswinValue, swinCode,
                softwareBaselineCode, registeredAtStart, registeredAtEnd);
        wrapper.orderByDesc(RxswinRegistryPo::getRegisteredAt);
        return rxswinRegistryMapper.selectPage(new Page<>(page, size), wrapper).getRecords().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(String manifestCode, String rxswinValue, String swinCode,
                      String softwareBaselineCode, Date registeredAtStart, Date registeredAtEnd) {
        LambdaQueryWrapper<RxswinRegistryPo> wrapper = buildQueryWrapper(manifestCode, rxswinValue, swinCode,
                softwareBaselineCode, registeredAtStart, registeredAtEnd);
        return rxswinRegistryMapper.selectCount(wrapper);
    }

    private LambdaQueryWrapper<RxswinRegistryPo> buildQueryWrapper(String manifestCode, String rxswinValue,
                                                                     String swinCode, String softwareBaselineCode,
                                                                     Date registeredAtStart, Date registeredAtEnd) {
        LambdaQueryWrapper<RxswinRegistryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RxswinRegistryPo::getRowValid, true);
        if (manifestCode != null && !manifestCode.isBlank()) {
            wrapper.like(RxswinRegistryPo::getManifestCode, manifestCode);
        }
        if (rxswinValue != null && !rxswinValue.isBlank()) {
            wrapper.like(RxswinRegistryPo::getRxswinValue, rxswinValue);
        }
        if (swinCode != null && !swinCode.isBlank()) {
            wrapper.eq(RxswinRegistryPo::getSwinCode, swinCode);
        }
        if (softwareBaselineCode != null && !softwareBaselineCode.isBlank()) {
            wrapper.eq(RxswinRegistryPo::getSoftwareBaselineCode, softwareBaselineCode);
        }
        if (registeredAtStart != null) {
            wrapper.ge(RxswinRegistryPo::getRegisteredAt, registeredAtStart);
        }
        if (registeredAtEnd != null) {
            wrapper.le(RxswinRegistryPo::getRegisteredAt, registeredAtEnd);
        }
        return wrapper;
    }

    private RxswinRegistryPo toPo(RxswinRegistry domain) {
        return RxswinRegistryPo.builder()
                .id(domain.getId())
                .manifestCode(domain.getManifestCode())
                .manifestDigest(domain.getManifestDigest())
                .swinCode(domain.getSwinCode())
                .rxswinValue(domain.getRxswinValue())
                .softwareBaselineCode(domain.getSoftwareBaselineCode())
                .status(domain.getStatus())
                .approvedAt(domain.getApprovedAt())
                .registeredAt(domain.getRegisteredAt())
                .requestSource(domain.getRequestSource())
                .traceId(domain.getTraceId())
                .version(domain.getVersion())
                .createBy(domain.getCreateBy())
                .createTime(domain.getCreateTime())
                .rowVersion(domain.getRowVersion())
                .rowValid(domain.getRowValid())
                .build();
    }

    private RxswinRegistry toDomain(RxswinRegistryPo po) {
        return RxswinRegistry.builder()
                .id(po.getId())
                .manifestCode(po.getManifestCode())
                .manifestDigest(po.getManifestDigest())
                .swinCode(po.getSwinCode())
                .rxswinValue(po.getRxswinValue())
                .softwareBaselineCode(po.getSoftwareBaselineCode())
                .status(po.getStatus())
                .approvedAt(po.getApprovedAt())
                .registeredAt(po.getRegisteredAt())
                .requestSource(po.getRequestSource())
                .traceId(po.getTraceId())
                .version(po.getVersion())
                .createBy(po.getCreateBy())
                .createTime(po.getCreateTime())
                .rowVersion(po.getRowVersion())
                .rowValid(po.getRowValid())
                .build();
    }
}
