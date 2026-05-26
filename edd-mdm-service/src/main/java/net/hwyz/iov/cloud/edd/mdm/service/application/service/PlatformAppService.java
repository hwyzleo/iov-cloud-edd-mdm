package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlatformCreateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.PlatformUpdateCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.PlatformQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.PlatformHistoryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.PlatformHistory;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import net.hwyz.iov.cloud.framework.security.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台应用服务
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformAppService {

    private final ProductDomainService productDomainService;
    private final OutboxService outboxService;

    /**
     * 创建平台
     *
     * @param cmd 创建命令
     * @return 平台DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatformDto createPlatform(PlatformCreateCmd cmd) {
        log.info("创建平台: {}", cmd.getCode());

        // 自动获取当前用户作为创建人
        String createBy = cmd.getCreateBy();
        if (createBy == null || createBy.isBlank()) {
            createBy = SecurityUtils.getUsername();
        }

        // 调用领域服务创建平台
        Platform platform = productDomainService.createPlatform(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getPlatformType(),
                cmd.getArchitecture(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                createBy
        );

        // 发布平台创建事件到Outbox
        outboxService.publishPlatformCreatedEvent(platform);

        return convertToDto(platform);
    }

    /**
     * 更新平台
     *
     * @param cmd 更新命令
     * @return 平台DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatformDto updatePlatform(PlatformUpdateCmd cmd) {
        log.info("更新平台: {}", cmd.getCode());

        // 自动获取当前用户作为修改人
        String modifyBy = cmd.getModifyBy();
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务更新平台
        Platform platform = productDomainService.updatePlatform(
                cmd.getCode(),
                cmd.getName(),
                cmd.getNameLocal(),
                cmd.getPlatformType(),
                cmd.getArchitecture(),
                cmd.getEffectiveFrom(),
                cmd.getEffectiveTo(),
                modifyBy
        );

        // 发布平台更新事件到Outbox
        outboxService.publishPlatformUpdatedEvent(platform);

        return convertToDto(platform);
    }

    /**
     * 失效平台
     *
     * @param code     平台code
     * @param modifyBy 修改人
     * @return 平台DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatformDto deactivatePlatform(String code, String modifyBy) {
        log.info("失效平台: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        // 调用领域服务失效平台
        Platform platform = productDomainService.deactivatePlatform(code, modifyBy);

        // 发布平台失效事件到Outbox
        outboxService.publishPlatformDeactivatedEvent(platform);

        return convertToDto(platform);
    }

    /**
     * 删除平台
     *
     * @param code     平台code
     * @param modifyBy 修改人
     */
    @Transactional(rollbackFor = Exception.class)
    public void deletePlatform(String code, String modifyBy) {
        log.info("删除平台: {}", code);

        // 自动获取当前用户作为修改人
        if (modifyBy == null || modifyBy.isBlank()) {
            modifyBy = SecurityUtils.getUsername();
        }

        productDomainService.deletePlatform(code, modifyBy);
    }

    /**
     * 根据code获取平台
     *
     * @param code 平台code
     * @return 平台DTO
     */
    public PlatformDto getPlatformByCode(String code) {
        Platform platform = productDomainService.getPlatformByCode(code);
        return convertToDto(platform);
    }

    /**
     * 分页查询平台列表
     *
     * @param query 查询条件
     * @return 平台DTO列表
     */
    public List<PlatformDto> listPlatforms(PlatformQuery query) {
        List<Platform> platforms = productDomainService.listPlatforms(
                query.getPage(),
                query.getSize(),
                query.isIncludeInactive()
        );
        return platforms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 查询平台总数
     *
     * @param includeInactive 是否包含失效记录
     * @return 总数
     */
    public long countPlatforms(boolean includeInactive) {
        return productDomainService.countPlatforms(includeInactive);
    }

    /**
     * 查询平台历史版本列表
     *
     * @param code 平台code
     * @return 历史版本DTO列表
     */
    public List<PlatformHistoryDto> listPlatformHistory(String code) {
        List<PlatformHistory> historyList = productDomainService.listPlatformHistory(code);
        return historyList.stream()
                .map(this::convertHistoryToDto)
                .collect(Collectors.toList());
    }

    /**
     * 转换为DTO
     *
     * @param platform 平台聚合根
     * @return 平台DTO
     */
    private PlatformDto convertToDto(Platform platform) {
        return PlatformDto.builder()
                .id(platform.getId())
                .code(platform.getCode())
                .name(platform.getName())
                .nameLocal(platform.getNameLocal())
                .platformType(platform.getPlatformType() != null ? platform.getPlatformType().name() : null)
                .architecture(platform.getArchitecture())
                .sourceSystem(platform.getSourceSystem())
                .sourceId(platform.getSourceId())
                .sourceVersion(platform.getSourceVersion())
                .ingestionChannel(platform.getIngestionChannel())
                .ingestionTime(platform.getIngestionTime())
                .sourcePayloadHash(platform.getSourcePayloadHash())
                .version(platform.getVersion())
                .effectiveFrom(platform.getEffectiveFrom())
                .effectiveTo(platform.getEffectiveTo())
                .status(platform.getStatus().name())
                .createBy(platform.getCreateBy())
                .createTime(platform.getCreateTime())
                .modifyBy(platform.getModifyBy())
                .modifyTime(platform.getModifyTime())
                .build();
    }

    /**
     * 转换历史版本为DTO
     *
     * @param history 平台历史版本实体
     * @return 平台历史版本DTO
     */
    private PlatformHistoryDto convertHistoryToDto(PlatformHistory history) {
        return PlatformHistoryDto.builder()
                .snapshotId(history.getSnapshotId())
                .entityId(history.getEntityId())
                .code(history.getCode())
                .name(history.getName())
                .nameLocal(history.getNameLocal())
                .platformType(history.getPlatformType())
                .architecture(history.getArchitecture())
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
