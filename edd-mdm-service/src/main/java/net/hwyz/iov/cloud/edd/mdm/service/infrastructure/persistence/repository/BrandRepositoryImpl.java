package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.BrandConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.BrandMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.BrandPo;
import org.springframework.stereotype.Repository;

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

    @Override
    public Brand save(Brand brand) {
        BrandPo po = brandConverter.toPo(brand);
        if (po.getId() == null) {
            brandMapper.insert(po);
        } else {
            brandMapper.updateById(po);
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
}
