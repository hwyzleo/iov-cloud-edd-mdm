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
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ConfigurationCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ConfigurationUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ConfigurationDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ModelCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ModelUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.ModelDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.OptionFamilyCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.OptionFamilyUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.OptionFamilyDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SupplierCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SupplierUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SupplierDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VariantCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VariantUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VariantDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlantCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlantUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlantDeletedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VehicleNodeCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VehicleNodeUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.VehicleNodeDeletedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.MaterialCategoryCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.MaterialCategoryUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.MaterialCategoryDeletedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PartCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PartUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PartDeletedEvent;
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
                    .aggregateType("CAR_LINE")
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
                    .aggregateType("CAR_LINE")
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
                    .aggregateType("CAR_LINE")
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
    public void saveOptionFamilyCreatedEvent(OptionFamilyCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("OPTION_FAMILY")
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
            log.error("保存选项族创建事件失败", e);
            throw new RuntimeException("保存选项族创建事件失败", e);
        }
    }

    @Override
    public void saveOptionFamilyUpdatedEvent(OptionFamilyUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("OPTION_FAMILY")
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
            log.error("保存选项族更新事件失败", e);
            throw new RuntimeException("保存选项族更新事件失败", e);
        }
    }

    @Override
    public void saveOptionFamilyDeactivatedEvent(OptionFamilyDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("OPTION_FAMILY")
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
            log.error("保存选项族失效事件失败", e);
            throw new RuntimeException("保存选项族失效事件失败", e);
        }
    }

    @Override
    public void saveModelCreatedEvent(ModelCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MODEL")
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
            log.error("保存车型创建事件失败", e);
            throw new RuntimeException("保存车型创建事件失败", e);
        }
    }

    @Override
    public void saveModelUpdatedEvent(ModelUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MODEL")
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
            log.error("保存车型更新事件失败", e);
            throw new RuntimeException("保存车型更新事件失败", e);
        }
    }

    @Override
    public void saveModelDeactivatedEvent(ModelDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MODEL")
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
            log.error("保存车型失效事件失败", e);
            throw new RuntimeException("保存车型失效事件失败", e);
        }
    }

    @Override
    public void saveVariantCreatedEvent(VariantCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VARIANT")
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
            log.error("保存版本创建事件失败", e);
            throw new RuntimeException("保存版本创建事件失败", e);
        }
    }

    @Override
    public void saveVariantUpdatedEvent(VariantUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VARIANT")
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
            log.error("保存版本更新事件失败", e);
            throw new RuntimeException("保存版本更新事件失败", e);
        }
    }

    @Override
    public void saveVariantDeactivatedEvent(VariantDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VARIANT")
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
            log.error("保存版本失效事件失败", e);
            throw new RuntimeException("保存版本失效事件失败", e);
        }
    }

    @Override
    public void saveConfigurationCreatedEvent(ConfigurationCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("CONFIGURATION")
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
            log.error("保存配置创建事件失败", e);
            throw new RuntimeException("保存配置创建事件失败", e);
        }
    }

    @Override
    public void saveConfigurationUpdatedEvent(ConfigurationUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("CONFIGURATION")
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
            log.error("保存配置更新事件失败", e);
            throw new RuntimeException("保存配置更新事件失败", e);
        }
    }

    @Override
    public void saveConfigurationDeactivatedEvent(ConfigurationDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("CONFIGURATION")
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
            log.error("保存配置失效事件失败", e);
            throw new RuntimeException("保存配置失效事件失败", e);
        }
    }

    @Override
    public void saveSupplierCreatedEvent(SupplierCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SUPPLIER")
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
            log.error("保存供应商创建事件失败", e);
            throw new RuntimeException("保存供应商创建事件失败", e);
        }
    }

    @Override
    public void saveSupplierUpdatedEvent(SupplierUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SUPPLIER")
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
            log.error("保存供应商更新事件失败", e);
            throw new RuntimeException("保存供应商更新事件失败", e);
        }
    }

    @Override
    public void saveSupplierDeactivatedEvent(SupplierDeactivatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("SUPPLIER")
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
            log.error("保存供应商失效事件失败", e);
            throw new RuntimeException("保存供应商失效事件失败", e);
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

    @Override
    public void saveVehicleNodeCreatedEvent(VehicleNodeCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VEHICLE_NODE")
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
            log.error("保存车载节点创建事件失败", e);
            throw new RuntimeException("保存车载节点创建事件失败", e);
        }
    }

    @Override
    public void saveVehicleNodeUpdatedEvent(VehicleNodeUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VEHICLE_NODE")
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
            log.error("保存车载节点更新事件失败", e);
            throw new RuntimeException("保存车载节点更新事件失败", e);
        }
    }

    @Override
    public void saveVehicleNodeDeletedEvent(VehicleNodeDeletedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("VEHICLE_NODE")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event))
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
            log.error("保存车载节点删除事件失败", e);
            throw new RuntimeException("保存车载节点删除事件失败", e);
        }
    }

    @Override
    public void savePlantCreatedEvent(PlantCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLANT")
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
            log.error("保存工厂创建事件失败", e);
            throw new RuntimeException("保存工厂创建事件失败", e);
        }
    }

    @Override
    public void savePlantUpdatedEvent(PlantUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLANT")
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
            log.error("保存工厂更新事件失败", e);
            throw new RuntimeException("保存工厂更新事件失败", e);
        }
    }

    @Override
    public void savePlantDeletedEvent(PlantDeletedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PLANT")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event))
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
            log.error("保存工厂删除事件失败", e);
            throw new RuntimeException("保存工厂删除事件失败", e);
        }
    }

    @Override
    public void saveMaterialCategoryCreatedEvent(MaterialCategoryCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MATERIAL_CATEGORY")
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
            log.error("保存物料分类创建事件失败", e);
            throw new RuntimeException("保存物料分类创建事件失败", e);
        }
    }

    @Override
    public void saveMaterialCategoryUpdatedEvent(MaterialCategoryUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MATERIAL_CATEGORY")
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
            log.error("保存物料分类更新事件失败", e);
            throw new RuntimeException("保存物料分类更新事件失败", e);
        }
    }

    @Override
    public void saveMaterialCategoryDeletedEvent(MaterialCategoryDeletedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("MATERIAL_CATEGORY")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event))
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
            log.error("保存物料分类删除事件失败", e);
            throw new RuntimeException("保存物料分类删除事件失败", e);
        }
    }

    @Override
    public void savePartCreatedEvent(PartCreatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PART")
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
            log.error("保存零件创建事件失败", e);
            throw new RuntimeException("保存零件创建事件失败", e);
        }
    }

    @Override
    public void savePartUpdatedEvent(PartUpdatedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PART")
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
            log.error("保存零件更新事件失败", e);
            throw new RuntimeException("保存零件更新事件失败", e);
        }
    }

    @Override
    public void savePartDeletedEvent(PartDeletedEvent event) {
        try {
            OutboxPo po = OutboxPo.builder()
                    .aggregateType("PART")
                    .aggregateId(event.getEntityId())
                    .eventType(event.getEventType())
                    .payload(objectMapper.writeValueAsString(event))
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
            log.error("保存零件删除事件失败", e);
            throw new RuntimeException("保存零件删除事件失败", e);
        }
    }
}
