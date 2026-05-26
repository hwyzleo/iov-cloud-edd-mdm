package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.IngestionLog;

import java.util.List;
import java.util.Optional;

/**
 * 数据接入日志仓储接口
 *
 * @author hwyz_leo
 */
public interface IngestionLogRepository {

    /**
     * 保存数据接入日志
     *
     * @param log 数据接入日志
     * @return 保存后的数据接入日志
     */
    IngestionLog save(IngestionLog log);

    /**
     * 根据消息ID查找数据接入日志
     *
     * @param messageId 消息ID
     * @return 数据接入日志
     */
    Optional<IngestionLog> findByMessageId(String messageId);

    /**
     * 分页查询数据接入日志列表
     *
     * @param page         页码
     * @param size         每页大小
     * @param sourceSystem 来源系统
     * @param entityType   实体类型
     * @param status       接入状态
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 数据接入日志列表
     */
    List<IngestionLog> findAll(int page, int size, String sourceSystem,
                               String entityType, String status,
                               String startTime, String endTime);

    /**
     * 查询数据接入日志总数
     *
     * @param sourceSystem 来源系统
     * @param entityType   实体类型
     * @param status       接入状态
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 总数
     */
    long count(String sourceSystem, String entityType, String status,
               String startTime, String endTime);
}
