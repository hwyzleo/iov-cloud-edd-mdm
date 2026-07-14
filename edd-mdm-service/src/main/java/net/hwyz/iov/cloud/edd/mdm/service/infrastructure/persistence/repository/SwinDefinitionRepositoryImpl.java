package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinManagedSystem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinManagedSystemRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SwinDefinitionMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SwinDefinitionPo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SWIN定义仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SwinDefinitionRepositoryImpl implements SwinDefinitionRepository {

    private final SwinDefinitionMapper swinDefinitionMapper;
    private final SwinManagedSystemRepository swinManagedSystemRepository;

    @Override
    public void save(SwinDefinition swinDefinition) {
        SwinDefinitionPo po = toPo(swinDefinition);
        if (po.getId() == null) {
            swinDefinitionMapper.insert(po);
            swinDefinition.setId(po.getId());
        } else {
            swinDefinitionMapper.updateById(po);
        }
    }

    @Override
    public Optional<SwinDefinition> findBySwinCode(String swinCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getSwinCode, swinCode);
        return Optional.ofNullable(swinDefinitionMapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public boolean existsBySwinCode(String swinCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getSwinCode, swinCode);
        return swinDefinitionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countBySchemeCode(String schemeCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getSchemeCode, schemeCode);
        return swinDefinitionMapper.selectCount(wrapper);
    }

    @Override
    public long countActiveByTypeRef(String typeRefType, String typeRefCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        wrapper.eq(SwinDefinitionPo::getTypeRefType, typeRefType);
        wrapper.eq(SwinDefinitionPo::getTypeRefCode, typeRefCode);
        return swinDefinitionMapper.selectCount(wrapper);
    }

    @Override
    public List<SwinDefinition> findAllActive() {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        wrapper.orderByAsc(SwinDefinitionPo::getId);
        return swinDefinitionMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SwinDefinition> findPaginated(int page, int size, boolean includeInactive) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        if (!includeInactive) {
            wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        }
        wrapper.orderByAsc(SwinDefinitionPo::getId);
        return swinDefinitionMapper.selectPage(new Page<>(page, size), wrapper).getRecords().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(boolean includeInactive) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        if (!includeInactive) {
            wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        }
        return swinDefinitionMapper.selectCount(wrapper);
    }

    @Override
    public void deleteBySwinCode(String swinCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getSwinCode, swinCode);
        swinDefinitionMapper.delete(wrapper);
    }

    @Override
    public List<SwinDefinition> findAllActiveBySchemeCode(String schemeCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        wrapper.eq(SwinDefinitionPo::getSchemeCode, schemeCode);
        wrapper.orderByAsc(SwinDefinitionPo::getId);
        return swinDefinitionMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SwinDefinition> findAllActiveByTypeRef(String typeRefType, String typeRefCode) {
        LambdaQueryWrapper<SwinDefinitionPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SwinDefinitionPo::getStatus, "ACTIVE");
        wrapper.eq(SwinDefinitionPo::getTypeRefType, typeRefType);
        wrapper.eq(SwinDefinitionPo::getTypeRefCode, typeRefCode);
        return swinDefinitionMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    private SwinDefinitionPo toPo(SwinDefinition domain) {
        return SwinDefinitionPo.builder()
                .id(domain.getId()).swinCode(domain.getSwinCode())
                .schemeCode(domain.getSchemeCode())
                .typeRefType(domain.getTypeRefType())
                .typeRefCode(domain.getTypeRefCode())
                .name(domain.getName()).nameLocal(domain.getNameLocal()).description(domain.getDescription())
                .version(domain.getVersion()).status(domain.getStatus().name())
                .createBy(domain.getCreateBy()).createTime(domain.getCreateTime())
                .modifyBy(domain.getModifyBy()).modifyTime(domain.getModifyTime())
                .rowVersion(domain.getRowVersion()).rowValid(domain.getRowValid())
                .build();
    }

    private SwinDefinition toDomain(SwinDefinitionPo po) {
        List<SwinManagedSystem> managedSystems = swinManagedSystemRepository.findBySwinCode(po.getSwinCode());
        return SwinDefinition.builder()
                .id(po.getId()).swinCode(po.getSwinCode()).schemeCode(po.getSchemeCode())
                .typeRefType(po.getTypeRefType())
                .typeRefCode(po.getTypeRefCode())
                .name(po.getName()).nameLocal(po.getNameLocal()).description(po.getDescription())
                .version(po.getVersion())
                .status(SwinDefinitionStatus.valueOf(po.getStatus()))
                .createBy(po.getCreateBy()).createTime(po.getCreateTime())
                .modifyBy(po.getModifyBy()).modifyTime(po.getModifyTime())
                .rowVersion(po.getRowVersion()).rowValid(po.getRowValid())
                .managedSystems(managedSystems)
                .build();
    }
}
