package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Supplier;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Plant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.VehicleNode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.DeviceCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
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
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.DeviceCategoryCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.DeviceCategoryUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.DeviceCategoryDeletedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SoftwareBaselineCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SoftwareBaselineUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SoftwareBaselineReleasedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SoftwareBaselineSupersededEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SoftwareBaselineDeletedEvent;
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

    @Override
    public void publishOptionFamilyCreatedEvent(OptionFamily optionFamily) {
        OptionFamilyCreatedEvent event = OptionFamilyCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.optionFamily.created")
                .occurredAt(new Date())
                .entityId(optionFamily.getCode())
                .version(optionFamily.getVersion())
                .payload(optionFamily)
                .build();

        outboxRepository.saveOptionFamilyCreatedEvent(event);
        log.info("发布选项族创建事件: {}", optionFamily.getCode());
    }

    @Override
    public void publishOptionFamilyUpdatedEvent(OptionFamily optionFamily) {
        OptionFamilyUpdatedEvent event = OptionFamilyUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.optionFamily.updated")
                .occurredAt(new Date())
                .entityId(optionFamily.getCode())
                .version(optionFamily.getVersion())
                .payload(optionFamily)
                .build();

        outboxRepository.saveOptionFamilyUpdatedEvent(event);
        log.info("发布选项族更新事件: {}", optionFamily.getCode());
    }

    @Override
    public void publishOptionFamilyDeactivatedEvent(OptionFamily optionFamily) {
        OptionFamilyDeactivatedEvent event = OptionFamilyDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.optionFamily.deactivated")
                .occurredAt(new Date())
                .entityId(optionFamily.getCode())
                .version(optionFamily.getVersion())
                .payload(optionFamily)
                .build();

        outboxRepository.saveOptionFamilyDeactivatedEvent(event);
        log.info("发布选项族失效事件: {}", optionFamily.getCode());
    }

    @Override
    public void publishModelCreatedEvent(Model model) {
        ModelCreatedEvent event = ModelCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.model.created")
                .occurredAt(new Date())
                .entityId(model.getCode())
                .version(model.getVersion())
                .payload(model)
                .build();

        outboxRepository.saveModelCreatedEvent(event);
        log.info("发布车型创建事件: {}", model.getCode());
    }

    @Override
    public void publishModelUpdatedEvent(Model model) {
        ModelUpdatedEvent event = ModelUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.model.updated")
                .occurredAt(new Date())
                .entityId(model.getCode())
                .version(model.getVersion())
                .payload(model)
                .build();

        outboxRepository.saveModelUpdatedEvent(event);
        log.info("发布车型更新事件: {}", model.getCode());
    }

    @Override
    public void publishModelDeactivatedEvent(Model model) {
        ModelDeactivatedEvent event = ModelDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.model.deactivated")
                .occurredAt(new Date())
                .entityId(model.getCode())
                .version(model.getVersion())
                .payload(model)
                .build();

        outboxRepository.saveModelDeactivatedEvent(event);
        log.info("发布车型失效事件: {}", model.getCode());
    }

    @Override
    public void publishVariantCreatedEvent(Variant variant) {
        VariantCreatedEvent event = VariantCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.variant.created")
                .occurredAt(new Date())
                .entityId(variant.getCode())
                .version(variant.getVersion())
                .payload(variant)
                .build();

        outboxRepository.saveVariantCreatedEvent(event);
        log.info("发布版本创建事件: {}", variant.getCode());
    }

    @Override
    public void publishVariantUpdatedEvent(Variant variant) {
        VariantUpdatedEvent event = VariantUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.variant.updated")
                .occurredAt(new Date())
                .entityId(variant.getCode())
                .version(variant.getVersion())
                .payload(variant)
                .build();

        outboxRepository.saveVariantUpdatedEvent(event);
        log.info("发布版本更新事件: {}", variant.getCode());
    }

    @Override
    public void publishVariantDeactivatedEvent(Variant variant) {
        VariantDeactivatedEvent event = VariantDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.variant.deactivated")
                .occurredAt(new Date())
                .entityId(variant.getCode())
                .version(variant.getVersion())
                .payload(variant)
                .build();

        outboxRepository.saveVariantDeactivatedEvent(event);
        log.info("发布版本失效事件: {}", variant.getCode());
    }

    @Override
    public void publishConfigurationCreatedEvent(Configuration configuration) {
        ConfigurationCreatedEvent event = ConfigurationCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.configuration.created")
                .occurredAt(new Date())
                .entityId(configuration.getCode())
                .version(configuration.getVersion())
                .payload(configuration)
                .build();

        outboxRepository.saveConfigurationCreatedEvent(event);
        log.info("发布配置创建事件: {}", configuration.getCode());
    }

    @Override
    public void publishConfigurationUpdatedEvent(Configuration configuration) {
        ConfigurationUpdatedEvent event = ConfigurationUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.configuration.updated")
                .occurredAt(new Date())
                .entityId(configuration.getCode())
                .version(configuration.getVersion())
                .payload(configuration)
                .build();

        outboxRepository.saveConfigurationUpdatedEvent(event);
        log.info("发布配置更新事件: {}", configuration.getCode());
    }

    @Override
    public void publishConfigurationDeactivatedEvent(Configuration configuration) {
        ConfigurationDeactivatedEvent event = ConfigurationDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.product.configuration.deactivated")
                .occurredAt(new Date())
                .entityId(configuration.getCode())
                .version(configuration.getVersion())
                .payload(configuration)
                .build();

        outboxRepository.saveConfigurationDeactivatedEvent(event);
        log.info("发布配置失效事件: {}", configuration.getCode());
    }

    @Override
    public void publishSupplierCreatedEvent(Supplier supplier) {
        SupplierCreatedEvent event = SupplierCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.party.supplier.created")
                .occurredAt(new Date())
                .entityId(supplier.getCode())
                .version(supplier.getVersion())
                .payload(supplier)
                .build();

        outboxRepository.saveSupplierCreatedEvent(event);
        log.info("发布供应商创建事件: {}", supplier.getCode());
    }

    @Override
    public void publishSupplierUpdatedEvent(Supplier supplier) {
        SupplierUpdatedEvent event = SupplierUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.party.supplier.updated")
                .occurredAt(new Date())
                .entityId(supplier.getCode())
                .version(supplier.getVersion())
                .payload(supplier)
                .build();

        outboxRepository.saveSupplierUpdatedEvent(event);
        log.info("发布供应商更新事件: {}", supplier.getCode());
    }

    @Override
    public void publishSupplierDeactivatedEvent(Supplier supplier) {
        SupplierDeactivatedEvent event = SupplierDeactivatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("mdm.party.supplier.deactivated")
                .occurredAt(new Date())
                .entityId(supplier.getCode())
                .version(supplier.getVersion())
                .payload(supplier)
                .build();

        outboxRepository.saveSupplierDeactivatedEvent(event);
        log.info("发布供应商失效事件: {}", supplier.getCode());
    }

    @Override
    public void publishVehicleNodeCreatedEvent(VehicleNode vehicleNode) {
        VehicleNodeCreatedEvent event = VehicleNodeCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("VehicleNodeCreated")
                .occurredAt(new Date())
                .entityId(vehicleNode.getNodeCode())
                .version(vehicleNode.getVersion())
                .payload(vehicleNode)
                .build();

        outboxRepository.saveVehicleNodeCreatedEvent(event);
        log.info("发布车载节点创建事件: {}", vehicleNode.getNodeCode());
    }

    @Override
    public void publishVehicleNodeUpdatedEvent(VehicleNode vehicleNode) {
        VehicleNodeUpdatedEvent event = VehicleNodeUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("VehicleNodeUpdated")
                .occurredAt(new Date())
                .entityId(vehicleNode.getNodeCode())
                .version(vehicleNode.getVersion())
                .payload(vehicleNode)
                .build();

        outboxRepository.saveVehicleNodeUpdatedEvent(event);
        log.info("发布车载节点更新事件: {}", vehicleNode.getNodeCode());
    }

    @Override
    public void publishVehicleNodeDeletedEvent(VehicleNode vehicleNode, boolean forceDelete) {
        VehicleNodeDeletedEvent event = VehicleNodeDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("VehicleNodeDeleted")
                .occurredAt(new Date())
                .entityId(vehicleNode.getNodeCode())
                .version(vehicleNode.getVersion())
                .payload(vehicleNode)
                .forceDelete(forceDelete)
                .build();

        outboxRepository.saveVehicleNodeDeletedEvent(event);
        log.info("发布车载节点删除事件: {} forceDelete={}", vehicleNode.getNodeCode(), forceDelete);
    }

    @Override
    public void publishPlantCreatedEvent(Plant plant) {
        PlantCreatedEvent event = PlantCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PlantCreated")
                .occurredAt(new Date())
                .entityId(plant.getCode())
                .version(plant.getVersion())
                .payload(plant)
                .build();

        outboxRepository.savePlantCreatedEvent(event);
        log.info("发布工厂创建事件: {}", plant.getCode());
    }

    @Override
    public void publishPlantUpdatedEvent(Plant plant) {
        PlantUpdatedEvent event = PlantUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PlantUpdated")
                .occurredAt(new Date())
                .entityId(plant.getCode())
                .version(plant.getVersion())
                .payload(plant)
                .build();

        outboxRepository.savePlantUpdatedEvent(event);
        log.info("发布工厂更新事件: {}", plant.getCode());
    }

    @Override
    public void publishPlantDeletedEvent(Plant plant, boolean forceDelete) {
        PlantDeletedEvent event = PlantDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PlantDeleted")
                .occurredAt(new Date())
                .entityId(plant.getCode())
                .version(plant.getVersion())
                .payload(plant)
                .forceDelete(forceDelete)
                .build();

        outboxRepository.savePlantDeletedEvent(event);
        log.info("发布工厂删除事件: {} forceDelete={}", plant.getCode(), forceDelete);
    }

    @Override
    public void publishMaterialCategoryCreatedEvent(MaterialCategory category) {
        MaterialCategoryCreatedEvent event = MaterialCategoryCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("MaterialCategoryCreated")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .build();

        outboxRepository.saveMaterialCategoryCreatedEvent(event);
        log.info("发布物料分类创建事件: {}", category.getCode());
    }

    @Override
    public void publishMaterialCategoryUpdatedEvent(MaterialCategory category) {
        MaterialCategoryUpdatedEvent event = MaterialCategoryUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("MaterialCategoryUpdated")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .build();

        outboxRepository.saveMaterialCategoryUpdatedEvent(event);
        log.info("发布物料分类更新事件: {}", category.getCode());
    }

    @Override
    public void publishMaterialCategoryDeletedEvent(MaterialCategory category, boolean forceDelete) {
        MaterialCategoryDeletedEvent event = MaterialCategoryDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("MaterialCategoryDeleted")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .forceDelete(forceDelete)
                .build();

        outboxRepository.saveMaterialCategoryDeletedEvent(event);
        log.info("发布物料分类删除事件: {} forceDelete={}", category.getCode(), forceDelete);
    }

    @Override
    public void publishPartCreatedEvent(Part part) {
        PartCreatedEvent event = PartCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PartCreated")
                .occurredAt(new Date())
                .entityId(part.getCode())
                .version(part.getVersion())
                .payload(part)
                .build();

        outboxRepository.savePartCreatedEvent(event);
        log.info("发布零件创建事件: {}", part.getCode());
    }

    @Override
    public void publishPartUpdatedEvent(Part part) {
        PartUpdatedEvent event = PartUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PartUpdated")
                .occurredAt(new Date())
                .entityId(part.getCode())
                .version(part.getVersion())
                .payload(part)
                .build();

        outboxRepository.savePartUpdatedEvent(event);
        log.info("发布零件更新事件: {}", part.getCode());
    }

    @Override
    public void publishPartDeletedEvent(Part part, boolean forceDelete) {
        PartDeletedEvent event = PartDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("PartDeleted")
                .occurredAt(new Date())
                .entityId(part.getCode())
                .version(part.getVersion())
                .payload(part)
                .forceDelete(forceDelete)
                .build();

        outboxRepository.savePartDeletedEvent(event);
        log.info("发布零件删除事件: {} forceDelete={}", part.getCode(), forceDelete);
    }

    @Override
    public void publishDeviceCategoryCreatedEvent(DeviceCategory category) {
        DeviceCategoryCreatedEvent event = DeviceCategoryCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DeviceCategoryCreated")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .build();
        outboxRepository.saveDeviceCategoryCreatedEvent(event);
        log.info("发布设备类别创建事件: {}", category.getCode());
    }

    @Override
    public void publishDeviceCategoryUpdatedEvent(DeviceCategory category) {
        DeviceCategoryUpdatedEvent event = DeviceCategoryUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DeviceCategoryUpdated")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .build();
        outboxRepository.saveDeviceCategoryUpdatedEvent(event);
        log.info("发布设备类别更新事件: {}", category.getCode());
    }

    @Override
    public void publishDeviceCategoryDeletedEvent(DeviceCategory category, boolean forceDelete) {
        DeviceCategoryDeletedEvent event = DeviceCategoryDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("DeviceCategoryDeleted")
                .occurredAt(new Date())
                .entityId(category.getCode())
                .version(category.getVersion())
                .payload(category)
                .forceDelete(forceDelete)
                .build();
        outboxRepository.saveDeviceCategoryDeletedEvent(event);
        log.info("发布设备类别删除事件: {} forceDelete={}", category.getCode(), forceDelete);
    }

    @Override
    public void publishSoftwareBaselineCreatedEvent(SoftwareBaseline baseline) {
        SoftwareBaselineCreatedEvent event = SoftwareBaselineCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SoftwareBaselineCreated")
                .occurredAt(new Date())
                .entityId(baseline.getCode())
                .version(baseline.getVersion())
                .payload(baseline)
                .build();
        outboxRepository.saveSoftwareBaselineCreatedEvent(event);
        log.info("发布软件基线创建事件: {}", baseline.getCode());
    }

    @Override
    public void publishSoftwareBaselineUpdatedEvent(SoftwareBaseline baseline) {
        SoftwareBaselineUpdatedEvent event = SoftwareBaselineUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SoftwareBaselineUpdated")
                .occurredAt(new Date())
                .entityId(baseline.getCode())
                .version(baseline.getVersion())
                .payload(baseline)
                .build();
        outboxRepository.saveSoftwareBaselineUpdatedEvent(event);
        log.info("发布软件基线更新事件: {}", baseline.getCode());
    }

    @Override
    public void publishSoftwareBaselineReleasedEvent(SoftwareBaseline baseline) {
        SoftwareBaselineReleasedEvent event = SoftwareBaselineReleasedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SoftwareBaselineReleased")
                .occurredAt(new Date())
                .entityId(baseline.getCode())
                .version(baseline.getVersion())
                .payload(baseline)
                .build();
        outboxRepository.saveSoftwareBaselineReleasedEvent(event);
        log.info("发布软件基线发布事件: {}", baseline.getCode());
    }

    @Override
    public void publishSoftwareBaselineSupersededEvent(SoftwareBaseline baseline) {
        SoftwareBaselineSupersededEvent event = SoftwareBaselineSupersededEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SoftwareBaselineSuperseded")
                .occurredAt(new Date())
                .entityId(baseline.getCode())
                .version(baseline.getVersion())
                .payload(baseline)
                .build();
        outboxRepository.saveSoftwareBaselineSupersededEvent(event);
        log.info("发布软件基线取代事件: {}", baseline.getCode());
    }

    @Override
    public void publishSoftwareBaselineDeletedEvent(SoftwareBaseline baseline, boolean forceDelete) {
        SoftwareBaselineDeletedEvent event = SoftwareBaselineDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("SoftwareBaselineDeleted")
                .occurredAt(new Date())
                .entityId(baseline.getCode())
                .version(baseline.getVersion())
                .payload(baseline)
                .forceDelete(forceDelete)
                .build();
        outboxRepository.saveSoftwareBaselineDeletedEvent(event);
        log.info("发布软件基线删除事件: {} forceDelete={}", baseline.getCode(), forceDelete);
    }
}
