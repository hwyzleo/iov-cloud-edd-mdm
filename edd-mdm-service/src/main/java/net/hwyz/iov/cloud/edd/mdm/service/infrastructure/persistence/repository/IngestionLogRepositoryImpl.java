package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.common.enums.IngestionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.IngestionLog;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.IngestionLogRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.IngestionLogMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.IngestionLogPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IngestionLogRepositoryImpl implements IngestionLogRepository {

    private final IngestionLogMapper ingestionLogMapper;

    @Override
    public IngestionLog save(IngestionLog log) {
        IngestionLogPo po = toPo(log);
        if (po.getId() == null) {
            ingestionLogMapper.insert(po);
        } else {
            ingestionLogMapper.updateById(po);
        }
        return toDomain(po);
    }

    @Override
    public Optional<IngestionLog> findByMessageId(String messageId) {
        LambdaQueryWrapper<IngestionLogPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IngestionLogPo::getMessageId, messageId);
        wrapper.eq(IngestionLogPo::getRowValid, true);
        IngestionLogPo po = ingestionLogMapper.selectOne(wrapper);
        return Optional.ofNullable(toDomain(po));
    }

    @Override
    public List<IngestionLog> findAll(int page, int size, String sourceSystem,
                                       String entityType, String status,
                                       String startTime, String endTime) {
        Page<IngestionLogPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<IngestionLogPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IngestionLogPo::getRowValid, true);
        if (sourceSystem != null && !sourceSystem.isBlank()) {
            wrapper.eq(IngestionLogPo::getSourceSystem, sourceSystem);
        }
        if (entityType != null && !entityType.isBlank()) {
            wrapper.eq(IngestionLogPo::getEntityType, entityType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(IngestionLogPo::getStatus, status);
        }
        wrapper.orderByDesc(IngestionLogPo::getCreateTime);
        Page<IngestionLogPo> result = ingestionLogMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(String sourceSystem, String entityType, String status,
                      String startTime, String endTime) {
        LambdaQueryWrapper<IngestionLogPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(IngestionLogPo::getRowValid, true);
        if (sourceSystem != null && !sourceSystem.isBlank()) {
            wrapper.eq(IngestionLogPo::getSourceSystem, sourceSystem);
        }
        if (entityType != null && !entityType.isBlank()) {
            wrapper.eq(IngestionLogPo::getEntityType, entityType);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(IngestionLogPo::getStatus, status);
        }
        return ingestionLogMapper.selectCount(wrapper);
    }

    private IngestionLogPo toPo(IngestionLog domain) {
        if (domain == null) return null;
        return IngestionLogPo.builder()
                .id(domain.getId()).messageId(domain.getMessageId())
                .sourceSystem(domain.getSourceSystem()).sourceId(domain.getSourceId())
                .sourceVersion(domain.getSourceVersion())
                .entityType(domain.getEntityType() != null ? domain.getEntityType().name() : null)
                .entityCode(domain.getEntityCode()).ingestionChannel(domain.getIngestionChannel())
                .receivedAt(domain.getReceivedAt()).processedAt(domain.getProcessedAt())
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .errorCode(domain.getErrorCode()).errorMessage(domain.getErrorMessage())
                .payloadHash(domain.getPayloadHash())
                .createBy(domain.getCreateBy()).createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy()).modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion()).rowValid(domain.getRowValid())
                .build();
    }

    private IngestionLog toDomain(IngestionLogPo po) {
        if (po == null) return null;
        return IngestionLog.builder()
                .id(po.getId()).messageId(po.getMessageId())
                .sourceSystem(po.getSourceSystem()).sourceId(po.getSourceId())
                .sourceVersion(po.getSourceVersion())
                .entityType(po.getEntityType() != null ? EntityType.valueOf(po.getEntityType()) : null)
                .entityCode(po.getEntityCode()).ingestionChannel(po.getIngestionChannel())
                .receivedAt(po.getReceivedAt()).processedAt(po.getProcessedAt())
                .status(po.getStatus() != null ? IngestionStatus.valueOf(po.getStatus()) : null)
                .errorCode(po.getErrorCode()).errorMessage(po.getErrorMessage())
                .payloadHash(po.getPayloadHash())
                .createBy(po.getCreateBy()).createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy()).modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion()).rowValid(po.getRowValid())
                .build();
    }
}
