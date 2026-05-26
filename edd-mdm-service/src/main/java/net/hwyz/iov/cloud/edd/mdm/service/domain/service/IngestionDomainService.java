package net.hwyz.iov.cloud.edd.mdm.service.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.hwyz.iov.cloud.edd.mdm.service.common.enums.IngestionStatus;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.IngestionSchemaException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.IngestionVersionConflictException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.entity.IngestionLog;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.valueobject.EntityType;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.IngestionLogRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionDomainService {

    private final IngestionLogRepository ingestionLogRepository;

    public void validateSchema(String sourceSystem, String sourceId, String sourceVersion,
                               String entityType, String payload) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            throw new IngestionSchemaException("sourceSystem不能为空");
        }
        if (sourceId == null || sourceId.isBlank()) {
            throw new IngestionSchemaException("sourceId不能为空");
        }
        if (sourceVersion == null || sourceVersion.isBlank()) {
            throw new IngestionSchemaException("sourceVersion不能为空");
        }
        if (entityType == null || entityType.isBlank()) {
            throw new IngestionSchemaException("entityType不能为空");
        }
        if (payload == null || payload.isBlank()) {
            throw new IngestionSchemaException("payload不能为空");
        }
    }

    public IngestionStatus checkIdempotent(String sourceSystem, String sourceId,
                                            String upstreamVersion, String localVersion,
                                            String upstreamHash, String localHash) {
        if (localVersion == null) {
            return null; // new record
        }

        int cmp = compareVersion(upstreamVersion, localVersion);
        if (cmp < 0) {
            log.info("过期消息丢弃: source={}, sourceId={}, upstream={}, local={}",
                    sourceSystem, sourceId, upstreamVersion, localVersion);
            return IngestionStatus.OUTDATED;
        } else if (cmp == 0) {
            if (upstreamHash != null && upstreamHash.equals(localHash)) {
                log.info("重复消息丢弃: source={}, sourceId={}, version={}",
                        sourceSystem, sourceId, upstreamVersion);
                return IngestionStatus.DUPLICATED;
            } else {
                throw new IngestionVersionConflictException(
                        String.format("同版本冲突: source=%s, sourceId=%s, version=%s",
                                sourceSystem, sourceId, upstreamVersion));
            }
        }
        return null; // newer version, proceed
    }

    public IngestionLog logIngestion(String messageId, String sourceSystem, String sourceId,
                                      String sourceVersion, EntityType entityType, String entityCode,
                                      String ingestionChannel, IngestionStatus status,
                                      String errorCode, String errorMessage, String payloadHash) {
        IngestionLog logEntry = IngestionLog.builder()
                .messageId(messageId).sourceSystem(sourceSystem).sourceId(sourceId)
                .sourceVersion(sourceVersion).entityType(entityType).entityCode(entityCode)
                .ingestionChannel(ingestionChannel).receivedAt(new Date()).processedAt(new Date())
                .status(status).errorCode(errorCode).errorMessage(errorMessage).payloadHash(payloadHash)
                .createBy(sourceSystem).createTime(new Date())
                .modifyBy(sourceSystem).modifyTime(new Date())
                .rowVersion(0).rowValid(true)
                .build();
        return ingestionLogRepository.save(logEntry);
    }

    public String computePayloadHash(String payload) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private int compareVersion(String v1, String v2) {
        if (v1 == null || v2 == null) return 0;
        try {
            return Long.compare(Long.parseLong(v1), Long.parseLong(v2));
        } catch (NumberFormatException e) {
            return v1.compareTo(v2);
        }
    }
}
