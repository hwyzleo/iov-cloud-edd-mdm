package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinScheme;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinRoute;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinSchemeStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinSchemeRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SwinSchemeMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SwinSchemePo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SWIN编码方案仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SwinSchemeRepositoryImpl implements SwinSchemeRepository {

    private final SwinSchemeMapper swinSchemeMapper;

    @Override
    public void save(SwinScheme swinScheme) {
        SwinSchemePo po = toPo(swinScheme);
        if (po.getId() == null) {
            swinSchemeMapper.insert(po);
            swinScheme.setId(po.getId());
        } else {
            swinSchemeMapper.updateById(po);
        }
    }

    @Override
    public Optional<SwinScheme> findByCode(String code) {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinSchemePo::getCode, code);
        return Optional.ofNullable(swinSchemeMapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinSchemePo::getCode, code);
        return swinSchemeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<SwinScheme> findAllActive() {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinSchemePo::getStatus, "ACTIVE");
        wrapper.orderByAsc(SwinSchemePo::getSortOrder);
        return swinSchemeMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SwinScheme> findPaginated(int page, int size, boolean includeInactive) {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        if (!includeInactive) {
            wrapper.eq(SwinSchemePo::getStatus, "ACTIVE");
        }
        wrapper.orderByAsc(SwinSchemePo::getSortOrder);
        return swinSchemeMapper.selectPage(new Page<>(page, size), wrapper).getRecords().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive) {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        if (!includeInactive) {
            wrapper.eq(SwinSchemePo::getStatus, "ACTIVE");
        }
        return swinSchemeMapper.selectCount(wrapper);
    }

    @Override
    public void deleteByCode(String code) {
        LambdaQueryWrapper<SwinSchemePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinSchemePo::getCode, code);
        swinSchemeMapper.delete(wrapper);
    }

    private SwinSchemePo toPo(SwinScheme domain) {
        return SwinSchemePo.builder()
                .id(domain.getId()).code(domain.getCode()).name(domain.getName())
                .nameLocal(domain.getNameLocal()).description(domain.getDescription())
                .route(domain.getRoute().name()).sortOrder(domain.getSortOrder())
                .source(domain.getSource()).externalRefId(domain.getExternalRefId())
                .externalVersion(domain.getExternalVersion()).lastSyncTime(domain.getLastSyncTime())
                .version(domain.getVersion()).effectiveFrom(domain.getEffectiveFrom())
                .effectiveTo(domain.getEffectiveTo()).status(domain.getStatus().name())
                .createBy(domain.getCreateBy()).createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy()).modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion()).rowValid(domain.getRowValid())
                .build();
    }

    private SwinScheme toDomain(SwinSchemePo po) {
        return SwinScheme.builder()
                .id(po.getId()).code(po.getCode()).name(po.getName())
                .nameLocal(po.getNameLocal()).description(po.getDescription())
                .route(SwinRoute.valueOf(po.getRoute()))
                .sortOrder(po.getSortOrder())
                .source(po.getSource()).externalRefId(po.getExternalRefId())
                .externalVersion(po.getExternalVersion()).lastSyncTime(po.getLastSyncTime())
                .version(po.getVersion()).effectiveFrom(po.getEffectiveFrom())
                .effectiveTo(po.getEffectiveTo())
                .status(SwinSchemeStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy()).createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy()).modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion()).rowValid(po.getRowValid())
                .build();
    }
}
