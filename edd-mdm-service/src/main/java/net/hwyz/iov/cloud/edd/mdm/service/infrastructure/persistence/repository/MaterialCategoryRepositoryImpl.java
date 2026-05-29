package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.MaterialCategory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.MaterialCategoryHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.MaterialCategoryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.MaterialCategoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.MaterialCategoryHistoryConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.MaterialCategoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.PartMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.MaterialCategoryHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.MaterialCategoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.PartPo;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 物料品类仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class MaterialCategoryRepositoryImpl implements MaterialCategoryRepository {

    private final MaterialCategoryMapper materialCategoryMapper;
    private final MaterialCategoryConverter materialCategoryConverter;
    private final MaterialCategoryHistoryMapper materialCategoryHistoryMapper;
    private final MaterialCategoryHistoryConverter materialCategoryHistoryConverter;
    private final PartMapper partMapper;

    @Override
    public MaterialCategory save(MaterialCategory category, String operationType) {
        MaterialCategoryPo po = materialCategoryConverter.toPo(category);
        if (po.getId() == null) {
            materialCategoryMapper.insert(po);
        } else {
            materialCategoryMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType);
        }
        return materialCategoryConverter.toDomain(po);
    }

    @Override
    public Optional<MaterialCategory> findByCode(String code) {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getCode, code);
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        MaterialCategoryPo po = materialCategoryMapper.selectOne(wrapper);
        return Optional.ofNullable(materialCategoryConverter.toDomain(po));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getCode, code);
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasChildren(String parentCode) {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getParentCode, parentCode);
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasParts(String categoryCode) {
        LambdaQueryWrapper<PartPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartPo::getCategoryCode, categoryCode);
        wrapper.eq(PartPo::getRowValid, true);
        return partMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(MaterialCategory category, String operator) {
        MaterialCategoryPo po = materialCategoryConverter.toPo(category);
        insertHistory(po, "DELETE");
        materialCategoryMapper.deleteById(po.getId());
    }

    @Override
    public List<MaterialCategory> list(String parentCode, String status, int page, int size) {
        Page<MaterialCategoryPo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = buildListWrapper(parentCode, status);
        wrapper.orderByDesc(MaterialCategoryPo::getCreateTime);
        Page<MaterialCategoryPo> result = materialCategoryMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(materialCategoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count(String parentCode, String status) {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = buildListWrapper(parentCode, status);
        return materialCategoryMapper.selectCount(wrapper);
    }

    @Override
    public List<MaterialCategory> listAllActive() {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        wrapper.eq(MaterialCategoryPo::getStatus, "ACTIVE");
        wrapper.orderByAsc(MaterialCategoryPo::getCode);
        return materialCategoryMapper.selectList(wrapper).stream()
                .map(materialCategoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialCategory> tree() {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        wrapper.eq(MaterialCategoryPo::getStatus, "ACTIVE");
        wrapper.orderByAsc(MaterialCategoryPo::getCode);
        return materialCategoryMapper.selectList(wrapper).stream()
                .map(materialCategoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MaterialCategoryHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<MaterialCategoryHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryHistoryPo::getCode, code);
        wrapper.orderByDesc(MaterialCategoryHistoryPo::getVersion);
        return materialCategoryHistoryMapper.selectList(wrapper).stream()
                .map(materialCategoryHistoryConverter::toDomain)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<MaterialCategoryPo> buildListWrapper(String parentCode, String status) {
        LambdaQueryWrapper<MaterialCategoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategoryPo::getRowValid, true);
        if (parentCode != null && !parentCode.isBlank()) {
            wrapper.eq(MaterialCategoryPo::getParentCode, parentCode);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(MaterialCategoryPo::getStatus, status);
        } else {
            wrapper.eq(MaterialCategoryPo::getStatus, "ACTIVE");
        }
        return wrapper;
    }

    private void insertHistory(MaterialCategoryPo po, String operationType) {
        Date now = new Date();
        MaterialCategoryHistoryPo historyPo = MaterialCategoryHistoryPo.builder()
                .entityId(po.getId())
                .operationType(operationType)
                .snapshotTime(now)
                .operator(po.getModifyBy())
                .code(po.getCode())
                .name(po.getName())
                .nameLocal(po.getNameLocal())
                .description(po.getDescription())
                .parentCode(po.getParentCode())
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
                .createBy(po.getModifyBy())
                .createTime(now)
                .modifyBy(po.getModifyBy())
                .modifyTime(now)
                .rowVersion(0)
                .rowValid(true)
                .build();
        materialCategoryHistoryMapper.insert(historyPo);
    }
}
