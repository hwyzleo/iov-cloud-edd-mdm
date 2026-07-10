package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.RxswinRegisterCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.RxswinRegistryQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.RxswinRegistryDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.RxswinBaselineNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.RxswinManifestConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.RxswinRegistryNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.RxswinSwinNotActiveException;
import net.hwyz.iov.cloud.edd.mdm.service.common.exception.SwinDefinitionNotExistException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.RxswinRegistry;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.SwinDefinition;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.SwinDefinitionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.RxswinRegistryRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SoftwareBaselineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.SwinDefinitionRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.RxswinValueGenerator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RXSWIN登记应用服务（EEAD 子域）
 *
 * @author hwyz_leo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RxswinRegistryAppService {

    private final RxswinRegistryRepository rxswinRegistryRepository;
    private final SwinDefinitionRepository swinDefinitionRepository;
    private final SoftwareBaselineRepository softwareBaselineRepository;
    private final RxswinValueGenerator rxswinValueGenerator;
    private final OutboxService outboxService;

    /**
     * RXSWIN幂等登记
     *
     * @param cmd 登记命令
     * @return 登记DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public RxswinRegistryDto register(RxswinRegisterCmd cmd) {
        log.info("RXSWIN登记: manifestCode={}, swinCode={}", cmd.getManifestCode(), cmd.getSwinCode());

        String requestSource = cmd.getRequestSource();
        if (requestSource == null || requestSource.isBlank()) {
            requestSource = "system";
        }
        final String finalRequestSource = requestSource;

        return rxswinRegistryRepository.findByManifestCode(cmd.getManifestCode())
                .map(existing -> handleExistingRegistry(existing, cmd))
                .orElseGet(() -> handleNewRegistration(cmd, finalRequestSource));
    }

    /**
     * 根据manifestCode查询登记结果
     *
     * @param manifestCode manifest业务键
     * @return 登记DTO
     */
    public RxswinRegistryDto getByManifestCode(String manifestCode) {
        RxswinRegistry registry = rxswinRegistryRepository.findByManifestCode(manifestCode)
                .orElseThrow(() -> new RxswinRegistryNotExistException(manifestCode));
        return toDto(registry, false);
    }

    /**
     * 分页查询RXSWIN登记记录
     *
     * @param query 查询对象
     * @return 登记DTO列表
     */
    public List<RxswinRegistryDto> listRegistries(RxswinRegistryQuery query) {
        return rxswinRegistryRepository.findPaginated(
                        query.getPage(), query.getSize(),
                        query.getManifestCode(), query.getRxswinValue(),
                        query.getSwinCode(), query.getSoftwareBaselineCode(),
                        query.getRegisteredAtStart(), query.getRegisteredAtEnd())
                .stream()
                .map(r -> toDto(r, false))
                .collect(Collectors.toList());
    }

    /**
     * 统计符合条件的记录总数
     *
     * @param query 查询对象
     * @return 总数
     */
    public long countRegistries(RxswinRegistryQuery query) {
        return rxswinRegistryRepository.count(
                query.getManifestCode(), query.getRxswinValue(),
                query.getSwinCode(), query.getSoftwareBaselineCode(),
                query.getRegisteredAtStart(), query.getRegisteredAtEnd());
    }

    private RxswinRegistryDto handleExistingRegistry(RxswinRegistry existing, RxswinRegisterCmd cmd) {
        boolean swinMatch = Objects.equals(existing.getSwinCode(), cmd.getSwinCode());
        boolean digestMatch = Objects.equals(existing.getManifestDigest(), cmd.getManifestDigest());
        boolean baselineMatch = Objects.equals(
                existing.getSoftwareBaselineCode(), cmd.getSoftwareBaselineCode());

        if (swinMatch && digestMatch && baselineMatch) {
            log.info("RXSWIN幂等命中: manifestCode={}, rxswinValue={}", cmd.getManifestCode(), existing.getRxswinValue());
            return toDto(existing, true);
        }

        throw new RxswinManifestConflictException(cmd.getManifestCode(),
                existing.getSwinCode(), existing.getManifestDigest(),
                cmd.getSwinCode(), cmd.getManifestDigest());
    }

    private RxswinRegistryDto handleNewRegistration(RxswinRegisterCmd cmd, String requestSource) {
        validateSwinActive(cmd.getSwinCode());
        validateBaselineIfExists(cmd.getSoftwareBaselineCode());

        String rxswinValue = rxswinValueGenerator.nextValue();

        RxswinRegistry registry = RxswinRegistry.create(
                cmd.getManifestCode(), cmd.getManifestDigest(), cmd.getSwinCode(),
                rxswinValue, cmd.getSoftwareBaselineCode(), cmd.getApprovedAt(),
                requestSource, cmd.getTraceId());

        try {
            rxswinRegistryRepository.save(registry);
        } catch (DuplicateKeyException e) {
            log.warn("并发插入唯一键冲突，重新读取: manifestCode={}", cmd.getManifestCode());
            RxswinRegistry existing = rxswinRegistryRepository.findByManifestCode(cmd.getManifestCode())
                    .orElseThrow(() -> new IllegalStateException("并发唯一键冲突后重新读取仍不存在: " + cmd.getManifestCode()));
            return handleExistingRegistry(existing, cmd);
        }

        outboxService.publishRxswinRegistryCreatedEvent(registry);
        return toDto(registry, false);
    }

    private void validateSwinActive(String swinCode) {
        SwinDefinition swinDefinition = swinDefinitionRepository.findBySwinCode(swinCode)
                .orElseThrow(() -> new SwinDefinitionNotExistException(swinCode));
        if (swinDefinition.getStatus() != SwinDefinitionStatus.ACTIVE) {
            throw new RxswinSwinNotActiveException(swinCode, swinDefinition.getStatus().name());
        }
    }

    private void validateBaselineIfExists(String softwareBaselineCode) {
        if (softwareBaselineCode != null && !softwareBaselineCode.isBlank()) {
            if (!softwareBaselineRepository.existsByCode(softwareBaselineCode)) {
                throw new RxswinBaselineNotExistException(softwareBaselineCode);
            }
        }
    }

    private RxswinRegistryDto toDto(RxswinRegistry registry, boolean idempotentHit) {
        return RxswinRegistryDto.builder()
                .id(registry.getId())
                .manifestCode(registry.getManifestCode())
                .manifestDigest(registry.getManifestDigest())
                .swinCode(registry.getSwinCode())
                .rxswinValue(registry.getRxswinValue())
                .softwareBaselineCode(registry.getSoftwareBaselineCode())
                .status(registry.getStatus())
                .approvedAt(registry.getApprovedAt())
                .registeredAt(registry.getRegisteredAt())
                .requestSource(registry.getRequestSource())
                .traceId(registry.getTraceId())
                .version(registry.getVersion())
                .idempotentHit(idempotentHit)
                .build();
    }
}
