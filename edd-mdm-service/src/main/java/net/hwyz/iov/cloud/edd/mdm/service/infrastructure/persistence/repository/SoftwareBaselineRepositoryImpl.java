package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.converter.SoftwareBaselineConverter;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SoftwareBaselineHistoryMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SoftwareBaselineItemMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.mapper.SoftwareBaselineMapper;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselineHistoryPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselineItemPo;
import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.persistence.po.SoftwareBaselinePo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 软件基线仓储实现
 *
 * @author hwyz_leo
 */
@Repository
@RequiredArgsConstructor
public class SoftwareBaselineRepositoryImpl implements SoftwareBaselineRepository {

    private final SoftwareBaselineMapper softwareBaselineMapper;
    private final SoftwareBaselineConverter softwareBaselineConverter;
    private final SoftwareBaselineHistoryMapper softwareBaselineHistoryMapper;
    private final SoftwareBaselineItemMapper softwareBaselineItemMapper;

    @Override
    public SoftwareBaseline save(SoftwareBaseline baseline, String operationType) {
        SoftwareBaselinePo po = softwareBaselineConverter.toPo(baseline);
        if (po.getId() == null) {
            softwareBaselineMapper.insert(po);
        } else {
            softwareBaselineMapper.updateById(po);
        }
        if (operationType != null) {
            insertHistory(po, operationType, false, baseline);
        }
        return loadItems(softwareBaselineConverter.toDomain(po));
    }

