package net.hwyz.iov.cloud.edd.mdm.service.infrastructure.config;

import net.hwyz.iov.cloud.edd.mdm.service.infrastructure.messaging.kafka.MdmKafkaTopicConfigException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MdmKafkaTopicProperties 单元测试（MDM-DSN-CR-041 §9.1）
 * <p>
 * 验证 19 个生产 Topic 配置键与值精确匹配 Kafka Topic 目录基线；
 * 空值 / 重复名称 / 缺键校验失败；生产与消费 Topic 集合正确分离。
 *
 * @author hwyz_leo
 */
@DisplayName("MdmKafkaTopicProperties 测试")
class MdmKafkaTopicPropertiesTest {

    private static final List<String> CATALOG_TOPICS = List.of(
            "mdm.brand", "mdm.car-line", "mdm.configuration", "mdm.device-category",
            "mdm.material-category", "mdm.model", "mdm.option-code", "mdm.option-family",
            "mdm.part", "mdm.plant", "mdm.platform", "mdm.rxswin", "mdm.software-baseline",
            "mdm.supplier", "mdm.swin-definition", "mdm.swin-scheme", "mdm.type-approval-baseline",
            "mdm.variant", "mdm.vehicle-node");

    private static final List<String> SEMANTIC_KEYS = List.of(
            "brand", "car-line", "configuration", "device-category", "material-category",
            "model", "option-code", "option-family", "part", "plant", "platform", "rxswin",
            "software-baseline", "supplier", "swin-definition", "swin-scheme",
            "type-approval-baseline", "variant", "vehicle-node");

    @Nested
    @DisplayName("目录基线一致性")
    class CatalogBaselineTests {

        @Test
        @DisplayName("默认配置 19 个生产 Topic 精确匹配目录基线")
        void defaults_matchCatalogBaseline() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            properties.validate();
            assertEquals(CATALOG_TOPICS, properties.producerTopics(),
                    "生产 Topic 清单应精确匹配 Kafka Topic 目录");
        }

        @Test
        @DisplayName("语义键集合与目录基线一致")
        void semanticKeys_matchBaselineKeys() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            assertEquals(SEMANTIC_KEYS, List.copyOf(properties.producerTopicsByKey().keySet()),
                    "语义键集合应精确匹配目录基线键集合");
        }

        @Test
        @DisplayName("当前无消费 Topic，consumerTopics 为空")
        void consumerTopics_isEmpty() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            assertTrue(properties.consumerTopics().isEmpty(), "当前 EDD-MDM 无消费 Topic");
        }

        @Test
        @DisplayName("按语义键解析返回目录 Topic 名称")
        void producerTopic_resolvesByKey() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            assertEquals("mdm.brand", properties.producerTopic("brand"));
            assertEquals("mdm.vehicle-node", properties.producerTopic("vehicle-node"));
            assertEquals("mdm.type-approval-baseline", properties.producerTopic("type-approval-baseline"));
        }
    }

    @Nested
    @DisplayName("配置校验")
    class ValidationTests {

        @Test
        @DisplayName("空值校验失败")
        void blankTopicName_failsValidation() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            properties.setBrand(" ");
            assertThrows(MdmKafkaTopicConfigException.class, properties::validate);
        }

        @Test
        @DisplayName("重复名称校验失败")
        void duplicateTopicName_failsValidation() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            properties.setBrand("mdm.car-line");
            assertThrows(MdmKafkaTopicConfigException.class, properties::validate);
        }

        @Test
        @DisplayName("非法格式名称校验失败")
        void invalidFormatTopicName_failsValidation() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            properties.setBrand("mdm.brand with space!");
            assertThrows(MdmKafkaTopicConfigException.class, properties::validate);
        }

        @Test
        @DisplayName("未知语义键解析抛配置错误（禁止回退推导）")
        void unknownKey_resolutionThrows() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            assertThrows(MdmKafkaTopicConfigException.class, () -> properties.producerTopic("not-a-key"));
        }

        @Test
        @DisplayName("覆盖为合法目录内名称（键间交换）时校验通过")
        void overrideWithCatalogName_passesValidation() {
            MdmKafkaTopicProperties properties = new MdmKafkaTopicProperties();
            // 键间交换目录名称，名称集合仍唯一，校验应通过
            properties.setBrand("mdm.car-line");
            properties.setCarLine("mdm.brand");
            assertDoesNotThrow(properties::validate);
        }
    }
}
