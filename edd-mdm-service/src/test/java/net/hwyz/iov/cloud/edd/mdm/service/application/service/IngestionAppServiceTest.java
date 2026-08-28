package net.hwyz.iov.cloud.edd.mdm.service.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.hwyz.iov.cloud.edd.mdm.service.application.dto.cmd.IngestCmd;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.IngestionAuthService;
import net.hwyz.iov.cloud.edd.mdm.service.application.port.service.OutboxService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.exception.IngestionSchemaException;
import net.hwyz.iov.cloud.edd.mdm.service.domain.model.aggregate.Configuration;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.BrandRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.CarLineRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ConfigurationRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.IngestionLogRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.ModelRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.PlatformRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.repository.VariantRepository;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.AuthoritativeSourceService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.IngestionDomainService;
import net.hwyz.iov.cloud.edd.mdm.service.domain.service.ProductDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * 数据接入应用服务单元测试（CR-033：Configuration name/nameLocal 长度校验）
 *
 * @author hwyz_leo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("IngestionAppService 测试")
class IngestionAppServiceTest {

    @Mock
    private IngestionAuthService ingestionAuthService;
    @Mock
    private AuthoritativeSourceService authoritativeSourceService;
    @Mock
    private IngestionDomainService ingestionDomainService;
    @Mock
    private ProductDomainService productDomainService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CarLineRepository carLineRepository;
    @Mock
    private PlatformRepository platformRepository;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private VariantRepository variantRepository;
    @Mock
    private ConfigurationRepository configurationRepository;
    @Mock
    private OutboxService outboxService;
    @Mock
    private IngestionLogRepository ingestionLogRepository;

    private IngestionAppService ingestionAppService;

    @BeforeEach
    void setUp() {
        ingestionAppService = new IngestionAppService(
                ingestionAuthService,
                authoritativeSourceService,
                ingestionDomainService,
                productDomainService,
                brandRepository,
                carLineRepository,
                platformRepository,
                modelRepository,
                variantRepository,
                configurationRepository,
                outboxService,
                ingestionLogRepository,
                new ObjectMapper()
        );
    }

    @Nested
    @DisplayName("Configuration 上游接入 name/nameLocal 长度校验测试（CR-033）")
    class ConfigurationNameLengthIngestTests {

        private IngestCmd buildIngestCmd(String name, String nameLocal) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", name);
            payload.put("nameLocal", nameLocal);
            payload.put("variantCode", "VAR001");
            return IngestCmd.builder()
                    .sourceSystem("PLM")
                    .sourceId("SRC-001")
                    .sourceVersion("v1")
                    .entityType("CONFIGURATION")
                    .occurredAt(new Date())
                    .ingestionChannel("FEIGN")
                    .messageId("msg-001")
                    .payload(payload)
                    .build();
        }

        @Test
        @DisplayName("name 513 字符被拒，抛 schema 异常且不触发任何持久化")
        void ingest_configurationName513_rejected() {
            // Given
            IngestCmd cmd = buildIngestCmd("n".repeat(Configuration.NAME_MAX_LENGTH + 1), "本地名称");

            // When
            assertThrows(IngestionSchemaException.class, () -> ingestionAppService.ingest(cmd, "api-key"));

            // Then：未进入配置仓储（主表/history/outbox 均无写入）
            verifyNoInteractions(configurationRepository);
            verifyNoInteractions(outboxService);
        }

        @Test
        @DisplayName("nameLocal 513 字符被拒，抛 schema 异常且不触发任何持久化")
        void ingest_configurationNameLocal513_rejected() {
            // Given
            IngestCmd cmd = buildIngestCmd("正常名称", "n".repeat(Configuration.NAME_MAX_LENGTH + 1));

            // When
            assertThrows(IngestionSchemaException.class, () -> ingestionAppService.ingest(cmd, "api-key"));

            // Then：未进入配置仓储（主表/history/outbox 均无写入）
            verifyNoInteractions(configurationRepository);
            verifyNoInteractions(outboxService);
        }

        @Test
        @DisplayName("name 512 字符校验通过，进入正常新增链路")
        void ingest_configurationName512_passes() {
            // Given
            IngestCmd cmd = buildIngestCmd("n".repeat(Configuration.NAME_MAX_LENGTH), "本地名称");
            when(ingestionDomainService.computePayloadHash(anyString())).thenReturn("hash");
            when(configurationRepository.findBySourceSystemAndSourceId(anyString(), anyString()))
                    .thenReturn(Optional.empty());
            when(productDomainService.generateConfigurationCode(anyString())).thenReturn("VAR0010000001");
            when(configurationRepository.save(any(Configuration.class), anyString()))
                    .thenReturn(Configuration.builder().code("VAR0010000001").version(1).build());

            // When
            Object result = ingestionAppService.ingest(cmd, "api-key");

            // Then：校验通过并完成新增（未抛异常）
            assertNotNull(result);
            verify(configurationRepository).save(any(Configuration.class), anyString());
            verify(outboxService).publishConfigurationCreatedEvent(any(Configuration.class));
        }
    }
}
