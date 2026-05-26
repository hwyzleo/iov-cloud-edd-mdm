package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 事件发件箱服务实现
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    public void publishBrandCreatedEvent(Brand brand) {
        BrandCreatedEvent event = BrandCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.brand.created")
                .occurredAt(new Date())
                .entityId(brand.getCode())
                .version(brand.getVersion())
                .payload(brand)
                .build();

        outboxRepository.saveBrandCreatedEvent(event);
        log.info("发布品牌创建事件: {}", brand.getCode());
    }

    @Override
    public void publishBrandUpdatedEvent(Brand brand) {
        BrandUpdatedEvent event = BrandUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.brand.updated")
                .occurredAt(new Date())
                .entityId(brand.getCode())
                .version(brand.getVersion())
                .payload(brand)
                .build();

        outboxRepository.saveBrandUpdatedEvent(event);
        log.info("发布品牌更新事件: {}", brand.getCode());
    }

    @Override
    public void publishBrandDeactivatedEvent(Brand brand) {
        BrandDeactivatedEvent event = BrandDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.brand.deactivated")
                .occurredAt(new Date())
                .entityId(brand.getCode())
                .version(brand.getVersion())
                .payload(brand)
                .build();

        outboxRepository.saveBrandDeactivatedEvent(event);
        log.info("发布品牌失效事件: {}", brand.getCode());
    }

    @Override
    public void publishCarLineCreatedEvent(CarLine carLine) {
        CarLineCreatedEvent event = CarLineCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.carLine.created")
                .occurredAt(new Date())
                .entityId(carLine.getCode())
                .version(carLine.getVersion())
                .payload(carLine)
                .build();

        outboxRepository.saveCarLineCreatedEvent(event);
        log.info("发布车系创建事件: {}", carLine.getCode());
    }

    @Override
    public void publishCarLineUpdatedEvent(CarLine carLine) {
        CarLineUpdatedEvent event = CarLineUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.carLine.updated")
                .occurredAt(new Date())
                .entityId(carLine.getCode())
                .version(carLine.getVersion())
                .payload(carLine)
                .build();

        outboxRepository.saveCarLineUpdatedEvent(event);
        log.info("发布车系更新事件: {}", carLine.getCode());
    }

    @Override
    public void publishCarLineDeactivatedEvent(CarLine carLine) {
        CarLineDeactivatedEvent event = CarLineDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.carLine.deactivated")
                .occurredAt(new Date())
                .entityId(carLine.getCode())
                .version(carLine.getVersion())
                .payload(carLine)
                .build();

        outboxRepository.saveCarLineDeactivatedEvent(event);
        log.info("发布车系失效事件: {}", carLine.getCode());
    }

    @Override
    public void publishPlatformCreatedEvent(Platform platform) {
        PlatformCreatedEvent event = PlatformCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.platform.created")
                .occurredAt(new Date())
                .entityId(platform.getCode())
                .version(platform.getVersion())
                .payload(platform)
                .build();

        outboxRepository.savePlatformCreatedEvent(event);
        log.info("发布平台创建事件: {}", platform.getCode());
    }

    @Override
    public void publishPlatformUpdatedEvent(Platform platform) {
        PlatformUpdatedEvent event = PlatformUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.platform.updated")
                .occurredAt(new Date())
                .entityId(platform.getCode())
                .version(platform.getVersion())
                .payload(platform)
                .build();

        outboxRepository.savePlatformUpdatedEvent(event);
        log.info("发布平台更新事件: {}", platform.getCode());
    }

    @Override
    public void publishPlatformDeactivatedEvent(Platform platform) {
        PlatformDeactivatedEvent event = PlatformDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.platform.deactivated")
                .occurredAt(new Date())
                .entityId(platform.getCode())
                .version(platform.getVersion())
                .payload(platform)
                .build();

        outboxRepository.savePlatformDeactivatedEvent(event);
        log.info("发布平台失效事件: {}", platform.getCode());
    }
}
