package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * KafkaTopicResolver 单元测试
 * <p>
 * 验证基于 Catalog 的 topic 路由解析（MDM-DSN-CR-034）：
 * - 单 topic 子域固定映射与 Catalog 双向一致
 * - Product / Party eventType 即 topic
 * - 未登记事件被拒绝并告警
 * - Provisioning 停用（Catalog 缺失）走兼容路径
 *
 * @author hwyz_leo
 */
@DisplayName("KafkaTopicResolver 测试")
class KafkaTopicResolverTest {

    private KafkaTopicCatalog catalog;
    private KafkaTopicResolver resolver;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        catalog = mock(KafkaTopicCatalog.class);
        ObjectProvider<KafkaTopicCatalog> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(catalog);
        resolver = new KafkaTopicResolver(provider);
    }

    @Nested
    @DisplayName("单 topic 子域固定映射")
    class SingleTopicMappingTests {

        @Test
        @DisplayName("EEAD 实体映射到各自固定 topic")
        void resolvesEeadFixedTopics() {
            when(catalog.contains(anyString())).thenReturn(true);
            assertEquals(Optional.of("mdm.eead.vehicleNode.event"),
                    resolver.resolve("VEHICLE_NODE", "VehicleNodeCreated"));
            assertEquals(Optional.of("mdm.eead.deviceCategory.event"),
                    resolver.resolve("DEVICE_CATEGORY", "DeviceCategoryCreated"));
            assertEquals(Optional.of("mdm.eead.swin.event"),
                    resolver.resolve("SWIN_DEFINITION", "SwinDefinitionCreated"));
            assertEquals(Optional.of("mdm.eead.swinScheme.event"),
                    resolver.resolve("SWIN_SCHEME", "SwinSchemeCreated"));
            assertEquals(Optional.of("mdm.eead.rxswin.event"),
                    resolver.resolve("RXSWIN_REGISTRY", "RxswinRegistryCreated"));
            assertEquals(Optional.of("mdm.eead.typeApprovalBaseline.event"),
                    resolver.resolve("TYPE_APPROVAL_BASELINE", "TypeApprovalBaselineCreated"));
        }

        @Test
        @DisplayName("Org / Material 实体映射到各自固定 topic")
        void resolvesOrgAndMaterialFixedTopics() {
            when(catalog.contains(anyString())).thenReturn(true);
            assertEquals(Optional.of("mdm.org.plant.event"),
                    resolver.resolve("PLANT", "PlantCreated"));
            assertEquals(Optional.of("mdm.material.part.event"),
                    resolver.resolve("PART", "PartCreated"));
            assertEquals(Optional.of("mdm.material.category.event"),
                    resolver.resolve("MATERIAL_CATEGORY", "MaterialCategoryCreated"));
            assertEquals(Optional.of("mdm.material.softwareBaseline.event"),
                    resolver.resolve("SOFTWARE_BASELINE", "SoftwareBaselineReleased"));
        }

        @Test
        @DisplayName("固定映射与 Catalog 双向一致：Catalog 缺失时被拒绝")
        void rejectsFixedTopicMissingFromCatalog() {
            when(catalog.contains("mdm.eead.vehicleNode.event")).thenReturn(false);
            assertTrue(resolver.resolve("VEHICLE_NODE", "VehicleNodeCreated").isEmpty());
        }
    }

    @Nested
    @DisplayName("多 topic 子域（Product / Party）")
    class MultiTopicTests {

        @Test
        @DisplayName("eventType 即 topic，Catalog 包含时解析成功")
        void resolvesEventTypeAsTopicWhenDeclared() {
            when(catalog.contains("mdm.product.brand.created")).thenReturn(true);
            assertEquals(Optional.of("mdm.product.brand.created"),
                    resolver.resolve("BRAND", "mdm.product.brand.created"));
            when(catalog.contains("mdm.party.supplier.updated")).thenReturn(true);
            assertEquals(Optional.of("mdm.party.supplier.updated"),
                    resolver.resolve("SUPPLIER", "mdm.party.supplier.updated"));
        }

        @Test
        @DisplayName("未登记 eventType 被拒绝并返回 empty")
        void rejectsUndeclaredEventType() {
            when(catalog.contains("unknown.event")).thenReturn(false);
            assertTrue(resolver.resolve("BRAND", "unknown.event").isEmpty());
        }

        @Test
        @DisplayName("eventType 即 topic 但不在 Catalog 中被拒绝")
        void rejectsEventTypeNotInCatalog() {
            when(catalog.contains(anyString())).thenReturn(false);
            assertTrue(resolver.resolve("BRAND", "mdm.product.brand.created").isEmpty());
        }
    }

    @Nested
    @DisplayName("Provisioning 停用兼容路径")
    class DisabledCompatibilityTests {

        @Test
        @DisplayName("Catalog 缺失时不校验，走既有解析路径")
        void resolvesWithoutCatalogWhenDisabled() {
            ObjectProvider<KafkaTopicCatalog> emptyProvider = mock(ObjectProvider.class);
            when(emptyProvider.getIfAvailable()).thenReturn(null);
            KafkaTopicResolver legacyResolver = new KafkaTopicResolver(emptyProvider);

            assertEquals(Optional.of("mdm.eead.vehicleNode.event"),
                    legacyResolver.resolve("VEHICLE_NODE", "VehicleNodeCreated"));
            assertEquals(Optional.of("mdm.product.brand.created"),
                    legacyResolver.resolve("BRAND", "mdm.product.brand.created"));
        }
    }
}
