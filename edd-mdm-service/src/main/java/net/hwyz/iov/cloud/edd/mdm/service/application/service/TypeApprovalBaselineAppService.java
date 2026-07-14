package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.TaBaselineProjectRequest;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TaBaselineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TaBaselineItemDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.TypeApprovalBaselineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineDuplicateException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.TaBaselineNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.TypeApprovalBaseline;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.TaBaselineItem;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.AnchorType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.TypeApprovalBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.TaBaselineCodeGenerator;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.TaBaselineProjectionDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.TypeApprovalBaselineDeletionDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 型式批准基线应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TypeApprovalBaselineAppService {

    private final TypeApprovalBaselineRepository typeApprovalBaselineRepository;
    private final TaBaselineProjectionDomainService taBaselineProjectionDomainService;
    private final TypeApprovalBaselineDeletionDomainService typeApprovalBaselineDeletionDomainService;
    private final TaBaselineCodeGenerator taBaselineCodeGenerator;
    private final OutboxService outboxService;

    /**
     * 执行卷积投影生成/刷新TA基线
     *
     * @param request 卷积投影请求
     * @return TA基线DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public TypeApprovalBaselineDto project(TaBaselineProjectRequest request) {
        log.info("执行TA基线卷积投影: swinCode={}", request.getSwinCode());

        String operator = resolveOperator(null);

        // 1. 执行卷积投影
        TaBaselineProjectionDomainService.ProjectionResult projectionResult =
                taBaselineProjectionDomainService.project(request.getSwinCode());

        // 2. 幂等检查：同锚点 + 同投影摘要
        TypeApprovalBaseline existingBaseline = typeApprovalBaselineRepository.findByAnchorAndDigest(
                projectionResult.getSwinCode(),
                projectionResult.getAnchorType().name(),
                projectionResult.getAnchorCode(),
                projectionResult.getDigest()
        ).orElse(null);

        if (existingBaseline != null) {
            log.info("TA基线已存在，幂等返回: code={}", existingBaseline.getTaBaselineCode());
            return toDto(existingBaseline);
        }

        // 3. 生成TA基线编码
        String taBaselineCode = taBaselineCodeGenerator.nextValue();

        // 4. 创建TA基线
        String sourceBaselineScope = projectionResult.getSourceBaselineCodes() != null ?
                String.join(",", projectionResult.getSourceBaselineCodes()) : null;

        TypeApprovalBaseline baseline = TypeApprovalBaseline.create(
                taBaselineCode,
                projectionResult.getSwinCode(),
                projectionResult.getAnchorType(),
                projectionResult.getAnchorCode(),
                projectionResult.getDigest(),
                sourceBaselineScope,
                operator
        );

        // 5. 保存基线
        baseline = typeApprovalBaselineRepository.save(baseline, "CREATE");

        // 6. 保存基线项
        for (TaBaselineProjectionDomainService.ProjectionItem projectionItem : projectionResult.getItems()) {
            TaBaselineItem item = TaBaselineItem.create(
                    baseline.getId(),
                    projectionItem.getVehicleNodeCode(),
                    projectionItem.getPartCode(),
                    projectionItem.getApprovedVersion(),
                    projectionResult.getSourceBaselineCodes() != null && !projectionResult.getSourceBaselineCodes().isEmpty() ?
                            projectionResult.getSourceBaselineCodes().get(0) : null,
                    operator
            );
            typeApprovalBaselineRepository.saveItem(item);
        }

        // 7. 重新加载基线（包含项）
        baseline = typeApprovalBaselineRepository.findByCode(taBaselineCode)
                .orElseThrow(() -> new TaBaselineNotExistException(taBaselineCode));

        // 8. 发布事件
        outboxService.publishTypeApprovalBaselineCreatedEvent(baseline);

        log.info("TA基线创建成功: code={}", taBaselineCode);
        return toDto(baseline);
    }

    /**
     * 发布TA基线（DRAFT -> RELEASED）
     *
     * @param code TA基线编码
     * @return TA基线DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public TypeApprovalBaselineDto release(String code) {
        log.info("发布TA基线: {}", code);

        String operator = resolveOperator(null);
        TypeApprovalBaseline baseline = typeApprovalBaselineRepository.findByCode(code)
                .orElseThrow(() -> new TaBaselineNotExistException(code));

        baseline.release(operator);
        baseline = typeApprovalBaselineRepository.save(baseline, "RELEASE");
        outboxService.publishTypeApprovalBaselineReleasedEvent(baseline);

        log.info("TA基线发布成功: {}", code);
        return toDto(baseline);
    }

    /**
     * 冻结TA基线（RELEASED -> FROZEN）
     *
     * @param code TA基线编码
     * @return TA基线DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public TypeApprovalBaselineDto freeze(String code) {
        log.info("冻结TA基线: {}", code);

        String operator = resolveOperator(null);
        TypeApprovalBaseline baseline = typeApprovalBaselineRepository.findByCode(code)
                .orElseThrow(() -> new TaBaselineNotExistException(code));

        baseline.freeze(operator);
        baseline = typeApprovalBaselineRepository.save(baseline, "FREEZE");
        outboxService.publishTypeApprovalBaselineFrozenEvent(baseline);

        log.info("TA基线冻结成功: {}", code);
        return toDto(baseline);
    }

    /**
     * 删除TA基线
     *
     * @param code        TA基线编码
     * @param forceDelete 是否强制删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code, boolean forceDelete) {
        log.info("删除TA基线: code={}, forceDelete={}", code, forceDelete);

        String operator = resolveOperator(null);
        TypeApprovalBaseline baseline = typeApprovalBaselineRepository.findByCode(code)
                .orElseThrow(() -> new TaBaselineNotExistException(code));

        typeApprovalBaselineDeletionDomainService.checkAndDelete(baseline, operator, forceDelete);
        outboxService.publishTypeApprovalBaselineDeletedEvent(baseline, forceDelete);

        log.info("TA基线删除成功: {}", code);
    }

    /**
     * 根据编码查询TA基线
     *
     * @param code TA基线编码
     * @return TA基线DTO
     */
    public TypeApprovalBaselineDto getByCode(String code) {
        TypeApprovalBaseline baseline = typeApprovalBaselineRepository.findByCode(code)
                .orElseThrow(() -> new TaBaselineNotExistException(code));
        return toDto(baseline);
    }

    /**
     * 根据SWIN代码查询TA基线列表
     *
     * @param swinCode SWIN代码
     * @return TA基线DTO列表
     */
    public List<TypeApprovalBaselineDto> listBySwinCode(String swinCode) {
        return typeApprovalBaselineRepository.listBySwinCode(swinCode).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 分页查询TA基线列表
     *
     * @param swinCode   SWIN代码（可选）
     * @param anchorType 锚定层级类型（可选）
     * @param anchorCode 锚点编码（可选）
     * @param status     状态（可选）
     * @param code       业务键（可选）
     * @param page       页码
     * @param size       每页大小
     * @return TA基线DTO列表
     */
    public List<TypeApprovalBaselineDto> list(String swinCode, String anchorType, String anchorCode,
                                                String status, String code, int page, int size) {
        return typeApprovalBaselineRepository.list(swinCode, anchorType, anchorCode, status, code, page, size).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 统计TA基线数量
     *
     * @param swinCode   SWIN代码（可选）
     * @param anchorType 锚定层级类型（可选）
     * @param anchorCode 锚点编码（可选）
     * @param status     状态（可选）
     * @param code       业务键（可选）
     * @return 基线数量
     */
    public long count(String swinCode, String anchorType, String anchorCode, String status, String code) {
        return typeApprovalBaselineRepository.count(swinCode, anchorType, anchorCode, status, code);
    }

    /**
     * 查询TA基线历史
     *
     * @param code TA基线编码
     * @return 历史记录列表
     */
    public List<TaBaselineHistoryDto> findHistoryByCode(String code) {
        return typeApprovalBaselineRepository.findHistoryByCode(code).stream()
                .map(this::toHistoryDto)
                .collect(Collectors.toList());
    }

    private TypeApprovalBaselineDto toDto(TypeApprovalBaseline baseline) {
        return TypeApprovalBaselineDto.builder()
                .id(baseline.getId())
                .taBaselineCode(baseline.getTaBaselineCode())
                .swinCode(baseline.getSwinCode())
                .anchorType(baseline.getAnchorType() != null ? baseline.getAnchorType().name() : null)
                .anchorCode(baseline.getAnchorCode())
                .status(baseline.getStatus() != null ? baseline.getStatus().name() : null)
                .projectionDigest(baseline.getProjectionDigest())
                .sourceBaselineScope(baseline.getSourceBaselineScope())
                .effectiveFrom(baseline.getEffectiveFrom())
                .remark(baseline.getRemark())
                .version(baseline.getVersion())
                .createBy(baseline.getCreateBy())
                .createTime(baseline.getCreateTime())
                .modifyBy(baseline.getModifyBy())
                .modifyTime(baseline.getModifyTime())
                .items(baseline.getActiveItems().stream().map(this::toItemDto).collect(Collectors.toList()))
                .build();
    }

    private TaBaselineItemDto toItemDto(TaBaselineItem item) {
        return TaBaselineItemDto.builder()
                .id(item.getId())
                .taBaselineId(item.getTaBaselineId())
                .vehicleNodeCode(item.getVehicleNodeCode())
                .partCode(item.getPartCode())
                .approvedVersion(item.getApprovedVersion())
                .sourceBaselineCode(item.getSourceBaselineCode())
                .createBy(item.getCreateBy())
                .createTime(item.getCreateTime())
                .build();
    }

    private TaBaselineHistoryDto toHistoryDto(TaBaselineHistory history) {
        return TaBaselineHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .taBaselineCode(history.getTaBaselineCode())
                .swinCode(history.getSwinCode())
                .anchorType(history.getAnchorType() != null ? history.getAnchorType().name() : null)
                .anchorCode(history.getAnchorCode())
                .status(history.getStatus() != null ? history.getStatus().name() : null)
                .projectionDigest(history.getProjectionDigest())
                .sourceBaselineScope(history.getSourceBaselineScope())
                .effectiveFrom(history.getEffectiveFrom())
                .remark(history.getRemark())
                .version(history.getVersion())
                .createBy(history.getCreateBy())
                .createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy())
                .modifyTime(history.getModifyTime())
                .itemsSnapshot(history.getItemsSnapshot() != null ?
                        history.getItemsSnapshot().stream().map(this::toItemDto).collect(Collectors.toList()) : null)
                .build();
    }

    private String resolveOperator(String operator) {
        if (operator == null || operator.isBlank()) {
            return SecurityUtils.getUsername();
        }
        return operator;
    }
}
