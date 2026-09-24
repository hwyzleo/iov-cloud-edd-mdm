package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * KafkaTopicResolver 单元测试（MDM-DSN-CR-041 §9.1）
 * <p>
 * 验证 19 个聚合类型到 Kafka Topic 目录的显式映射：
 * - 全量映射精确匹配目录基线
 * - 未登记聚合类型抛配置错误（禁止回退到类名 / eventType 推导）
 *
 * @author hwyz_leo
 */
@DisplayName("KafkaTopicResolver 测试")
class KafkaTopicResolverTest {

    private KafkaTopicResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new KafkaTopicResolver(new MdmKafkaTopicProperties());
    }

    @Nested
    @DisplayName("19 条显式映射")
    class CatalogMappingTests {

        @Test
        @DisplayName("Product 子域映射")
        void resolvesProductDomain() {
            assertEquals("mdm.brand", resolver.resolve("BRAND", "BrandCreated"));
            assertEquals("mdm.car-line", resolver.resolve("CAR_LINE", "CarLineUpdated"));
            assertEquals("mdm.platform", resolver.resolve("PLATFORM", "PlatformDeactivated"));
            assertEquals("mdm.model", resolver.resolve("MODEL", "ModelCreated"));
            assertEquals("mdm.variant", resolver.resolve("VARIANT", "VariantUpdated"));
            assertEquals("mdm.configuration", resolver.resolve("CONFIGURATION", "ConfigurationDeactivated"));
            assertEquals("mdm.option-family", resolver.resolve("OPTION_FAMILY", "OptionFamilyCreated"));
            assertEquals("mdm.option-code", resolver.resolve("OPTION_CODE", "OptionCodeUpdated"));
        }

        @Test
        @DisplayName("Party 子域映射")
        void resolvesPartyDomain() {
            assertEquals("mdm.supplier", resolver.resolve("SUPPLIER", "SupplierCreated"));
        }

        @Test
        @DisplayName("EEAD 子域映射")
        void resolvesEeadDomain() {
            assertEquals("mdm.vehicle-node", resolver.resolve("VEHICLE_NODE", "VehicleNodeCreated"));
            assertEquals("mdm.device-category", resolver.resolve("DEVICE_CATEGORY", "DeviceCategoryDeleted"));
            assertEquals("mdm.swin-scheme", resolver.resolve("SWIN_SCHEME", "SwinSchemeCreated"));
            assertEquals("mdm.swin-definition", resolver.resolve("SWIN_DEFINITION", "SwinDefinitionUpdated"));
            assertEquals("mdm.rxswin", resolver.resolve("RXSWIN_REGISTRY", "RxswinRegistryCreated"));
            assertEquals("mdm.type-approval-baseline", resolver.resolve("TYPE_APPROVAL_BASELINE", "TypeApprovalBaselineReleased"));
        }

        @Test
        @DisplayName("Org / Material 子域映射")
        void resolvesOrgAndMaterialDomain() {
            assertEquals("mdm.plant", resolver.resolve("PLANT", "PlantCreated"));
            assertEquals("mdm.material-category", resolver.resolve("MATERIAL_CATEGORY", "MaterialCategoryCreated"));
            assertEquals("mdm.part", resolver.resolve("PART", "PartUpdated"));
            assertEquals("mdm.software-baseline", resolver.resolve("SOFTWARE_BASELINE", "SoftwareBaselineReleased"));
        }

        @Test
        @DisplayName("事件类型不参与路由推导，映射与 eventType 无关")
        void mappingIndependentOfEventType() {
            assertEquals("mdm.brand", resolver.resolve("BRAND", "任意事件类型"));
            assertEquals("mdm.vehicle-node", resolver.resolve("VEHICLE_NODE", "AnyEventType"));
        }
    }

    @Nested
    @DisplayName("未登记聚合类型")
    class UnknownAggregateTests {

        @Test
        @DisplayName("未登记聚合类型抛配置错误，禁止回退推导")
        void unknownAggregateType_throwsConfigError() {
            assertThrows(MdmKafkaTopicConfigException.class,
                    () -> resolver.resolve("UNKNOWN_ENTITY", "WhateverHappened"));
            assertThrows(MdmKafkaTopicConfigException.class,
                    () -> resolver.resolve(null, "BrandCreated"));
        }
    }
}
