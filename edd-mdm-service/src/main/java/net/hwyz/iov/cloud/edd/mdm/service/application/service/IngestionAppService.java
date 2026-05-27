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
        String code = extractCodeFromPayload(cmd.getPayload());
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

    private String extractCodeFromPayload(Map<String, Object> payload) {
        Object code = payload.get("code");
        if (code == null) {
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

    private IngestionResult processConfiguration(IngestCmd cmd, String code, String payloadHash) {
        String sourceSystem = cmd.getSourceSystem();
        String sourceId = cmd.getSourceId();
        String sourceVersion = cmd.getSourceVersion();
        String ingestionChannel = cmd.getIngestionChannel();
        String messageId = cmd.getMessageId();
        Map<String, Object> payload = cmd.getPayload();

        Configuration existing = configurationRepository.findByCode(code).orElse(null);
        String localVersion = existing != null ? existing.getSourceVersion() : null;
        String localHash = existing != null ? existing.getSourcePayloadHash() : null;

        IngestionStatus idempotentResult = ingestionDomainService.checkIdempotent(
                sourceSystem, sourceId, sourceVersion, localVersion, payloadHash, localHash);

        if (idempotentResult == IngestionStatus.DUPLICATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.CONFIGURATION, code, ingestionChannel, IngestionStatus.DUPLICATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("DUPLICATED").build();
        }
        if (idempotentResult == IngestionStatus.OUTDATED) {
            ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                    EntityType.CONFIGURATION, code, ingestionChannel, IngestionStatus.OUTDATED, null, null, payloadHash);
            return IngestionResult.builder().entityId(existing != null ? existing.getId() : null)
                    .version(existing != null ? existing.getVersion() : null).operationType("OUTDATED").build();
        }

        String name = (String) payload.get("name");
        String nameLocal = (String) payload.get("nameLocal");
        String variantCode = (String) payload.get("variantCode");
        String description = (String) payload.get("description");

        Configuration configuration;
        String operationType;
        if (existing == null) {
            configuration = Configuration.createFromUpstream(code, name, nameLocal, variantCode, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            configuration = configurationRepository.save(configuration);
            outboxService.publishConfigurationCreatedEvent(configuration);
            operationType = "CREATED";
        } else {
            existing.updateFromUpstream(name, nameLocal, description,
                    cmd.getOccurredAt(), null,
                    sourceSystem, sourceId, sourceVersion, ingestionChannel, payloadHash, sourceSystem);
            configuration = configurationRepository.save(existing);
            outboxService.publishConfigurationUpdatedEvent(configuration);
            operationType = "UPDATED";
        }

        ingestionDomainService.logIngestion(messageId, sourceSystem, sourceId, sourceVersion,
                EntityType.CONFIGURATION, code, ingestionChannel, IngestionStatus.SUCCESS, null, null, payloadHash);

        return IngestionResult.builder().entityId(configuration.getId()).version(configuration.getVersion()).operationType(operationType).build();
    }
}
