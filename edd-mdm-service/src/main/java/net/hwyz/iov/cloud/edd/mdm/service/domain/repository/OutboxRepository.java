package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.BrandDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SeriesCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SeriesUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.SeriesDeactivatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformCreatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformUpdatedEvent;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.event.PlatformDeactivatedEvent;

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
    void saveSeriesCreatedEvent(SeriesCreatedEvent event);

    /**
     * 保存车系更新事件
     *
     * @param event 车系更新事件
     */
    void saveSeriesUpdatedEvent(SeriesUpdatedEvent event);

    /**
     * 保存车系失效事件
     *
     * @param event 车系失效事件
     */
    void saveSeriesDeactivatedEvent(SeriesDeactivatedEvent event);

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
