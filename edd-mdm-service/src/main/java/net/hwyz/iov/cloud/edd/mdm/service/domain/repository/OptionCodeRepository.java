package net.hwyz.iov.cloud.edd.mdm.service.domain.repository;

import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.OptionCode;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.OptionCodeHistory;

import java.util.List;
import java.util.Optional;

/**
 * 选项码仓储接口
 *
 * @author hwyz_leo
 */
public interface OptionCodeRepository {

    OptionCode save(OptionCode optionCode, String operationType);

    Optional<OptionCode> findById(Long id);

    Optional<OptionCode> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByOptionFamilyCode(String optionFamilyCode);

    /**
     * 检查是否存在指定选项族下的活跃选项码
     *
     * @param optionFamilyCode 选项族code
     * @return 是否存在ACTIVE状态的选项码
     */
    boolean existsByOptionFamilyCodeAndStatusActive(String optionFamilyCode);

    List<OptionCode> findAll(int page, int size, String optionFamilyCode, boolean includeInactive);

    long count(String optionFamilyCode, boolean includeInactive);

    void delete(OptionCode optionCode);

    List<OptionCodeHistory> findHistoryByCode(String code);
}
