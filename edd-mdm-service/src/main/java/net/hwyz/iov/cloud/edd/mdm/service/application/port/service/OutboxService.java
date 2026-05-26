package net.hwyz.iov.cloud.edd.mdm.service.application.port.service;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;

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
}
