package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineItemBindCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineRepublishRequest;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SoftwareBaselineUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SoftwareBaselineQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineItemDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineRepublishBatchResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SoftwareBaselineRepublishResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineItemDuplicateException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SoftwareBaselineRepublishBatchLimitExceededException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Part;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SoftwareBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineDeletionDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.SoftwareBaselineRepublishDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 软件基线应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SoftwareBaselineAppService {

    private final SoftwareBaselineRepository softwareBaselineRepository;
    private final SoftwareBaselineDomainService softwareBaselineDomainService;
    private final SoftwareBaselineDeletionDomainService softwareBaselineDeletionDomainService;
    private final SoftwareBaselineRepublishDomainService softwareBaselineRepublishDomainService;
    private final OutboxService outboxService;

    @Value("${mdm.material.software-baseline.republish.batch-max-size:500}")
    private long republishBatchMaxSize;

    @Value("${mdm.material.software-baseline.republish.batch-commit-size:100}")
    private int republishBatchCommitSize;

    @Transactional(rollbackFor = Exception.class)
    public SoftwareBaselineDto createSoftwareBaseline(SoftwareBaselineCreateCmd cmd) {
        log.info("创建软件基线: anchorType={}, anchorCode={}, baselineVersion={}",
                cmd.getAnchorType(), cmd.getAnchorCode(), cmd.getBaselineVersion());

        AnchorType anchorType = AnchorType.valueOf(cmd.getAnchorType());
        String operator = resolveOperator(cmd.getCreateBy());

        softwareBaselineDomainService.validateAnchor(anchorType, cmd.getAnchorCode());

        String code = softwareBaselineDomainService.generateCode(anchorType, cmd.getAnchorCode(), cmd.getBaselineVersion());
        softwareBaselineDomainService.validateUniqueness(code, anchorType, cmd.getAnchorCode(), cmd.getBaselineVersion());

        SoftwareBaseline baseline = SoftwareBaseline.create(code, cmd.getName(), anchorType,
                cmd.getAnchorCode(), cmd.getBaselineVersion(), cmd.getDescription(),
                cmd.getEffectiveFrom(), cmd.getEffectiveTo(), operator);

        baseline = softwareBaselineRepository.save(baseline, "CREATE");
        outboxService.publishSoftwareBaselineCreatedEvent(baseline);

        return toDto(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public SoftwareBaselineDto updateSoftwareBaseline(SoftwareBaselineUpdateCmd cmd) {
        log.info("更新软件基线: {}", cmd.getCode());

        String operator = resolveOperator(cmd.getModifyBy());
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(cmd.getCode())
                .orElseThrow(() -> new SoftwareBaselineNotExistException(cmd.getCode()));

        baseline.update(cmd.getName(), cmd.getDescription(), cmd.getEffectiveFrom(), cmd.getEffectiveTo(), operator);
        baseline = softwareBaselineRepository.save(baseline, "UPDATE");
        outboxService.publishSoftwareBaselineUpdatedEvent(baseline);

        return toDto(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public SoftwareBaselineDto bindItem(SoftwareBaselineItemBindCmd cmd) {
        log.info("绑定基线项: baselineCode={}, partCode={}", cmd.getBaselineCode(), cmd.getPartCode());

        String operator = resolveOperator(cmd.getOperator());
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(cmd.getBaselineCode())
                .orElseThrow(() -> new SoftwareBaselineNotExistException(cmd.getBaselineCode()));

        Part part = softwareBaselineDomainService.validateBaselineItemPart(cmd.getPartCode());

        if (baseline.getActiveItems().stream().anyMatch(i -> i.getPartCode().equals(cmd.getPartCode()))) {
            throw new SoftwareBaselineItemDuplicateException(cmd.getBaselineCode(), cmd.getPartCode());
        }

        SoftwareBaselineItem item = SoftwareBaselineItem.create(
                baseline.getCode(), cmd.getPartCode(),
                part.getVehicleNodeCode(), cmd.getRemark(),
                operator);

        softwareBaselineRepository.deleteItemByBaselineAndPart(baseline.getCode(), cmd.getPartCode());

        baseline.bindItem(item);
        softwareBaselineRepository.saveItem(item);
        baseline = softwareBaselineRepository.save(baseline, "UPDATE");
        outboxService.publishSoftwareBaselineUpdatedEvent(baseline);

        return toDto(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public SoftwareBaselineDto unbindItem(String baselineCode, String partCode, String operator) {
        log.info("解绑基线项: baselineCode={}, partCode={}", baselineCode, partCode);

        operator = resolveOperator(operator);
        final String op = operator;
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(baselineCode)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(baselineCode));

        baseline.unbindItem(partCode, op);

        softwareBaselineRepository.deleteItemByBaselineAndPart(baselineCode, partCode);

        baseline = softwareBaselineRepository.save(baseline, "UPDATE");
        outboxService.publishSoftwareBaselineUpdatedEvent(baseline);

        return toDto(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public SoftwareBaselineDto releaseSoftwareBaseline(String code, String releasedBy) {
        log.info("发布软件基线: {}", code);

        releasedBy = resolveOperator(releasedBy);
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(code));

        if (baseline.getActiveItems().isEmpty()) {
            throw new IllegalStateException("基线清单为空，不允许发布: " + code);
        }

        baseline.release(releasedBy);
        baseline = softwareBaselineRepository.save(baseline, "RELEASE");
        outboxService.publishSoftwareBaselineReleasedEvent(baseline);

        return toDto(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public void supersedeSoftwareBaseline(String code, String supersededByCode, String operator) {
        log.info("取代软件基线: {} -> {}", code, supersededByCode);

        operator = resolveOperator(operator);
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(code));

        baseline.supersede(supersededByCode, operator);
        baseline = softwareBaselineRepository.save(baseline, "SUPERSEDE");
        outboxService.publishSoftwareBaselineSupersededEvent(baseline);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteSoftwareBaseline(String code, String operator, boolean forceDelete) {
        log.info("删除软件基线: {} forceDelete={}", code, forceDelete);

        operator = resolveOperator(operator);
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(code));

        softwareBaselineDeletionDomainService.checkAndDelete(baseline, operator, forceDelete);
        outboxService.publishSoftwareBaselineDeletedEvent(baseline, forceDelete);
    }

    public SoftwareBaselineDto getSoftwareBaselineByCode(String code) {
        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(code));
        return toDto(baseline);
    }

    public List<SoftwareBaselineDto> listSoftwareBaseline(SoftwareBaselineQuery query) {
        List<SoftwareBaseline> baselines = softwareBaselineRepository.list(
                query.getAnchorType(), query.getAnchorCode(), query.getBaselineStatus(),
                query.getStatus(), query.getPage(), query.getSize());
        return baselines.stream().map(this::toDto).collect(Collectors.toList());
    }

    public long countSoftwareBaseline(SoftwareBaselineQuery query) {
        return softwareBaselineRepository.count(
                query.getAnchorType(), query.getAnchorCode(), query.getBaselineStatus(), query.getStatus());
    }

    public List<SoftwareBaselineDto> snapshot(boolean includeDraft, boolean includeSuperseded, int page, int size) {
        return softwareBaselineRepository.snapshot(includeDraft, includeSuperseded, page, size).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long snapshotCount(boolean includeDraft, boolean includeSuperseded) {
        return softwareBaselineRepository.snapshotCount(includeDraft, includeSuperseded);
    }

    public List<SoftwareBaselineDto> listAllActive() {
        return softwareBaselineRepository.listAllActive().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SoftwareBaselineDto> listByAnchor(AnchorType anchorType, String anchorCode) {
        return softwareBaselineRepository.findActiveByAnchor(anchorType, anchorCode).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SoftwareBaselineDto> listByPartCode(String partCode) {
        return softwareBaselineRepository.listByPartCode(partCode).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SoftwareBaselineHistoryDto> findHistoryByCode(String code) {
        return softwareBaselineRepository.findHistoryByCode(code).stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    /**
     * 单条补发
     *
     * @param code 基线编码
     * @return 补发结果
     */
    public SoftwareBaselineRepublishResult republish(String code) {
        log.info("单条补发软件基线: {}", code);

        SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code)
                .orElseThrow(() -> new SoftwareBaselineNotExistException(code));

        String eventType = softwareBaselineRepublishDomainService.resolveEventTypeByStatus(
                baseline.getBaselineStatus() != null ? baseline.getBaselineStatus().name() : null);

        outboxService.publishSoftwareBaselineRepublishEvent(baseline, null);

        log.info("软件基线补发成功: code={}, eventType={}", code, eventType);
        return SoftwareBaselineRepublishResult.builder()
                .code(code)
                .eventType(eventType)
                .republished(true)
                .build();
    }

    /**
     * 批量补发
     *
     * @param request 批量补发请求
     * @return 批量补发结果
     */
    public SoftwareBaselineRepublishBatchResult republishBatch(SoftwareBaselineRepublishRequest request) {
        log.info("批量补发软件基线: anchorType={}, anchorCode={}, baselineStatus={}, codes={}, all={}",
                request.getAnchorType(), request.getAnchorCode(), request.getBaselineStatus(),
                request.getCodes(), request.getAll());

        List<String> codes = request.getAll() != null && request.getAll() ? null : request.getCodes();

        long hitCount = softwareBaselineRepublishDomainService.countByFilter(
                request.getAnchorType(), request.getAnchorCode(), request.getBaselineStatus(), codes);

        if (hitCount > republishBatchMaxSize) {
            throw new SoftwareBaselineRepublishBatchLimitExceededException(hitCount, republishBatchMaxSize);
        }

        String batchId = UUID.randomUUID().toString();
        long publishedCount = 0;
        long failedCount = 0;

        int totalPages = (int) Math.ceil((double) hitCount / republishBatchCommitSize);
        for (int page = 1; page <= totalPages; page++) {
            List<String> batchCodes = softwareBaselineRepublishDomainService.listCodesByFilter(
                    request.getAnchorType(), request.getAnchorCode(), request.getBaselineStatus(),
                    codes, page, republishBatchCommitSize);

            for (String code : batchCodes) {
                try {
                    SoftwareBaseline baseline = softwareBaselineRepository.findByCode(code).orElse(null);
                    if (baseline != null) {
                        outboxService.publishSoftwareBaselineRepublishEvent(baseline, batchId);
                        publishedCount++;
                    } else {
                        failedCount++;
                        log.warn("批量补发时基线不存在: {}", code);
                    }
                } catch (Exception e) {
                    failedCount++;
                    log.error("批量补发基线失败: code={}", code, e);
                }
            }
        }

        log.info("批量补发完成: batchId={}, hitCount={}, publishedCount={}, failedCount={}",
                batchId, hitCount, publishedCount, failedCount);

        return SoftwareBaselineRepublishBatchResult.builder()
                .hitCount(hitCount)
                .publishedCount(publishedCount)
                .failedCount(failedCount)
                .batchId(batchId)
                .build();
    }

    private SoftwareBaselineDto toDto(SoftwareBaseline baseline) {
        return SoftwareBaselineDto.builder()
                .id(baseline.getId())
                .code(baseline.getCode())
                .name(baseline.getName())
                .anchorType(baseline.getAnchorType() != null ? baseline.getAnchorType().name() : null)
                .anchorCode(baseline.getAnchorCode())
                .baselineVersion(baseline.getBaselineVersion())
                .baselineStatus(baseline.getBaselineStatus() != null ? baseline.getBaselineStatus().name() : null)
                .releasedAt(baseline.getReleasedAt())
                .releasedBy(baseline.getReleasedBy())
                .supersededByCode(baseline.getSupersededByCode())
                .description(baseline.getDescription())
                .sourceSystem(baseline.getSourceSystem())
                .version(baseline.getVersion())
                .effectiveFrom(baseline.getEffectiveFrom())
                .effectiveTo(baseline.getEffectiveTo())
                .status(baseline.getStatus())
                .createBy(baseline.getCreateBy())
                .createTime(baseline.getCreateTime())
                .modifyBy(baseline.getModifyBy())
                .modifyTime(baseline.getModifyTime())
                .items(baseline.getActiveItems().stream().map(this::toItemDto).collect(Collectors.toList()))
                .build();
    }

    private SoftwareBaselineItemDto toItemDto(SoftwareBaselineItem item) {
        return SoftwareBaselineItemDto.builder()
                .id(item.getId())
                .baselineCode(item.getBaselineCode())
                .partCode(item.getPartCode())
                .vehicleNodeCode(item.getVehicleNodeCode())
                .remark(item.getRemark())
                .createBy(item.getCreateBy())
                .createTime(item.getCreateTime())
                .modifyBy(item.getModifyBy())
                .modifyTime(item.getModifyTime())
                .build();
    }

    private SoftwareBaselineHistoryDto toHistoryDto(net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SoftwareBaselineHistory history) {
        return SoftwareBaselineHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .code(history.getCode())
                .name(history.getName())
                .anchorType(history.getAnchorType() != null ? history.getAnchorType().name() : null)
                .anchorCode(history.getAnchorCode())
                .baselineVersion(history.getBaselineVersion())
                .baselineStatus(history.getBaselineStatus() != null ? history.getBaselineStatus().name() : null)
                .releasedAt(history.getReleasedAt())
                .releasedBy(history.getReleasedBy())
                .supersededByCode(history.getSupersededByCode())
                .description(history.getDescription())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus())
                .forceDelete(history.getForceDelete())
                .itemsSnapshot(history.getItemsSnapshot() != null ? history.getItemsSnapshot().stream().map(this::toItemDto).collect(Collectors.toList()) : null)
                .build();
    }

    private String resolveOperator(String operator) {
        if (operator == null || operator.isBlank()) {
            return SecurityUtils.getUsername();
        }
        return operator;
    }
}
