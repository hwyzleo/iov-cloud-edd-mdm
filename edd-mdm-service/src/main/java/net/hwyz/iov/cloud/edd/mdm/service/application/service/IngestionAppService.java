package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.IngestCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.query.IngestionLogQuery;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.IngestionLogDto;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.result.IngestionResult;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.IngestionAuthService;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.common.enums.IngestionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.IngestionSchemaException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Brand;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Model;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Platform;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.CarLine;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Variant;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.IngestionLog;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.IngestionLogRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ModelRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.CarLineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.AuthoritativeSourceService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.IngestionDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionAppService {

    private final IngestionAuthService ingestionAuthService;
    private final AuthoritativeSourceService authoritativeSourceService;
    private final IngestionDomainService ingestionDomainService;
    private final ProductDomainService productDomainService;
    private final BrandRepository brandRepository;
    private final CarLineRepository carLineRepository;
    private final PlatformRepository platformRepository;
    private final ModelRepository modelRepository;
    private final VariantRepository variantRepository;
    private final ConfigurationRepository configurationRepository;
    private final OutboxService outboxService;
    private final IngestionLogRepository ingestionLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional(rollbackFor = Exception.class)
    public IngestionResult ingest(IngestCmd cmd, String authHeader) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String entityType = cmd.getEntityType();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();

        // 1. Schema校验
        String payloadJson = objectMapper.valueToTree(cmd.getPayload()).toString();
        ingestionDomainService.validateSchema(sourceSystem, sourceId, sourceVersion, entityType, payloadJson);

        // 2. 来源鉴权
        ingestionAuthService.authenticate(sourceSystem, authHeader);

        // 3. 权威源校验
        EntityType et = EntityType.valueOf(entityType);
        // CR-005：CONFIGURATION 类型允许 payload.code 为空（由系统按规则生成或冲突兜底生成）
        String code = extractCodeFromPayload(cmd.getPayload(), et);
        authoritativeSourceService.validateAuthoritativeSource(et, code, sourceSystem);

        // 4. 计算payload哈希
        String payloadHash = ingestionDomainService.computePayloadHash(payloadJson);

        // 5. 根据实体类型处理
        return switch (et) {
            case BRAND -> processBrand(cmd, code, payloadHash);
            case SERIES -> processCarLine(cmd, code, payloadHash);
            case PLATFORM -> processPlatform(cmd, code, payloadHash);
            case MODEL -> processModel(cmd, code, payloadHash);
            case VARIANT -> processVariant(cmd, code, payloadHash);
            case CONFIGURATION -> processConfiguration(cmd, code, payloadHash);
        };
    }

    private IngestionResult processBrand(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        Brand existing = brandRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.BRAND, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.BRAND, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String description = (String) payload.get("description");
        String logo = (String) payload.get("logo");
        String country = (String) payload.get("country");
        Integer foundedYear = payload.get("foundedYear") != null ? ((Number) payload.get("foundedYear")).intValue() : null;

        Brand brand;
        String operationType;
        if (existing == null) {
            brand = Brand.createFromUpstream(code, name, nameLocal, description, logo, country,
                    foundedYear, cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            brand = brandRepository.save(brand);
            outboxService.publishBrandCreatedEvent(brand);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, description, logo, country,
                    foundedYear, cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            brand = brandRepository.save(existing);
            outboxService.publishBrandUpdatedEvent(brand);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.BRAND, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(brand.getId()).version(brand.getVersion()).operationType(operationType).build();
    }

    private IngestionResult processCarLine(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        CarLine existing = carLineRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.SERIES, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.SERIES, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String brandCode = (String) payload.get("brandCode");

        CarLine carLine;
        String operationType;
        if (existing == null) {
            carLine = CarLine.createFromUpstream(code, name, nameLocal, brandCode,
                    null, null, null, cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            carLine = carLineRepository.save(carLine);
            outboxService.publishCarLineCreatedEvent(carLine);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, null, null, null,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            carLine = carLineRepository.save(existing);
            outboxService.publishCarLineUpdatedEvent(carLine);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.SERIES, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(carLine.getId()).version(carLine.getVersion()).operationType(operationType).build();
    }

    private IngestionResult processPlatform(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        Platform existing = platformRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.PLATFORM, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.PLATFORM, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");

        Platform platform;
        String operationType;
        if (existing == null) {
            platform = Platform.createFromUpstream(code, name, nameLocal,
                    null, null, cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            platform = platformRepository.save(platform);
            outboxService.publishPlatformCreatedEvent(platform);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, null, null,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            platform = platformRepository.save(existing);
            outboxService.publishPlatformUpdatedEvent(platform);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.PLATFORM, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(platform.getId()).version(platform.getVersion()).operationType(operationType).build();
    }

    public List<IngestionLogDto> listIngestionLogs(IngestionLogQuery query) {
        List<IngestionLog> logs = ingestionLogRepository.findAll(query.getPage(), query.getSize(),
                query.getSourceSystem(), query.getEntityType(), query.getStatus(),
                query.getStartTime(), query.getEndTime());
        return logs.stream().map(this::toLogDto).collect(Collectors.toList());
    }

    public long countIngestionLogs(IngestionLogQuery query) {
        return ingestionLogRepository.count(query.getSourceSystem(), query.getEntityType(),
                query.getStatus(), query.getStartTime(), query.getEndTime());
    }

    public IngestionLogDto getIngestionLogByMessageId(String messageId) {
        return ingestionLogRepository.findByMessageId(messageId).map(this::toLogDto).orElse(null);
    }

    private IngestionLogDto toLogDto(IngestionLog log) {
        return IngestionLogDto.builder()
                .id(log.getId()).messageId(log.getMessageId())
                .sourceSystem(log.getSourceSystem()).sourceId(log.getSourceId())
                .sourceVersion(log.getSourceVersion())
                .entityType(log.getEntityType() != null ? log.getEntityType().name() : null)
                .entityCode(log.getEntityCode()).ingestionChannel(log.getIngestionChannel())
                .receivedAt(log.getReceivedAt()).processedAt(log.getProcessedAt())
                .status(log.getStatus() != null ? log.getStatus().name() : null)
                .errorCode(log.getErrorCode()).errorMessage(log.getErrorMessage())
                .payloadHash(log.getPayloadHash())
                .build();
    }

    private String extractCodeFromPayload(Map<String, Object> payload, EntityType entityType) {
        Object code = payload.get("code");
        if (code == null) {
            // CR-005：CONFIGURATION 类型允许 payload 不携带 code（由系统按规则生成）
            if (entityType == EntityType.CONFIGURATION) {
                return null;
            }
            throw new IngestionSchemaException("payload中缺少code字段");
        }
        return code.toString();
    }

    private IngestionResult processModel(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        Model existing = modelRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.MODEL, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.MODEL, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String carLineCode = (String) payload.get("carLineCode");
        String platformCode = (String) payload.get("platformCode");
        String modelYear = (String) payload.get("modelYear");
        String description = (String) payload.get("description");

        Model model;
        String operationType;
        if (existing == null) {
            model = Model.createFromUpstream(code, name, nameLocal, carLineCode, platformCode,
                    modelYear, description, cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            model = modelRepository.save(model);
            outboxService.publishModelCreatedEvent(model);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, modelYear, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            model = modelRepository.save(existing);
            outboxService.publishModelUpdatedEvent(model);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.MODEL, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(model.getId()).version(model.getVersion()).operationType(operationType).build();
    }

    private IngestionResult processVariant(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        Variant existing = variantRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.VARIANT, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.VARIANT, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String modelCode = (String) payload.get("modelCode");
        String description = (String) payload.get("description");

        Variant variant;
        String operationType;
        if (existing == null) {
            variant = Variant.createFromUpstream(code, name, nameLocal, modelCode, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            variant = variantRepository.save(variant);
            outboxService.publishVariantCreatedEvent(variant);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            variant = variantRepository.save(existing);
            outboxService.publishVariantUpdatedEvent(variant);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.VARIANT, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(variant.getId()).version(variant.getVersion()).operationType(operationType).build();
    }

    private IngestionResult processConfiguration(IngestCmd cmd, String upstreamCode, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        // ===== CR-005 第 1 层：用 (sourceSystem, sourceId) 定位本地记录（幂等更新锚） =====
        Configuration existing = configurationRepository
                .findBySourceSystemAndSourceId(sourceSystem, sourceId).orElse(null);

        // 命中本地记录 → 走幂等校验 + 更新（保持本地 code 不变）
        if (existing != null) {
            String localVersion = existing.getSourceVersion();
            String localHash = existing.getSourcePayloadHash();
            IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                    sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

            if (idempotentResult == IngestionStatus.DUPLICATED) {
                ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                        EntityType.CONFIGURATION, existing.getCode(), ingestionChannel,
                        IngestionStatus.DUPLICATED, null, null, payloadHash);
                return IngestionResult.builder().entityId(existing.getId())
                        .version(existing.getVersion()).operationType("DUPLICATED").build();
            }
            if (idempotentResult == IngestionStatus.OUTDATED) {
                ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                        EntityType.CONFIGURATION, existing.getCode(), ingestionChannel,
                        IngestionStatus.OUTDATED, null, null, payloadHash);
                return IngestionResult.builder().entityId(existing.getId())
                        .version(existing.getVersion()).operationType("OUTDATED").build();
            }

            // 上游 code 漂移：payload 携带的 code 与本地已有 code 不一致 → 仅记录告警，不改 code
            if (upstreamCode != null && !upstreamCode.equals(existing.getCode())) {
                log.warn("上游 Configuration code 漂移：sourceSystem={}, sourceId={}, localCode={}, upstreamCode={}（保持本地 code 不变）",
                        sourceSystem, sourceId, existing.getCode(), upstreamCode);
            }

            String name = (String) payload.get("name");
            String nameLocal = (String) payload.get("nameLocal");
            String description = (String) payload.get("description");
            existing.updateFromUpstream(name, nameLocal, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            Configuration saved = configurationRepository.save(existing);
            outboxService.publishConfigurationUpdatedEvent(saved);

            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.CONFIGURATION, saved.getCode(), ingestionChannel,
                    IngestionStatus.SUCCESS, null, null, payloadHash);
            return IngestionResult.builder().entityId(saved.getId())
                    .version(saved.getVersion()).operationType("UPDATED").build();
        }

        // ===== CR-005 第 2 层：未命中（视为新增），决策 code =====
        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String variantCode = (String) payload.get("variantCode");
        String description = (String) payload.get("description");

        String finalCode;
        boolean codeOverride = false;
        if (upstreamCode == null || upstreamCode.length() > 64) {
            // 上游未带 code 或 code 超长 → 系统按规则生成
            if (upstreamCode != null && upstreamCode.length() > 64) {
                log.warn("上游 Configuration code 长度超限（>64）: sourceSystem={}, sourceId={}, upstreamCode={}（按系统规则生成）",
                        sourceSystem, sourceId, upstreamCode);
            }
            finalCode = productDomainService.generateConfigurationCode(variantCode);
        } else if (configurationRepository.existsByCode(upstreamCode)) {
            // 上游 code 在 MDM 全局已被占用 → 视为命名空间冲突，本地按规则生成新 code 兜底，告警 + 监控
            finalCode = productDomainService.generateConfigurationCode(variantCode);
            codeOverride = true;
            log.warn("Configuration code 命名空间冲突，本地兜底生成: sourceSystem={}, sourceId={}, upstreamCode={}, finalCode={}",
                    sourceSystem, sourceId, upstreamCode, finalCode);
            // 监控指标 mdm.configuration.code.upstream_conflict（待 Prometheus 接入后绑定 Counter）
        } else {
            // 上游 code 未被占用 → 直采上游 code
            finalCode = upstreamCode;
        }

        Configuration configuration = Configuration.createFromUpstream(
                finalCode, name, nameLocal, variantCode, description,
                cmd.getOccurredAt(), null,
                sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
        Configuration saved = configurationRepository.save(configuration);
        outboxService.publishConfigurationCreatedEvent(saved);

        // 在 ingestionLog 的 errorMessage 字段记录 code 决策结果（便于排查；非 ERROR 状态）
        String decisionNote;
        if (codeOverride) {
            decisionNote = String.format("codeOverride=true, upstreamCode=%s, finalCode=%s", upstreamCode, finalCode);
        } else if (upstreamCode == null) {
            decisionNote = "code generated by system: " + finalCode;
        } else {
            decisionNote = "code adopted from upstream: " + finalCode;
        }
        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.CONFIGURATION, saved.getCode(), ingestionChannel,
                IngestionStatus.SUCCESS, null, decisionNote, payloadHash);

        return IngestionResult.builder().entityId(saved.getId())
                .version(saved.getVersion()).operationType("CREATED").build();
    }
}