    @Override
    public Optional<SoftwareBaseline> findByCode(String code) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getCode, code);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        SoftwareBaselinePo po = softwareBaselineMapper.selectOne(wrapper);
        if (po == null) {
            return Optional.empty();
        }
        SoftwareBaseline baseline = softwareBaselineConverter.toDomain(po);
        return Optional.of(loadItems(baseline));
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getCode, code);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        return softwareBaselineMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByAnchorAndVersion(AnchorType anchorType, String anchorCode, String baselineVersion) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType.name());
        wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        wrapper.eq(SoftwareBaselinePo::getBaselineVersion, baselineVersion);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        return softwareBaselineMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void delete(SoftwareBaseline baseline, String operator, boolean forceDelete) {
        SoftwareBaselinePo po = softwareBaselineConverter.toPo(baseline);
        insertHistory(po, "DELETE", forceDelete, baseline);
        LambdaQueryWrapper<SoftwareBaselineItemPo> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SoftwareBaselineItemPo::getBaselineCode, baseline.getCode());
        softwareBaselineItemMapper.delete(itemWrapper);
        softwareBaselineMapper.deleteById(po.getId());
    }

    @Override
    public List<SoftwareBaseline> list(String anchorType, String anchorCode, String baselineStatus,
                                        String status, int page, int size) {
        Page<SoftwareBaselinePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = buildListWrapper(anchorType, anchorCode, baselineStatus, status);
        wrapper.orderByDesc(SoftwareBaselinePo::getCreateTime);
        Page<SoftwareBaselinePo> result = softwareBaselineMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(po -> loadItems(softwareBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public long count(String anchorType, String anchorCode, String baselineStatus, String status) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = buildListWrapper(anchorType, anchorCode, baselineStatus, status);
        return softwareBaselineMapper.selectCount(wrapper);
    }

    @Override
    public List<SoftwareBaseline> snapshot(boolean includeDraft, boolean includeSuperseded, int page, int size) {
        Page<SoftwareBaselinePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        if (!includeDraft) {
            wrapper.ne(SoftwareBaselinePo::getBaselineStatus, "DRAFT");
        }
        if (!includeSuperseded) {
            wrapper.ne(SoftwareBaselinePo::getBaselineStatus, "SUPERSEDED");
        }
        wrapper.orderByDesc(SoftwareBaselinePo::getCreateTime);
        Page<SoftwareBaselinePo> result = softwareBaselineMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(po -> loadItems(softwareBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public long snapshotCount(boolean includeDraft, boolean includeSuperseded) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        if (!includeDraft) {
            wrapper.ne(SoftwareBaselinePo::getBaselineStatus, "DRAFT");
        }
        if (!includeSuperseded) {
            wrapper.ne(SoftwareBaselinePo::getBaselineStatus, "SUPERSEDED");
        }
        return softwareBaselineMapper.selectCount(wrapper);
    }

    @Override
    public List<SoftwareBaseline> findActiveByAnchor(AnchorType anchorType, String anchorCode) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType.name());
        wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        wrapper.eq(SoftwareBaselinePo::getBaselineStatus, "RELEASED");
        wrapper.orderByDesc(SoftwareBaselinePo::getBaselineVersion);
        return softwareBaselineMapper.selectList(wrapper).stream()
                .map(po -> loadItems(softwareBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public List<SoftwareBaseline> listAllActive() {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        wrapper.eq(SoftwareBaselinePo::getStatus, "ACTIVE");
        wrapper.orderByDesc(SoftwareBaselinePo::getCreateTime);
        return softwareBaselineMapper.selectList(wrapper).stream()
                .map(po -> loadItems(softwareBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public long countByAnchor(AnchorType anchorType, String anchorCode) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType.name());
        wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        return softwareBaselineMapper.selectCount(wrapper);
    }

    @Override
    public long countByPartCode(String partCode) {
        LambdaQueryWrapper<SoftwareBaselineItemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselineItemPo::getPartCode, partCode);
        wrapper.eq(SoftwareBaselineItemPo::getRowValid, true);
        return softwareBaselineItemMapper.selectCount(wrapper);
    }

    @Override
    public List<SoftwareBaseline> listByPartCode(String partCode) {
        LambdaQueryWrapper<SoftwareBaselineItemPo> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(SoftwareBaselineItemPo::getPartCode, partCode);
        itemWrapper.eq(SoftwareBaselineItemPo::getRowValid, true);
        List<String> baselineCodes = softwareBaselineItemMapper.selectList(itemWrapper).stream()
                .map(SoftwareBaselineItemPo::getBaselineCode)
                .distinct()
                .collect(Collectors.toList());
        if (baselineCodes.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SoftwareBaselinePo::getCode, baselineCodes);
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        return softwareBaselineMapper.selectList(wrapper).stream()
                .map(po -> loadItems(softwareBaselineConverter.toDomain(po)))
                .collect(Collectors.toList());
    }

    @Override
    public List<SoftwareBaselineHistory> findHistoryByCode(String code) {
        LambdaQueryWrapper<SoftwareBaselineHistoryPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselineHistoryPo::getCode, code);
        wrapper.orderByDesc(SoftwareBaselineHistoryPo::getVersion);
        return softwareBaselineHistoryMapper.selectList(wrapper).stream()
                .map(softwareBaselineConverter::toHistoryDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveItem(SoftwareBaselineItem item) {
        SoftwareBaselineItemPo po = softwareBaselineConverter.toPo(item);
        if (po.getId() == null) {
            softwareBaselineItemMapper.insert(po);
        } else {
            softwareBaselineItemMapper.updateById(po);
        }
    }

    @Override
    public void deleteItem(SoftwareBaselineItem item) {
        SoftwareBaselineItemPo po = softwareBaselineConverter.toPo(item);
        softwareBaselineItemMapper.deleteById(po.getId());
    }

    @Override
    public void deleteItemByBaselineAndPart(String baselineCode, String partCode) {
        LambdaQueryWrapper<SoftwareBaselineItemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselineItemPo::getBaselineCode, baselineCode);
        wrapper.eq(SoftwareBaselineItemPo::getPartCode, partCode);
        softwareBaselineItemMapper.delete(wrapper);
    }

    @Override
    public List<SoftwareBaselineItem> findItemsByBaselineCode(String baselineCode) {
        LambdaQueryWrapper<SoftwareBaselineItemPo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselineItemPo::getBaselineCode, baselineCode);
        wrapper.eq(SoftwareBaselineItemPo::getRowValid, true);
        return softwareBaselineItemMapper.selectList(wrapper).stream()
                .map(softwareBaselineConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByFilter(String anchorType, String anchorCode, String baselineStatus, List<String> codes) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        if (anchorType != null && !anchorType.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType);
        }
        if (anchorCode != null && !anchorCode.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        }
        if (baselineStatus != null && !baselineStatus.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getBaselineStatus, baselineStatus);
        }
        if (codes != null && !codes.isEmpty()) {
            wrapper.in(SoftwareBaselinePo::getCode, codes);
        }
        return softwareBaselineMapper.selectCount(wrapper);
    }

    @Override
    public List<String> listCodesByFilter(String anchorType, String anchorCode, String baselineStatus,
                                           List<String> codes, int page, int size) {
        Page<SoftwareBaselinePo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        if (anchorType != null && !anchorType.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType);
        }
        if (anchorCode != null && !anchorCode.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        }
        if (baselineStatus != null && !baselineStatus.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getBaselineStatus, baselineStatus);
        }
        if (codes != null && !codes.isEmpty()) {
            wrapper.in(SoftwareBaselinePo::getCode, codes);
        }
        wrapper.orderByAsc(SoftwareBaselinePo::getCode);
        Page<SoftwareBaselinePo> result = softwareBaselineMapper.selectPage(pageParam, wrapper);
        return result.getRecords().stream()
                .map(SoftwareBaselinePo::getCode)
                .collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SoftwareBaselinePo> buildListWrapper(String anchorType, String anchorCode,
                                                                     String baselineStatus, String status) {
        LambdaQueryWrapper<SoftwareBaselinePo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SoftwareBaselinePo::getRowValid, true);
        if (anchorType != null && !anchorType.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorType, anchorType);
        }
        if (anchorCode != null && !anchorCode.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getAnchorCode, anchorCode);
        }
        if (baselineStatus != null && !baselineStatus.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getBaselineStatus, baselineStatus);
        }
        if (status != null && !status.isBlank()) {
            wrapper.eq(SoftwareBaselinePo::getStatus, status);
        }
        return wrapper;
    }

    private SoftwareBaseline loadItems(SoftwareBaseline baseline) {
        if (baseline == null) {
            return null;
        }
        List<SoftwareBaselineItem> items = findItemsByBaselineCode(baseline.getCode());
        baseline.setItems(items);
        return baseline;
    }

    private void insertHistory(SoftwareBaselinePo po, String operationType, boolean forceDelete,
                                SoftwareBaseline baseline) {
        SoftwareBaselineHistoryPo historyPo = softwareBaselineConverter.toHistoryPo(
                baseline != null ? baseline : softwareBaselineConverter.toDomain(po),
                operationType, po.getModifyBy(), forceDelete);
        softwareBaselineHistoryMapper.insert(historyPo);
    }
}
