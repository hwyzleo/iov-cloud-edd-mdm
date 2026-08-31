package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionFamily;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionFamilyHistory;

import java.util.List;
import java.util.Optional;

/**
 * 选项族仓储接口
 *
 * @author hwyz_leo
 */
public interface OptionFamilyRepository {

    OptionFamily save(OptionFamily optionFamily, String operationType);

    Optional<OptionFamily> findById(Long id);

    Optional<OptionFamily> findByCode(String code);

    boolean existsByCode(String code);

    List<OptionFamily> findAll(int page, int size, boolean includeInactive, String category);

    long count(boolean includeInactive, String category);

    /**
     * 查询全部未删除（row_valid=1）的选项族，用于 CR-035 名称标准化防重比对
     *
     * @return row_valid=1 的选项族列表
     */
    List<OptionFamily> findAllForNameCheck();

    void delete(OptionFamily optionFamily);

    List<OptionFamilyHistory> findHistoryByCode(String code);
}
