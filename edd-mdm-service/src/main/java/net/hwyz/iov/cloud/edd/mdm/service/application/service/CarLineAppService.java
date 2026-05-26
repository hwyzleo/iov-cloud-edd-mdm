package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.CarLineCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.CarLineUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.CarLineQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.CarLineHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.CarLineHistory;
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
public class CarLineAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    /**
     * 创建车系
     *
     * @param cmd 创建命令
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CarLineDto createCarLine(CarLineCreateCmd cmd) {
        log.info("创建车系: {}", cmd.getCode());

        // 自动获取当前用户作为创建人
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 调用领域服务创建车系
        CarLine carLine = productDomainService.createCarLine(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getBrandCode(),
                cmd.getCarLineType(),
                cmd.getLifecycleStatus(),
                cmd.getTargetMarket(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                createBy
        );

        // 发布车系创建事件到Outbox
        outboxService.publishCarLineCreatedEvent(carLine);

        return convertToDto(carLine);
    }

    /**
     * 更新车系
     *
     * @param cmd 更新命令
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CarLineDto updateCarLine(CarLineUpdateCmd cmd) {
        log.info("更新车系: {}", cmd.getCode());

        // 自动获取当前用户作为修改人
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务更新车系
        CarLine carLine = productDomainService.updateCarLine(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getCarLineType(),
                cmd.getLifecycleStatus(),
                cmd.getTargetMarket(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                modifyBy
        );

        // 发布车系更新事件到Outbox
        outboxService.publishCarLineUpdatedEvent(carLine);

        return convertToDto(carLine);
    }

    /**
     * 失效车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     * @return 车系DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public CarLineDto deactivateCarLine(String code, String modifyBy) {
        log.info("失效车系: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务失效车系
        CarLine carLine = productDomainService.deactivateCarLine(code, modifyBy);

        // 发布车系失效事件到Outbox
        outboxService.publishCarLineDeactivatedEvent(carLine);

        return convertToDto(carLine);
    }

    /**
     * 删除车系
     *
     * @param code     车系code
     * @param modifyBy 修改人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCarLine(String code, String modifyBy) {
        log.info("删除车系: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        productDomainService.deleteCarLine(code, modifyBy);
    }

    /**
     * 根据code获取车系
     *
     * @param code 车系code
     * @return 车系DTO
     */
    public CarLineDto getCarLineByCode(String code) {
        CarLine carLine = productDomainService.getCarLineByCode(code);
        return convertToDto(carLine);
    }

    /**
     * 分页查询车系列表
     *
     * @param query 查询条件
     * @return 车系DTO列表
     */
    public List<CarLineDto> listCarLine(CarLineQuery query) {
        List<CarLine> carLineList = productDomainService.listCarLine(
                query.getPage(),
                query.getSize(),
                query.getBrandCode(),
                query.isIncludeInactive()
        );
        return carLineList.stream()
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
    public long countCarLine(String brandCode, boolean includeInactive) {
        return productDomainService.countCarLine(brandCode, includeInactive);
    }

    /**
     * 查询车系历史版本列表
     *
     * @param code 车系code
     * @return 历史版本DTO列表
     */
    public List<CarLineHistoryDto> listCarLineHistory(String code) {
        List<CarLineHistory> historyList = productDomainService.listCarLineHistory(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     *
     * @param carLine 车系聚合根
     * @return 车系DTO
     */
    private CarLineDto convertToDto(CarLine carLine) {
        return CarLineDto.builder()
                .id(carLine.getId())
                .code(carLine.getCode())
                .name(carLine.getName())
                .nameLocal(carLine.getNameLocal())
                .brandCode(carLine.getBrandCode())
                .carLineType(carLine.getCarLineType() != null ? carLine.getCarLineType().name() : null)
                .lifecycleStatus(carLine.getLifecycleStatus() != null ? carLine.getLifecycleStatus().name() : null)
                .targetMarket(carLine.getTargetMarket() != null ? carLine.getTargetMarket().name() : null)
                .sourceSystem(carLine.getSourceSystem())
                .sourceId(carLine.getSourceId())
                .sourceVersion(carLine.getSourceVersion())
                .ingestionChannel(carLine.getIngestionChannel())
                .ingestionTime(carLine.getIngestionTime())
                .sourcePayloadHash(carLine.getSourcePayloadHash())
                .version(carLine.getVersion())
                .effectiveFrom(carLine.getEffectiveFrom())
                .effectiveTo(carLine.getEffectiveTo())
                .status(carLine.getStatus().name())
                .createBy(carLine.getCreateBy())
                .createTime(carLine.getCreateTime())
                .modifyBy(carLine.getModifyBy())
                .modifyTime(carLine.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 车系历史版本实体
     * @return 车系历史版本DTO
     */
    private CarLineHistoryDto convertHistoryToDto(CarLineHistory history) {
        return CarLineHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .brandCode(history.getBrandCode())
                .carLineType(history.getCarLineType())
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
