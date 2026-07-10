package net.hwyz.iov.cloud.edd.mdm.service.application.port.service;

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
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.RxswinRegistry;

/**
 * 事件发件箱服务端口
 *
 * @author hwyz_leo
 */
public interface OutboxService {

    /**
     * 发布品牌创建事件
     *
     * @param brand 品牌聚合根
     */
    void publishBrandCreatedEvent(Brand brand);

    /**
     * 发布品牌更新事件
     *
     * @param brand 品牌聚合根
     */
    void publishBrandUpdatedEvent(Brand brand);

    /**
     * 发布品牌失效事件
     *
     * @param brand 品牌聚合根
     */
    void publishBrandDeactivatedEvent(Brand brand);

    /**
     * 发布车系创建事件
     *
     * @param carLine 车系聚合根
     */
    void publishCarLineCreatedEvent(CarLine carLine);

    /**
     * 发布车系更新事件
     *
     * @param carLine 车系聚合根
     */
    void publishCarLineUpdatedEvent(CarLine carLine);

    /**
     * 发布车系失效事件
     *
     * @param carLine 车系聚合根
     */
    void publishCarLineDeactivatedEvent(CarLine carLine);

    /**
     * 发布平台创建事件
     *
     * @param platform 平台聚合根
     */
    void publishPlatformCreatedEvent(Platform platform);

    /**
     * 发布平台更新事件
     *
     * @param platform 平台聚合根
     */
    void publishPlatformUpdatedEvent(Platform platform);

    /**
     * 发布平台失效事件
     *
     * @param platform 平台聚合根
     */
    void publishPlatformDeactivatedEvent(Platform platform);

    /**
     * 发布选项族创建事件
     *
     * @param optionFamily 选项族聚合根
     */
    void publishOptionFamilyCreatedEvent(OptionFamily optionFamily);

    /**
     * 发布选项族更新事件
     *
     * @param optionFamily 选项族聚合根
     */
    void publishOptionFamilyUpdatedEvent(OptionFamily optionFamily);

    /**
     * 发布选项族失效事件
     *
     * @param optionFamily 选项族聚合根
     */
    void publishOptionFamilyDeactivatedEvent(OptionFamily optionFamily);

    /**
     * 发布车型创建事件
     *
     * @param model 车型聚合根
     */
    void publishModelCreatedEvent(Model model);

    /**
     * 发布车型更新事件
     *
     * @param model 车型聚合根
     */
    void publishModelUpdatedEvent(Model model);

    /**
     * 发布车型失效事件
     *
     * @param model 车型聚合根
     */
    void publishModelDeactivatedEvent(Model model);

    /**
     * 发布版本创建事件
     *
     * @param variant 版本聚合根
     */
    void publishVariantCreatedEvent(Variant variant);

    /**
     * 发布版本更新事件
     *
     * @param variant 版本聚合根
     */
    void publishVariantUpdatedEvent(Variant variant);

    /**
     * 发布版本失效事件
     *
     * @param variant 版本聚合根
     */
    void publishVariantDeactivatedEvent(Variant variant);

    /**
     * 发布配置创建事件
     *
     * @param configuration 配置聚合根
     */
    void publishConfigurationCreatedEvent(Configuration configuration);

    /**
     * 发布配置更新事件
     *
     * @param configuration 配置聚合根
     */
    void publishConfigurationUpdatedEvent(Configuration configuration);

    /**
     * 发布配置失效事件
     *
     * @param configuration 配置聚合根
     */
    void publishConfigurationDeactivatedEvent(Configuration configuration);

    /**
     * 发布供应商创建事件
     *
     * @param supplier 供应商聚合根
     */
    void publishSupplierCreatedEvent(Supplier supplier);

    /**
     * 发布供应商更新事件
     *
     * @param supplier 供应商聚合根
     */
    void publishSupplierUpdatedEvent(Supplier supplier);

    /**
     * 发布供应商失效事件
     *
     * @param supplier 供应商聚合根
     */
    void publishSupplierDeactivatedEvent(Supplier supplier);

    /**
     * 发布车载节点创建事件（EEAD 子域，topic: mdm.eead.vehicleNode.event）
     *
     * @param vehicleNode 车载节点聚合根
     */
    void publishVehicleNodeCreatedEvent(VehicleNode vehicleNode);

