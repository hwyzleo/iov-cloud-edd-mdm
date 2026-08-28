package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config.MdmKafkaTopicProvisioningProperties;
import net.hwyz.iov.cloud.framework.kafka.topic.KafkaTopicDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MDM Kafka Topic 定义提供者单元测试
 * <p>
 * 验证全部 mdm.* Topic 声明（MDM-DSN-CR-034 §5）：
 * Product / Party / EEAD / Org / Material 全覆盖，无遗漏、无多余、无重复；
 * carLine 被声明，series 不被声明。
 *
 * @author hwyz_leo
 */
@DisplayName("MdmKafkaTopicDefinitionProvider 测试")
class MdmKafkaTopicDefinitionProviderTest {

    private MdmKafkaTopicDefinitionProvider provider;

    @BeforeEach
    void setUp() {
        MdmKafkaTopicProvisioningProperties properties = new MdmKafkaTopicProvisioningProperties();
        properties.setPartitions(3);
        properties.setReplicationFactor((short) 3);
        provider = new MdmKafkaTopicDefinitionProvider(properties);
    }

    private Set<String> declaredTopics() {
        Collection<KafkaTopicDefinition> definitions = provider.topicDefinitions();
        return definitions.stream().map(KafkaTopicDefinition::name).collect(Collectors.toSet());
    }

    @Nested
    @DisplayName("Product 子域多 topic")
    class ProductTopicTests {

        @Test
        @DisplayName("8 个实体各声明 created/updated/deactivated")
        void declaresAllProductEntityEvents() {
            Set<String> topics = declaredTopics();
            List<String> entities = List.of("brand", "carLine", "platform", "model", "variant",
                    "configuration", "optionFamily", "optionCode");
            List<String> events = List.of("created", "updated", "deactivated");

            for (String entity : entities) {
                for (String event : events) {
                    assertTrue(topics.contains("mdm.product." + entity + "." + event),
                            "缺少 Product topic: mdm.product." + entity + "." + event);
                }
            }
        }

        @Test
        @DisplayName("carLine 被声明，series 不被声明")
        void declaresCarLineNotSeries() {
            Set<String> topics = declaredTopics();
            assertTrue(topics.contains("mdm.product.carLine.created"));
            assertFalse(topics.contains("mdm.product.series.created"));
            assertFalse(topics.contains("mdm.product.series.updated"));
            assertFalse(topics.contains("mdm.product.series.deactivated"));
        }
    }

    @Nested
    @DisplayName("Party 子域多 topic")
    class PartyTopicTests {

        @Test
        @DisplayName("supplier 声明 created/updated/deactivated")
        void declaresAllSupplierEvents() {
            Set<String> topics = declaredTopics();
            assertTrue(topics.contains("mdm.party.supplier.created"));
            assertTrue(topics.contains("mdm.party.supplier.updated"));
            assertTrue(topics.contains("mdm.party.supplier.deactivated"));
        }
    }

    @Nested
    @DisplayName("固定 topic（EEAD / Org / Material）")
    class FixedTopicTests {

        @Test
        @DisplayName("声明全部固定 topic")
        void declaresAllFixedTopics() {
            Set<String> topics = declaredTopics();
            List<String> fixed = List.of(
                    "mdm.eead.vehicleNode.event",
                    "mdm.eead.deviceCategory.event",
                    "mdm.eead.swinScheme.event",
                    "mdm.eead.swin.event",
                    "mdm.eead.typeApprovalBaseline.event",
                    "mdm.eead.rxswin.event",
                    "mdm.org.plant.event",
                    "mdm.material.part.event",
                    "mdm.material.category.event",
                    "mdm.material.softwareBaseline.event");
            for (String topic : fixed) {
                assertTrue(topics.contains(topic), "缺少固定 topic: " + topic);
            }
        }

        @Test
        @DisplayName("不声明 upstream.* / DLQ / 清单外 Topic")
        void doesNotDeclareOutOfScopeTopics() {
            Set<String> topics = declaredTopics();
            assertTrue(topics.stream().noneMatch(t -> t.startsWith("upstream.")),
                    "不应声明 upstream.* Topic");
            assertTrue(topics.stream().noneMatch(t -> t.contains(".dlq") || t.contains("DLQ")),
                    "不应声明 DLQ Topic");
            assertTrue(topics.stream().allMatch(t -> t.startsWith("mdm.")),
                    "只应声明 mdm.* Topic");
        }
    }

    @Nested
    @DisplayName("覆盖完整性")
    class CoverageTests {

        @Test
        @DisplayName("总计 37 个 topic，无重复")
        void declaresExpectedTotalWithNoDuplicates() {
            Collection<KafkaTopicDefinition> definitions = provider.topicDefinitions();
            Set<String> names = declaredTopics();
            assertEquals(37, names.size(), "MDM 应声明 37 个 topic");
            assertEquals(definitions.size(), names.size(), "声明不应包含重复 topic");
        }

        @Test
        @DisplayName("全部 Definition 携带环境注入的分区与副本数")
        void definitionsCarryConfiguredPartitionsAndReplication() {
            for (KafkaTopicDefinition def : provider.topicDefinitions()) {
                assertEquals(3, def.partitions());
                assertEquals((short) 3, def.replicationFactor());
            }
        }
    }
}
