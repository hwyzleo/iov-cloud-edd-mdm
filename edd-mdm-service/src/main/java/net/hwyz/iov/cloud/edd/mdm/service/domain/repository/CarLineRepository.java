package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.CarLineHistory;

import java.util.List;
import java.util.Optional;

/**
 * 车系仓储接口
 *
 * @author hwyz_leo
 */
public interface CarLineRepository {

    /**
     * 保存车系
     *
     * @param carLine 车系聚合根
     * @return 保存后的车系
     */
    CarLine save(CarLine carLine, String operationType);

    /**
     * 根据ID查找车系
     *
     * @param id 主键ID
     * @return 车系
     */
    Optional<CarLine> findById(Long id);

    /**
     * 根据code查找车系
     *
     * @param code 业务主键
     * @return 车系
     */
    Optional<CarLine> findByCode(String code);

    /**
     * 检查code是否存在
     *
     * @param code 业务主键
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 分页查询车系列表
     *
     * @param page            页码
     * @param size            每页大小
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 车系列表
     */
    List<CarLine> findAll(int page, int size, String brandCode, boolean includeInactive);

    /**
     * 查询车系总数
     *
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    long count(String brandCode, boolean includeInactive);

    /**
     * 删除车系
     *
     * @param carLine 车系聚合根
     */
    void delete(CarLine carLine);

    /**
     * 查询车系历史版本列表
     *
     * @param code 车系code
     * @return 历史版本列表
     */
    List<CarLineHistory> findHistoryByCode(String code);
}