    /**
     * 发布车载节点更新事件（含失效，EEAD 子域）
     *
     * @param vehicleNode 车载节点聚合根
     */
    void publishVehicleNodeUpdatedEvent(VehicleNode vehicleNode);

    /**
     * 发布车载节点删除事件（EEAD 子域）
     *
     * @param vehicleNode   车载节点聚合根（删除前最后一份快照）
     * @param forceDelete   是否 force 旁路删除
     */
    void publishVehicleNodeDeletedEvent(VehicleNode vehicleNode, boolean forceDelete);

    /**
     * 发布工厂创建事件（Org 子域）
     *
     * @param plant 工厂聚合根
     */
    void publishPlantCreatedEvent(Plant plant);

    /**
     * 发布工厂更新事件（含失效，Org 子域）
     *
     * @param plant 工厂聚合根
     */
    void publishPlantUpdatedEvent(Plant plant);

    /**
     * 发布工厂删除事件（Org 子域）
     *
     * @param plant       工厂聚合根（删除前最后一份快照）
     * @param forceDelete 是否 force 旁路删除
     */
    void publishPlantDeletedEvent(Plant plant, boolean forceDelete);

    /**
     * 发布物料分类创建事件（Material 子域）
     *
     * @param category 物料分类聚合根
     */
    void publishMaterialCategoryCreatedEvent(MaterialCategory category);

    /**
     * 发布物料分类更新事件（Material 子域）
     *
     * @param category 物料分类聚合根
     */
    void publishMaterialCategoryUpdatedEvent(MaterialCategory category);

    /**
     * 发布物料分类删除事件（Material 子域）
     *
     * @param category    物料分类聚合根（删除前最后一份快照）
     * @param forceDelete 是否 force 旁路删除
     */
    void publishMaterialCategoryDeletedEvent(MaterialCategory category, boolean forceDelete);

    /**
     * 发布零件创建事件（Material 子域）
     *
     * @param part 零件聚合根
     */
    void publishPartCreatedEvent(Part part);

    /**
     * 发布零件更新事件（Material 子域）
     *
     * @param part 零件聚合根
     */
    void publishPartUpdatedEvent(Part part);

    /**
     * 发布零件删除事件（Material 子域）
     *
     * @param part        零件聚合根（删除前最后一份快照）
     * @param forceDelete 是否 force 旁路删除
     */
    void publishPartDeletedEvent(Part part, boolean forceDelete);

    /**
     * 发布设备类别创建事件（EEAD 子域）
     *
     * @param category 设备类别聚合根
     */
    void publishDeviceCategoryCreatedEvent(DeviceCategory category);

    /**
     * 发布设备类别更新事件（EEAD 子域）
     *
     * @param category 设备类别聚合根
     */
    void publishDeviceCategoryUpdatedEvent(DeviceCategory category);

    /**
     * 发布设备类别删除事件（EEAD 子域）
     *
     * @param category    设备类别聚合根（删除前最后一份快照）
     * @param forceDelete 是否 force 旁路删除
     */
    void publishDeviceCategoryDeletedEvent(DeviceCategory category, boolean forceDelete);

    /**
     * 发布软件基线创建事件（Material 子域）
     *
     * @param baseline 软件基线聚合根
     */
    void publishSoftwareBaselineCreatedEvent(SoftwareBaseline baseline);

    /**
     * 发布软件基线更新事件（Material 子域）
     *
     * @param baseline 软件基线聚合根
     */
    void publishSoftwareBaselineUpdatedEvent(SoftwareBaseline baseline);

    /**
     * 发布软件基线发布事件（Material 子域）
     *
     * @param baseline 软件基线聚合根
     */
    void publishSoftwareBaselineReleasedEvent(SoftwareBaseline baseline);

    /**
     * 发布软件基线取代事件（Material 子域）
     *
     * @param baseline 软件基线聚合根
     */
    void publishSoftwareBaselineSupersededEvent(SoftwareBaseline baseline);

    /**
     * 发布软件基线删除事件（Material 子域）
     *
     * @param baseline    软件基线聚合根（删除前最后一份快照）
     * @param forceDelete 是否 force 旁路删除
     */
    void publishSoftwareBaselineDeletedEvent(SoftwareBaseline baseline, boolean forceDelete);

    /**
     * 发布RXSWIN登记创建事件（EEAD 子域）
     *
     * @param rxswinRegistry RXSWIN登记聚合根
     */
    void publishRxswinRegistryCreatedEvent(RxswinRegistry rxswinRegistry);
}
