package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.BrandHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.BrandConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.BrandHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.BrandHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.BrandMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.BrandHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.BrandPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 品牌仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandMapper brandMapper;
    private final BrandConverter brandConverter;
    private final BrandHistoryMapper brandHistoryMapper;
    private final BrandHistoryConverter brandHistoryConverter;

    @Override
    public Brand save(Brand brand, String operationType) {
        BrandPo po = brandConverter.toPo(brand);
        if (po.getId() == null) {
            brandMapper.insert(po);
        } else {
            brandMapper.updateById(po);
        }
        // 写入历史快照
        if (operationType != null) {
            BrandHistoryPo historyPo = BrandHistoryPo.builder()
                    .entityId(po.getId())
                    .code(po.getCode())
                    .name(po.getName())
                    .nameLocal(po.getNameLocal())
                    .description(po.getDescription())
                    .logo(po.getLogo())
                    .country(po.getCountry())
                    .foundedYear(po.getFoundedYear())
                    .sourceSystem(po.getSourceSystem())
                    .sourceId(po.getSourceId())
                    .sourceVersion(po.getSourceVersion())
                    .ingestionChannel(po.getIngestionChannel())
                    .ingestionTime(po.getIngestionTime())
                    .sourcePayloadHash(po.getSourcePayloadHash())
                    .version(po.getVersion())
                    .effectiveFrom(po.getEffectiveFrom())
                    .effectiveTo(po.getEffectiveTo())
                    .status(po.getStatus())
                    .operationType(operationType)
                    .snapshotTime(new Date())
                    .operator(po.getModifyBy())
                    .createBy(po.getModifyBy())
                    .createTime(new Date())
                    .modifyBy(po.getModifyBy())
                    .modifyTime(new Date())
                    .rowVersion(0)
                    .rowValid(true)
                    .build();
            brandHistoryMapper.insert(historyPo);
        }
        return brandConverter.toDomain(po);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        BrandPo po = brandMapper.selectById(id);
        return Optional.ofNullable(brandConverter.toDomain(po));
    }

    @Override
    public Optional<Brand> findByCode(String code) {
        LambdaQueryWrapper<BrandPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrandPo::getCode, code);
        wrapper.eq(BrandPo::getRowValid, true);
        BrandPo po = brandMapper.selectOne(wrapper);
        return Optional.ofNullable(brandConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<BrandPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrandPo::getCode, code);
        wrapper.eq(BrandPo::getRowValid, true);
        return brandMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Brand> findAll(int page, int size, boolean includeInactive) {
        Page<BrandPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<BrandPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrandPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(BrandPo::getStatus, "ACTIVE");
        }
        wrapper.orderByDesc(BrandPo::getCreateTime);
        Page<BrandPo> result = brandMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(brandConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive) {
        LambdaQueryWrapper<BrandPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrandPo::getRowValid, true);
        if (!includeInactive) {
            wrapper.eq(BrandPo::getStatus, "ACTIVE");
        }
        return brandMapper.selectCount(wrapper);
    }

    @Override
    public void delete(Brand brand) {
        BrandPo po = brandConverter.toPo(brand);
        brandMapper.updateById(po);
    }

    @Override
    public List<BrandHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<BrandHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BrandHistoryPo::getCode, code);
        wrapper.eq(BrandHistoryPo::getRowValid, true);
        wrapper.orderByDesc(BrandHistoryPo::getVersion);
        List<BrandHistoryPo> poList = brandHistoryMapper.selectList(wrapper);
        return poList.stream()
                .map(brandHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }
}
