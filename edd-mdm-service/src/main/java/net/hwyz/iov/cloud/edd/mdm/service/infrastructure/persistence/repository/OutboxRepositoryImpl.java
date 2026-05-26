package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.CarLineCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.CarLineUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.CarLineDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.OutboxRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.OutboxMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.OutboxPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 事件发件箱仓储实现
 *
 * @author hwyz_leo
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxMapper outboxMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void saveBrandCreatedEvent(BrandCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("BRAND")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存品牌创建事件失败", e);
            throw new RuntimeException("保存品牌创建事件失败", e);
        }
    }

    @Override
    public void saveBrandUpdatedEvent(BrandUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("BRAND")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存品牌更新事件失败", e);
            throw new RuntimeException("保存品牌更新事件失败", e);
        }
    }

    @Override
    public void saveBrandDeactivatedEvent(BrandDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("BRAND")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存品牌失效事件失败", e);
            throw new RuntimeException("保存品牌失效事件失败", e);
        }
    }

    @Override
    public void saveCarLineCreatedEvent(CarLineCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SERIES")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存车系创建事件失败", e);
            throw new RuntimeException("保存车系创建事件失败", e);
        }
    }

    @Override
    public void saveCarLineUpdatedEvent(CarLineUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SERIES")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存车系更新事件失败", e);
            throw new RuntimeException("保存车系更新事件失败", e);
        }
    }

    @Override
    public void saveCarLineDeactivatedEvent(CarLineDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SERIES")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存车系失效事件失败", e);
            throw new RuntimeException("保存车系失效事件失败", e);
        }
    }

    @Override
    public void savePlatformCreatedEvent(PlatformCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLATFORM")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存平台创建事件失败", e);
            throw new RuntimeException("保存平台创建事件失败", e);
        }
    }

    @Override
    public void savePlatformUpdatedEvent(PlatformUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLATFORM")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存平台更新事件失败", e);
            throw new RuntimeException("保存平台更新事件失败", e);
        }
    }

    @Override
    public void savePlatformDeactivatedEvent(PlatformDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLATFORM")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event.getPayload()))
                    .occurredAt(event.getOccurredAt())
                    .sent(false)
                    .retryCount(0)
                    .createBy("system")
                    .createTime(new Date())
                    .modifyBy("system")
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            outboxMapper.insert(po);
        } catch (Exception e) {
            log.error("保存平台失效事件失败", e);
            throw new RuntimeException("保存平台失效事件失败", e);
        }
    }

    @Override
    public List<Object> findPendingEvents(int batchSize) {
        LambdaQueryWrapper<OutboxPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OutboxPo::getSent, false);
        wrapper.eq(OutboxPo::getRowValid, true);
        wrapper.orderByAsc(OutboxPo::getOccurredAt);
        wrapper.last("LIMIT " + batchSize);
        return (List<Object>) (List<?>) outboxMapper.selectList(wrapper);
    }

    @Override
    public void markEventAsSent(String eventId) {
        LambdaUpdateWrapper<OutboxPo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OutboxPo::getId, Long.parseLong(eventId));
        wrapper.set(OutboxPo::getSent, true);
        wrapper.set(OutboxPo::getSentAt, new Date());
        wrapper.set(OutboxPo::getModifyBy, "system");
        wrapper.set(OutboxPo::getModifyTime, new Date());
        outboxMapper.update(null, wrapper);
    }

    @Override
    public void incrementRetryCount(String eventId) {
        LambdaUpdateWrapper<OutboxPo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OutboxPo::getId, Long.parseLong(eventId));
        wrapper.setSql("retry_count = retry_count + 1");
        wrapper.set(OutboxPo::getModifyBy, "system");
        wrapper.set(OutboxPo::getModifyTime, new Date());
        outboxMapper.update(null, wrapper);
    }
}
