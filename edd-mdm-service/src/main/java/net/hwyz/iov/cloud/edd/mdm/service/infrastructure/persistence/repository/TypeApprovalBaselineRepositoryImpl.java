package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.TypeApprovalBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.TypeApprovalBaselineConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.TaBaselineHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.TaBaselineItemMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.TypeApprovalBaselineMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TaBaselineItemPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.TypeApprovalBaselinePo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 型式批准基线仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class TypeApprovalBaselineRepositoryImpl implements TypeApprovalBaselineRepository {

    private final TypeApprovalBaselineMapper typeApprovalBaselineMapper;
    private final TypeApprovalBaselineConverter typeApprovalBaselineConverter;
    private final TaBaselineHistoryMapper taBaselineHistoryMapper;
    private final TaBaselineItemMapper taBaselineItemMapper;

    @Override
    public TypeApprovalBaseline save(TypeApprovalBaseline baseline, String operationType) {
        TypeApprovalBaselinePo po = typeApprovalBaselineConverter.toPo(baseline);
        if (po.getId() == null) {
            typeApprovalBaselineMapper.insert(po);
        } else {
            typeApprovalBaselineMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType, baseline);
        }
        return loadItems(typeApprovalBaselineConverter.toDomain(po));
    }

    @Override
    public Optional<TypeApprovalBaseline> findByCode(String taBaselineCode) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getTaBaselineCode, taBaselineCode);
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        TypeApprovalBaselinePo po = typeApprovalBaselineMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        TypeApprovalBaseline baseline = typeApprovalBaselineConverter.toDomain(po);
        return Optional.of(loadItems(baseline));
    }

    @Override
    public Optional<TypeApprovalBaseline> findByAnchorAndDigest(String swinCode, String anchorType,
                                                                  String anchorCode, String projectionDigest) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getSwinCode, swinCode);
        wrapper.eq(TypeApprovalBaselinePo::getAnchorType, anchorType);
        wrapper.eq(TypeApprovalBaselinePo::getAnchorCode, anchorCode);
        wrapper.eq(TypeApprovalBaselinePo::getProjectionDigest, projectionDigest);
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        TypeApprovalBaselinePo po = typeApprovalBaselineMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        TypeApprovalBaseline baseline = typeApprovalBaselineConverter.toDomain(po);
        return Optional.of(loadItems(baseline));
    }

    @Override
    public boolean existsByCode(String taBaselineCode) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getTaBaselineCode, taBaselineCode);
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        return typeApprovalBaselineMapper.selectCount(wrapper) > 0;
    }

    @Override
    public long countBySwinCode(String swinCode) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getSwinCode, swinCode);
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        return typeApprovalBaselineMapper.selectCount(wrapper);
    }

    @Override
    public List<TypeApprovalBaseline> listBySwinCode(String swinCode) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getSwinCode, swinCode);
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        return typeApprovalBaselineMapper.selectList(wrapper).stream()
                .map(po -> loadItems(typeApprovalBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public List<TypeApprovalBaseline> list(String swinCode, String anchorType, String anchorCode,
                                             String status, String code, int page, int size) {
        Page<TypeApprovalBaselinePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = buildListWrapper(swinCode, anchorType, anchorCode, status, code);
        wrapper.orderByDesc(TypeApprovalBaselinePo::getCreateTime);
        Page<TypeApprovalBaselinePo> result = typeApprovalBaselineMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(po -> loadItems(typeApprovalBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public long count(String swinCode, String anchorType, String anchorCode, String status, String code) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = buildListWrapper(swinCode, anchorType, anchorCode, status, code);
        return typeApprovalBaselineMapper.selectCount(wrapper);
    }

    @Override
    public void delete(TypeApprovalBaseline baseline, String operator) {
        TypeApprovalBaselinePo po = typeApprovalBaselineConverter.toPo(baseline);
        insertHistory(po, "DELETE", baseline);
        // 删除基线项
        LambdaQueryWrapper<TaBaselineItemPo> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(TaBaselineItemPo::getTaBaselineId, baseline.getId());
        taBaselineItemMapper.delete(itemWrapper);
        // 删除基线主表
        typeApprovalBaselineMapper.deleteById(po.getId());
    }

    @Override
    public void saveHistory(TaBaselineHistory history) {
        TaBaselineHistoryPo po = typeApprovalBaselineConverter.toHistoryPo(
                TypeApprovalBaseline.builder()
                        .id(history.getEntityId())
                        .taBaselineCode(history.getTaBaselineCode())
                        .swinCode(history.getSwinCode())
                        .anchorType(history.getAnchorType())
                        .anchorCode(history.getAnchorCode())
                        .status(history.getStatus())
                        .projectionDigest(history.getProjectionDigest())
                        .sourceBaselineScope(history.getSourceBaselineScope())
                        .effectiveFrom(history.getEffectiveFrom())
                        .remark(history.getRemark())
                        .version(history.getVersion())
                        .createBy(history.getCreateBy())
                        .createTime(history.getCreateTime())
                        .modifyBy(history.getModifyBy())
                        .modifyTime(history.getModifyTime())
                        .rowVersion(history.getRowVersion())
                        .rowValid(history.getRowValid())
                        .build(),
                history.getOperationType(),
                history.getOperator());
        taBaselineHistoryMapper.insert(po);
    }

    @Override
    public List<TaBaselineHistory> findHistoryByCode(String taBaselineCode) {
        LambdaQueryWrapper<TaBaselineHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaBaselineHistoryPo::getTaBaselineCode, taBaselineCode);
        wrapper.orderByDesc(TaBaselineHistoryPo::getVersion);
        return taBaselineHistoryMapper.selectList(wrapper).stream()
                .map(typeApprovalBaselineConverter::toHistoryDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveItem(TaBaselineItem item) {
        TaBaselineItemPo po = typeApprovalBaselineConverter.toPo(item);
        if (po.getId() == null) {
            taBaselineItemMapper.insert(po);
        } else {
            taBaselineItemMapper.updateById(po);
        }
    }

    @Override
    public void deleteItemsByBaselineId(Long taBaselineId) {
        LambdaQueryWrapper<TaBaselineItemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaBaselineItemPo::getTaBaselineId, taBaselineId);
        taBaselineItemMapper.delete(wrapper);
    }

    @Override
    public List<TaBaselineItem> findItemsByBaselineId(Long taBaselineId) {
        LambdaQueryWrapper<TaBaselineItemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaBaselineItemPo::getTaBaselineId, taBaselineId);
        wrapper.eq(TaBaselineItemPo::getRowValid, true);
        return taBaselineItemMapper.selectList(wrapper).stream()
                .map(typeApprovalBaselineConverter::toDomain)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<TypeApprovalBaselinePo> buildListWrapper(String swinCode, String anchorType,
                                                                          String anchorCode, String status, String code) {
        LambdaQueryWrapper<TypeApprovalBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TypeApprovalBaselinePo::getRowValid, true);
        if (swinCode != null && !swinCode.isBlank()) {
            wrapper.eq(TypeApprovalBaselinePo::getSwinCode, swinCode);
        }
        if (anchorType != null && !anchorType.isBlank()) {
            wrapper.eq(TypeApprovalBaselinePo::getAnchorType, anchorType);
        }
        if (anchorCode != null && !anchorCode.isBlank()) {
            wrapper.eq(TypeApprovalBaselinePo::getAnchorCode, anchorCode);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(TypeApprovalBaselinePo::getStatus, status);
        }
        if (code != null && !code.isBlank()) {
            wrapper.like(TypeApprovalBaselinePo::getTaBaselineCode, code);
        }
        return wrapper;
    }

    private TypeApprovalBaseline loadItems(TypeApprovalBaseline baseline) {
        if (baseline == null) {
            return null;
        }
        List<TaBaselineItem> items = findItemsByBaselineId(baseline.getId());
        baseline.setItems(items);
        return baseline;
    }

    private void insertHistory(TypeApprovalBaselinePo po, String operationType, TypeApprovalBaseline baseline) {
        TaBaselineHistoryPo historyPo = typeApprovalBaselineConverter.toHistoryPo(
                baseline != null ? baseline : typeApprovalBaselineConverter.toDomain(po),
                operationType,
                po.getModifyBy());
        taBaselineHistoryMapper.insert(historyPo);
    }
}
