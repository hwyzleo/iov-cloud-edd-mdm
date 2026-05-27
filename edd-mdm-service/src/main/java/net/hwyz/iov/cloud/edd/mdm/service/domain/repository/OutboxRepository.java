package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

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

import java.util.List;

/**
 * 事件发件箱仓储接口
 *
 * @author hwyz_leo
 */
public interface OutboxRepository {

    /**
     * 保存品牌创建事件
     *
     * @param event 品牌创建事件
     */
    void saveBrandCreatedEvent(BrandCreatedEvent event);

    /**
     * 保存品牌更新事件
     *
     * @param event 品牌更新事件
     */
    void saveBrandUpdatedEvent(BrandUpdatedEvent event);

    /**
     * 保存品牌失效事件
     *
     * @param event 品牌失效事件
     */
    void saveBrandDeactivatedEvent(BrandDeactivatedEvent event);

    /**
     * 保存车系创建事件
     *
     * @param event 车系创建事件
     */
    void saveCarLineCreatedEvent(CarLineCreatedEvent event);

    /**
     * 保存车系更新事件
     *
     * @param event 车系更新事件
     */
    void saveCarLineUpdatedEvent(CarLineUpdatedEvent event);

    /**
     * 保存车系失效事件
     *
     * @param event 车系失效事件
     */
    void saveCarLineDeactivatedEvent(CarLineDeactivatedEvent event);

    /**
     * 保存平台创建事件
     *
     * @param event 平台创建事件
     */
    void savePlatformCreatedEvent(PlatformCreatedEvent event);

    /**
     * 保存平台更新事件
     *
     * @param event 平台更新事件
     */
    void savePlatformUpdatedEvent(PlatformUpdatedEvent event);

    /**
     * 保存平台失效事件
     *
     * @param event 平台失效事件
     */
    void savePlatformDeactivatedEvent(PlatformDeactivatedEvent event);

    /**
     * 保存选项族创建事件
     *
     * @param event 选项族创建事件
     */
    void saveOptionFamilyCreatedEvent(OptionFamilyCreatedEvent event);

    /**
     * 保存选项族更新事件
     *
     * @param event 选项族更新事件
     */
    void saveOptionFamilyUpdatedEvent(OptionFamilyUpdatedEvent event);

    /**
     * 保存选项族失效事件
     *
     * @param event 选项族失效事件
     */
    void saveOptionFamilyDeactivatedEvent(OptionFamilyDeactivatedEvent event);

    /**
     * 保存车型创建事件
     *
     * @param event 车型创建事件
     */
    void saveModelCreatedEvent(ModelCreatedEvent event);

    /**
     * 保存车型更新事件
     *
     * @param event 车型更新事件
     */
    void saveModelUpdatedEvent(ModelUpdatedEvent event);

    /**
     * 保存车型失效事件
     *
     * @param event 车型失效事件
     */
    void saveModelDeactivatedEvent(ModelDeactivatedEvent event);

    /**
     * 保存版本创建事件
     *
     * @param event 版本创建事件
     */
    void saveVariantCreatedEvent(VariantCreatedEvent event);

    /**
     * 保存版本更新事件
     *
     * @param event 版本更新事件
     */
    void saveVariantUpdatedEvent(VariantUpdatedEvent event);

    /**
     * 保存版本失效事件
     *
     * @param event 版本失效事件
     */
    void saveVariantDeactivatedEvent(VariantDeactivatedEvent event);

    /**
     * 保存配置创建事件
     *
     * @param event 配置创建事件
     */
    void saveConfigurationCreatedEvent(ConfigurationCreatedEvent event);

    /**
     * 保存配置更新事件
     *
     * @param event 配置更新事件
     */
    void saveConfigurationUpdatedEvent(ConfigurationUpdatedEvent event);

    /**
     * 保存配置失效事件
     *
     * @param event 配置失效事件
     */
    void saveConfigurationDeactivatedEvent(ConfigurationDeactivatedEvent event);

    /**
     * 保存供应商创建事件
     *
     * @param event 供应商创建事件
     */
    void saveSupplierCreatedEvent(SupplierCreatedEvent event);

    /**
     * 保存供应商更新事件
     *
     * @param event 供应商更新事件
     */
    void saveSupplierUpdatedEvent(SupplierUpdatedEvent event);

    /**
     * 保存供应商失效事件
     *
     * @param event 供应商失效事件
     */
    void saveSupplierDeactivatedEvent(SupplierDeactivatedEvent event);

    /**
     * 获取待发送的事件列表
     *
     * @param batchSize 批量大小
     * @return 事件列表
     */
    List<Object> findPendingEvents(int batchSize);

    /**
     * 标记事件为已发送
     *
     * @param eventId 事件ID
     */
    void markEventAsSent(String eventId);

    /**
     * 增加重试次数
     *
     * @param eventId 事件ID
     */
    void incrementRetryCount(String eventId);
}
