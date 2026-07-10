package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.RxswinRegistry;

import java.util.List;
import java.util.Optional;

/**
 * RXSWIN登记仓储接口
 *
 * @author hwyz_leo
 */
public interface RxswinRegistryRepository {

    /**
     * 保存RXSWIN登记记录
     *
     * @param rxswinRegistry RXSWIN登记记录
     */
    void save(RxswinRegistry rxswinRegistry);

    /**
     * 根据manifestCode查找登记记录
     *
     * @param manifestCode manifest业务键
     * @return RXSWIN登记记录
     */
    Optional<RxswinRegistry> findByManifestCode(String manifestCode);

    /**
     * 根据swinCode统计引用数（供SWIN删除保护使用）
     *
     * @param swinCode SWIN代码
     * @return 有效登记引用数
     */
    long countBySwinCode(String swinCode);

    /**
     * 分页查询RXSWIN登记记录
     *
     * @param page              页码
     * @param size              每页大小
     * @param manifestCode      manifestCode筛选（可选）
     * @param rxswinValue       rxswinValue筛选（可选）
     * @param swinCode          swinCode筛选（可选）
     * @param softwareBaselineCode softwareBaselineCode筛选（可选）
     * @param registeredAtStart 登记时间范围起始（可选）
     * @param registeredAtEnd   登记时间范围结束（可选）
     * @return RXSWIN登记记录列表
     */
    List<RxswinRegistry> findPaginated(int page, int size, String manifestCode, String rxswinValue,
                                        String swinCode, String softwareBaselineCode,
                                        java.util.Date registeredAtStart, java.util.Date registeredAtEnd);

    /**
     * 统计符合条件的记录总数
     *
     * @param manifestCode      manifestCode筛选（可选）
     * @param rxswinValue       rxswinValue筛选（可选）
     * @param swinCode          swinCode筛选（可选）
     * @param softwareBaselineCode softwareBaselineCode筛选（可选）
     * @param registeredAtStart 登记时间范围起始（可选）
     * @param registeredAtEnd   登记时间范围结束（可选）
     * @return 总数
     */
    long count(String manifestCode, String rxswinValue, String swinCode,
               String softwareBaselineCode, java.util.Date registeredAtStart, java.util.Date registeredAtEnd);
}
