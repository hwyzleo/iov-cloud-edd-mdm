package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SeriesCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.SeriesUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.SeriesQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SeriesDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.SeriesHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Series;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.SeriesHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车系应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeriesAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    /**
     * 创建车系
     *
     * @param cmd 创建命令
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SeriesDto createSeries(SeriesCreateCmd cmd) {
        log.info("创建车系: {}", cmd.getCode());

        // 自动获取当前用户作为创建人
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 调用领域服务创建车系
        Series series = productDomainService.createSeries(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getBrandCode(),
                cmd.getSeriesType(),
                cmd.getLifecycleStatus(),
                cmd.getTargetMarket(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                createBy
        );

        // 发布车系创建事件到Outbox
        outboxService.publishSeriesCreatedEvent(series);

        return convertToDto(series);
    }

    /**
     * 更新车系
     *
     * @param cmd 更新命令
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SeriesDto updateSeries(SeriesUpdateCmd cmd) {
        log.info("更新车系: {}", cmd.getCode());

        // 自动获取当前用户作为修改人
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务更新车系
        Series series = productDomainService.updateSeries(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getSeriesType(),
                cmd.getLifecycleStatus(),
                cmd.getTargetMarket(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                modifyBy
        );

        // 发布车系更新事件到Outbox
        outboxService.publishSeriesUpdatedEvent(series);

        return convertToDto(series);
    }

    /**
     * 失效车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public SeriesDto deactivateSeries(String code, String modifyBy) {
        log.info("失效车系: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务失效车系
        Series series = productDomainService.deactivateSeries(code, modifyBy);

        // 发布车系失效事件到Outbox
        outboxService.publishSeriesDeactivatedEvent(series);

        return convertToDto(series);
    }

    /**
     * 删除车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSeries(String code, String modifyBy) {
        log.info("删除车系: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        productDomainService.deleteSeries(code, modifyBy);
    }

    /**
     * 根据code获取车系
     *
     * @param code 车系code
     * @return 车系DTO
     */
    public SeriesDto getSeriesByCode(String code) {
        Series series = productDomainService.getSeriesByCode(code);
        return convertToDto(series);
    }

    /**
     * 分页查询车系列表
     *
     * @param query 查询条件
     * @return 车系DTO列表
     */
    public List<SeriesDto> listSeries(SeriesQuery query) {
        List<Series> seriesList = productDomainService.listSeries(
                query.getPage(),
                query.getSize(),
                query.getBrandCode(),
                query.isIncludeInactive()
        );
        return seriesList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询车系总数
     *
     * @param brandCode       品牌code（可选）
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countSeries(String brandCode, boolean includeInactive) {
        return productDomainService.countSeries(brandCode, includeInactive);
    }

    /**
     * 查询车系历史版本列表
     *
     * @param code 车系code
     * @return 历史版本DTO列表
     */
    public List<SeriesHistoryDto> listSeriesHistory(String code) {
        List<SeriesHistory> historyList = productDomainService.listSeriesHistory(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     *
     * @param series 车系聚合根
     * @return 车系DTO
     */
    private SeriesDto convertToDto(Series series) {
        return SeriesDto.builder()
                .id(series.getId())
                .code(series.getCode())
                .name(series.getName())
                .nameLocal(series.getNameLocal())
                .brandCode(series.getBrandCode())
                .seriesType(series.getSeriesType() != null ? series.getSeriesType().name() : null)
                .lifecycleStatus(series.getLifecycleStatus() != null ? series.getLifecycleStatus().name() : null)
                .targetMarket(series.getTargetMarket() != null ? series.getTargetMarket().name() : null)
                .sourceSystem(series.getSourceSystem())
                .sourceId(series.getSourceId())
                .sourceVersion(series.getSourceVersion())
                .ingestionChannel(series.getIngestionChannel())
                .ingestionTime(series.getIngestionTime())
                .sourcePayloadHash(series.getSourcePayloadHash())
                .version(series.getVersion())
                .effectiveFrom(series.getEffectiveFrom())
                .effectiveTo(series.getEffectiveTo())
                .status(series.getStatus().name())
                .createBy(series.getCreateBy())
                .createTime(series.getCreateTime())
                .modifyBy(series.getModifyBy())
                .modifyTime(series.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 车系历史版本实体
     * @return 车系历史版本DTO
     */
    private SeriesHistoryDto convertHistoryToDto(SeriesHistory history) {
        return SeriesHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .brandCode(history.getBrandCode())
                .seriesType(history.getSeriesType())
                .lifecycleStatus(history.getLifecycleStatus())
                .targetMarket(history.getTargetMarket())
                .sourceSystem(history.getSourceSystem())
                .sourceId(history.getSourceId())
                .sourceVersion(history.getSourceVersion())
                .ingestionChannel(history.getIngestionChannel())
                .ingestionTime(history.getIngestionTime())
                .sourcePayloadHash(history.getSourcePayloadHash())
                .version(history.getVersion())
                .effectiveFrom(history.getEffectiveFrom())
                .effectiveTo(history.getEffectiveTo())
                .status(history.getStatus())
                .operationType(history.getOperationType())
                .snapshotTime(history.getSnapshotTime())
                .operator(history.getOperator())
                .createBy(history.getCreateBy())
                .createTime(history.getCreateTime())
                .modifyBy(history.getModifyBy())
                .modifyTime(history.getModifyTime())
                .build();
    }
}
